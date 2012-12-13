package ru.fizteh.fivt.students.altimin.proxy;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;
import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * User: altimin
 * Date: 12/12/12
 * Time: 8:50 PM
 */
public class AsmShardingProxyFactory implements ru.fizteh.fivt.proxy.ShardingProxyFactory {
    private final String className = "Proxy";

    @Override
    public Object createProxy(Object[] targets, Class[] interfaces) {
        ProxyUtils.check(targets, interfaces);
        byte[] byteCode = generateByteCode(targets, interfaces);
        try {
            Class clazz = loadClass(byteCode);
            return clazz.getConstructor(ArrayList.class).newInstance(new ArrayList(Arrays.asList(targets)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create proxy object", e);
        } catch (VerifyError e) {
            throw new RuntimeException("Failed to create proxy object", e);
        }
    }

    private static Class<?> loadClass(byte[] bytes) {
        class LocalClassLoader extends ClassLoader {
            public Class<?> defineClass(byte[] bytes) {
                return super.defineClass(null, bytes, 0, bytes.length);
            }
        }
        return new LocalClassLoader().defineClass(bytes);
    }

    private byte[] generateByteCode(Object[] targets, Class[] interfaces) {
        ClassWriter cw = getClassWriter();
        String[] interfacesInternalNames = new String[interfaces.length];
        for (int i = 0; i < interfaces.length; i ++) {
            interfacesInternalNames[i] = Type.getInternalName(interfaces[i]);
        }
        cw.visit(
                Opcodes.V1_7,
                Opcodes.ACC_PUBLIC,
                className,
                null,
                Type.getInternalName(Object.class),
                interfacesInternalNames
        ); // create class header
        cw.visitField(
                Opcodes.ACC_PRIVATE,
                "targets",
                Type.getDescriptor(ArrayList.class),
                null,
                null
        ).visitEnd(); // create local field for targets storage
        generateConstructor(cw);
        Set<String> proceedMethods = new HashSet<String>();
        for (Class iface: interfaces) {
            for (java.lang.reflect.Method method: iface.getDeclaredMethods()) {
                String description = ProxyUtils.getMethodDescription(method);
                if (!proceedMethods.contains(description)) {
                    generateMethod(cw, method, targets.length, iface);
                } else {
                    proceedMethods.add(description);
                }
            }
        }
        //cw.visitEnd();
        return cw.toByteArray();
    }

    private void throwRuntimeException(GeneratorAdapter ga, String exceptionMessage) {
        ga.throwException(Type.getType(RuntimeException.class), exceptionMessage);
    }

    private boolean isIntOrLongClass(Class clazz) {
        return clazz.equals(int.class)
                || clazz.equals(Integer.class)
                || clazz.equals(long.class)
                || clazz.equals(Long.class);
    }

    private int getIndexOfTargetIndex(java.lang.reflect.Method method) {
        Class[] classes = method.getParameterTypes();
        int result = 0;
        while (result < classes.length && !isIntOrLongClass(classes[result])) {
            result ++;
        }
        if (result == classes.length) {
            throw new RuntimeException("No int or long class in method parameters");
        }
        return result;
    }



    private void generateMethod(
            ClassWriter cw,
            final java.lang.reflect.Method method,
            final int targetsLength,
            final Class iface)
    {
        final Type typeProxy = Type.getType("L" + className + ";");
        final Type typeArrayList = Type.getType(ArrayList.class);
        generateMethod(
                cw,
                Opcodes.ACC_PUBLIC,
                method.getName(),
                Type.getMethodDescriptor(method),
                new Function1V<GeneratorAdapter>() {
                    Type typeIface = Type.getType(iface);

                    private void getArrayElement(GeneratorAdapter ga, int i) {
                        ga.loadThis();
                        ga.getField(typeProxy, "targets", typeArrayList);
                        ga.push(i);
                        try {
                            ga.invokeVirtual(
                                    typeArrayList,
                                    new Method("get", Type.getMethodDescriptor(ArrayList.class.getMethod("get", int.class))));
                        } catch (NoSuchMethodException e) {
                            throw new RuntimeException("ArrayList doesn't have method get(int).");
                        }
                    }

                    private void invokeInterface(GeneratorAdapter ga) {
                        ga.invokeInterface(typeIface, new Method(method.getName(), Type.getMethodDescriptor(method)));
                    }

                    @Override
                    public void apply(GeneratorAdapter ga) {
                        if (method.isAnnotationPresent(DoNotProxy.class)) {
                            throwRuntimeException(ga, "It's impossible to call method with @DoNotProxy annotation");
                            return;
                        }
                        if (method.isAnnotationPresent(Collect.class)) {
                            Class returnType = method.getReturnType();
                            if (returnType.equals(void.class) || returnType.equals(Void.class)) {
                                for (int i = 0; i < targetsLength; i ++) {
                                    getArrayElement(ga, i);
                                    ga.loadArgs();
                                    invokeInterface(ga);
                                }
                                ga.returnValue();
                                return;
                            } else if (returnType.equals(int.class) || returnType.equals(Integer.class)) {
                                ga.push(0);
                                for (int i = 0; i < targetsLength; i ++) {
                                    getArrayElement(ga, i);
                                    ga.loadArgs();
                                    invokeInterface(ga);
                                    if (returnType.equals(Integer.class)) {
                                        ga.unbox(Type.INT_TYPE);
                                    }
                                    ga.math(GeneratorAdapter.ADD, Type.INT_TYPE);
                                }
                                if (returnType.equals(Integer.class)) {
                                    ga.box(Type.INT_TYPE);
                                }
                                ga.returnValue();
                                return;
                            } else if (returnType.equals(long.class) || returnType.equals(Long.class)) {
                                ga.push((long) 0);
                                for (int i = 0; i < targetsLength; i ++) {
                                    getArrayElement(ga, i);
                                    ga.loadArgs();
                                    invokeInterface(ga);
                                    if (returnType.equals(Long.class)) {
                                        ga.unbox(Type.LONG_TYPE);
                                    }
                                    ga.math(GeneratorAdapter.ADD, Type.LONG_TYPE);
                                }
                                if (returnType.equals(Long.class)) {
                                    ga.box(Type.LONG_TYPE);
                                }
                                ga.returnValue();
                                return;
                            } else if (returnType.equals(List.class)) {
                                ga.newInstance(typeArrayList);
                                ga.dup();
                                ga.invokeConstructor(typeArrayList, new Method("<init>", "()V"));
                                for (int i = 0; i < targetsLength; i ++) {
                                    ga.dup();
                                    getArrayElement(ga, i);
                                    ga.loadArgs();
                                    invokeInterface(ga);
                                    try {
                                        ga.invokeVirtual(
                                                Type.getType(ArrayList.class),
                                                new Method("addAll", Type.getMethodDescriptor(ArrayList.class.getMethod("addAll", Collection.class)))
                                        );
                                    } catch (NoSuchMethodException e) {
                                        throw new RuntimeException("ArrayList has no method addAll(Collection)", e);
                                    }
                                    ga.pop();
                                }
                                ga.returnValue();
                                return;
                            }
                            throwRuntimeException(ga, "Class is not collectable");
                        } else {
                            ga.loadThis();
                            ga.getField(typeProxy, "targets", typeArrayList); // get targets for invocation
                            int indexOfTargetIndex = getIndexOfTargetIndex(method);
                            ga.loadArg(indexOfTargetIndex);
                            Class targetIndexType = method.getParameterTypes()[indexOfTargetIndex];
                            if (targetIndexType.equals(Long.class)) {
                                ga.unbox(Type.LONG_TYPE);
                            }
                            if (targetIndexType.equals(Integer.class)) {
                                ga.unbox(Type.INT_TYPE);
                            }
                            if (targetIndexType.equals(int.class) || targetIndexType.equals(Integer.class)) {
                                ga.cast(Type.INT_TYPE, Type.LONG_TYPE);
                            }
                            ga.push((long) targetsLength);
                            ga.math(GeneratorAdapter.REM, Type.LONG_TYPE);
                            ga.cast(Type.LONG_TYPE, Type.INT_TYPE);
                            try {
                                ga.invokeVirtual(
                                        typeArrayList,
                                        new Method("get", Type.getMethodDescriptor(ArrayList.class.getMethod("get", int.class))));
                            } catch (NoSuchMethodException e) {
                                throw new RuntimeException("ArrayList doesn't have method get(int).");
                            }
                            ga.loadArgs();
                            ga.invokeInterface(typeIface, new Method(method.getName(), Type.getMethodDescriptor(method)));
                            ga.returnValue();
                        }
                    }
                }
        );
    }

    private void generateConstructor(ClassWriter cw) {
        generateMethod(
                cw,
                Opcodes.ACC_PUBLIC,
                "<init>",
                "(" + Type.getDescriptor(ArrayList.class) + ")V",
                new Function1V<GeneratorAdapter>() {
                    @Override
                    public void apply(GeneratorAdapter ga) {
                        ga.loadThis();
                        ga.dup();
                        ga.invokeConstructor(
                                Type.getType(Object.class),
                                new Method("<init>", "()V")
                        );
                        ga.loadThis();
                        ga.loadArg(0);
                        ga.putField(
                                Type.getType("L" + className + ";"),
                                "targets",
                                Type.getType(ArrayList.class)
                        );
                        ga.returnValue();
                    }
                }
        );
    }

    private static void generateMethod(ClassWriter cw, int access, String name, String descriptor,
                                       Function1V<GeneratorAdapter> f)
    {
        MethodVisitor mv = cw.visitMethod(access, name, descriptor, null, null);
        GeneratorAdapter ga = new GeneratorAdapter(mv, access, name, descriptor);
        ga.visitCode();
        f.apply(ga);
        ga.endMethod();
    }

    private interface Function1V<T> {
        void apply(T value);
    }

    private ClassWriter getClassWriter() {
        return new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    }
}
