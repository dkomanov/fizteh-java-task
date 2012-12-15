package ru.fizteh.fivt.students.alexanderKuzmin.asmProxy;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.GeneratorAdapter;
import ru.fizteh.fivt.proxy.ShardingProxyFactory;
import ru.fizteh.fivt.students.alexanderKuzmin.asmProxy.ProxySharingClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alexander Kuzmin group 196 Class ShardingAsmProxyFactory
 * 
 */

public class ShardingAsmProxyFactory implements ShardingProxyFactory {

    private ClassWriter newClassWriter() {
        int flags = ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS;
        return new ClassWriter(flags);
    }

    @Override
    public Object createProxy(Object[] targets, Class[] interfaces) {
        ProxySharingClass.throwIncorrectArgument(targets, interfaces);
        ProxySharingClass
                .throwExceptionIfInterfacesContainsEqualsMethodSignature(interfaces);
        String[] interfacesName = new String[interfaces.length];
        for (int i = 0; i < interfaces.length; ++i) {
            interfacesName[i] = Type.getInternalName(interfaces[i]);
        }

        ClassWriter cw = newClassWriter();
        cw.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC, "Proxy", null,
                "java/lang/Object", interfacesName);
        cw.visitField(Opcodes.ACC_PRIVATE, "targets",
                Type.getDescriptor(ArrayList.class), null, null).visitEnd();
        generateConstructor(cw);
        for (Class<?> interfc : interfaces) {
            Method[] methods = interfc.getDeclaredMethods();
            for (Method method : methods) {
                method.setAccessible(true);
                generateInterfaceMethod(cw, interfc, method, targets.length);
            }
        }
        cw.visitEnd();

        try {
            Class<?> clazz = loadClass(cw.toByteArray());
            Constructor<?> constructor = clazz.getConstructor(ArrayList.class);
            constructor.setAccessible(true);
            return constructor
                    .newInstance(new ArrayList(Arrays.asList(targets)));
        } catch (Throwable t) {
            throw new RuntimeException("Cannot create proxy object.", t);
        }
    }

    private Class<?> loadClass(byte[] bytes) {
        class LocalClassLoader extends ClassLoader {
            public Class<?> defineClass(byte[] bytes) {
                return super.defineClass(null, bytes, 0, bytes.length);
            }
        }
        return new LocalClassLoader().defineClass(bytes);
    }

    private static void generateMethod(ClassWriter cw, int access, String name,
            String descriptor, Function1V<GeneratorAdapter> f) {
        MethodVisitor mv = cw.visitMethod(access, name, descriptor, null, null);
        GeneratorAdapter ga = new GeneratorAdapter(mv, access, name, descriptor);
        ga.visitCode();
        f.apply(ga);
        ga.endMethod();
    }

    private interface Function1V<T> {
        void apply(T value);
    }

    private void generateConstructor(ClassWriter cw) {
        generateMethod(cw, Opcodes.ACC_PUBLIC, "<init>",
                "(" + Type.getDescriptor(ArrayList.class) + ")V",
                new Function1V<GeneratorAdapter>() {
                    @Override
                    public void apply(GeneratorAdapter ga) {
                        ga.loadThis();
                        ga.invokeConstructor(Type.getType("java/lang/Object"),
                                new org.objectweb.asm.commons.Method("<init>",
                                        "()V"));
                        ga.loadThis();
                        ga.loadArg(0);
                        ga.putField(Type.getType("Proxy"), "targets",
                                Type.getType(ArrayList.class));
                        ga.returnValue();
                    }
                });
    }

    private void generateInterfaceMethod(ClassWriter cw, Class<?> interfc,
            final Method method, final int targetsCount) {
        final Type arrayListType = Type.getType(ArrayList.class);
        final Type proxyType = Type.getType("Proxy");
        final Type interfaceType = Type.getType(interfc);
        final String descriptor = Type.getMethodDescriptor(method);
        final String name = method.getName();
        final Class<?> methodReturnType = method.getReturnType();

        generateMethod(cw, Opcodes.ACC_PUBLIC, name, descriptor,
                new Function1V<GeneratorAdapter>() {
                    @Override
                    public void apply(GeneratorAdapter ga) {
                        if (ProxySharingClass.isDoNotProxy(method)) {
                            ga.throwException(
                                    Type.getType(IllegalStateException.class),
                                    "This method has DoNotProxy annotation.");
                        }

                        if (!ProxySharingClass.isCollect(method)) {
                            ga.loadThis();
                            ga.getField(proxyType, "targets", arrayListType);
                            int count = getCountOfArgument(method
                                    .getParameterTypes());
                            ga.loadArg(count);
                            if (method.getParameterTypes()[count]
                                    .equals(int.class)) {
                                ga.cast(Type.INT_TYPE, Type.LONG_TYPE);
                            }
                            ga.push((long) targetsCount);
                            ga.math(GeneratorAdapter.REM, Type.LONG_TYPE);
                            ga.cast(Type.LONG_TYPE, Type.INT_TYPE);
                            try {
                                ga.invokeVirtual(arrayListType,
                                        new org.objectweb.asm.commons.Method(
                                                "get", "(I)Ljava/lang/Object;"));
                            } catch (Throwable e) {
                                throw new RuntimeException(
                                        "ArrayList doesn't have the method.");
                            }
                            ga.checkCast(interfaceType);
                            ga.loadArgs();
                            ga.invokeInterface(interfaceType,
                                    new org.objectweb.asm.commons.Method(name,
                                            descriptor));
                        } else {
                            Label forConditionLabel = ga.newLabel();
                            Label forLoopEnd = ga.newLabel();

                            int iLocal = ga.newLocal(Type.INT_TYPE);
                            ga.push(0);
                            ga.storeLocal(iLocal);

                            int sizeLocal = ga.newLocal(Type.INT_TYPE);
                            ga.loadThis();
                            ga.getField(proxyType, "targets", arrayListType);
                            try {
                                ga.invokeVirtual(arrayListType,
                                        new org.objectweb.asm.commons.Method(
                                                "size", "()I"));
                            } catch (Throwable e) {
                                throw new RuntimeException(
                                        "ArrayList doesn't have the method.");
                            }
                            ga.storeLocal(sizeLocal);

                            int resLocal = 0;
                            if (!methodReturnType.equals(void.class)) {
                                resLocal = ga.newLocal(Type
                                        .getType(methodReturnType));
                                if (methodReturnType.equals(int.class)) {
                                    ga.push(0);
                                } else if (methodReturnType
                                        .equals(Integer.class)) {
                                    ga.newInstance(Type.getType(Integer.class));
                                    ga.dup();
                                    ga.push(0);
                                    ga.invokeConstructor(
                                            Type.getType(Integer.class),
                                            new org.objectweb.asm.commons.Method(
                                                    "<init>", "(I)V"));
                                } else if (methodReturnType.equals(long.class)) {
                                    ga.push((long) 0);
                                } else if (methodReturnType.equals(Long.class)) {
                                    ga.newInstance(Type.getType(Long.class));
                                    ga.dup();
                                    ga.push((long) 0);
                                    ga.invokeConstructor(
                                            Type.getType(Long.class),
                                            new org.objectweb.asm.commons.Method(
                                                    "<init>", "(J)V"));
                                } else if (methodReturnType.equals(List.class)) {
                                    ga.newInstance(Type
                                            .getType(ArrayList.class));
                                    ga.dup();
                                    ga.invokeConstructor(
                                            arrayListType,
                                            new org.objectweb.asm.commons.Method(
                                                    "<init>", "()V"));
                                } else {
                                    throw new IllegalStateException(
                                            "Incorrect return type of method.");
                                }
                                ga.storeLocal(resLocal);
                            }

                            ga.visitLabel(forConditionLabel);
                            ga.loadLocal(iLocal);
                            ga.loadLocal(sizeLocal);
                            ga.ifCmp(Type.INT_TYPE, Opcodes.IFGE, forLoopEnd);

                            ga.loadThis();
                            ga.getField(proxyType, "targets", arrayListType);
                            ga.loadLocal(iLocal);
                            try {
                                ga.invokeVirtual(arrayListType,
                                        new org.objectweb.asm.commons.Method(
                                                "get", "(I)Ljava/lang/Object;"));
                            } catch (Throwable e) {
                                throw new RuntimeException(
                                        "ArrayList doesn't have the method.");
                            }
                            ga.checkCast(interfaceType);
                            ga.loadArgs();
                            ga.invokeInterface(interfaceType,
                                    new org.objectweb.asm.commons.Method(name,
                                            descriptor));

                            if (!methodReturnType.equals(void.class)) {
                                Type returnType = Type
                                        .getType(methodReturnType);
                                ga.loadLocal(resLocal);
                                ga.swap(returnType, returnType);
                                String returnTypeDescriptor = returnType
                                        .getDescriptor();
                                final org.objectweb.asm.commons.Method add = new org.objectweb.asm.commons.Method(
                                        "add", "(" + returnTypeDescriptor
                                                + returnTypeDescriptor + ")"
                                                + returnTypeDescriptor);
                                ga.invokeStatic(
                                        Type.getType(ShardingAsmProxyFactory.class),
                                        add);
                                ga.storeLocal(resLocal);
                            }

                            ga.iinc(iLocal, 1);
                            ga.goTo(forConditionLabel);

                            ga.visitLabel(forLoopEnd);

                            if (!methodReturnType.equals(void.class)) {
                                ga.loadLocal(resLocal);
                            }
                        }
                        ga.returnValue();
                    }
                });
    }

    public static int getCountOfArgument(Class<?>[] args) {
        for (int i = 0; i < args.length; ++i) {
            Class<?> clazz = args[i];
            if (clazz.equals(int.class) || clazz.equals(Integer.class)
                    || clazz.equals(long.class) || clazz.equals(Long.class)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Incorrect arguments.");
    }

    public static int add(int a, int b) {
        return a + b;
    }

    public static Integer add(Integer a, Integer b) {
        return a + b;
    }

    public static long add(long a, long b) {
        return a + b;
    }

    public static Long add(Long a, Long b) {
        return a + b;
    }

    public static List add(List a, List b) {
        a.addAll(b);
        return a;
    }
}