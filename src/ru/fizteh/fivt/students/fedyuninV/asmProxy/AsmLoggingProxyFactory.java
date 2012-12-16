package ru.fizteh.fivt.students.fedyuninV.asmProxy;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;
import ru.fizteh.fivt.proxy.LoggingProxyFactory;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class AsmLoggingProxyFactory implements LoggingProxyFactory{
    private static Class targetClass;
    private interface Function1V<T> {
        void apply(T value);
    }

    public Object createProxy(Object target, Appendable writer, Class... interfaces) {
        if (target == null  ||  writer == null  ||  interfaces == null  ||  interfaces.length == 0
                || Arrays.asList(interfaces).contains(null)) {
            throw new IllegalArgumentException("Null parameter found");
        }
        int methodsNum = 0;
        for (int i = 0; i < interfaces.length; i++) {
            if (!interfaces[i].isAssignableFrom(target.getClass())) {
                throw new IllegalArgumentException("target doesn't support interface");
            }
            methodsNum += interfaces[i].getMethods().length;
        }
        if (methodsNum == 0) {
            throw new IllegalArgumentException("No methods in interfaces");
        }
        targetClass = target.getClass();
        Logger logger = new Logger(writer);
        Class clazz = loadClass(getBytes(target, logger, interfaces));
        try {
            Constructor constructor = clazz.getConstructor(targetClass, Logger.class);
            return constructor.newInstance(target, logger);
        } catch (Exception ex) {
            throw new RuntimeException("Can't create proxy", ex);
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

    private byte[] getBytes(Object target, Logger logger, Class[] interfaces) {
        String[] internalNames = new String[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            internalNames[i] = Type.getInternalName(interfaces[i]);
        }
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classWriter.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC, "Proxy", null, Type.getInternalName(Object.class), internalNames);
        //adding target & logger
        classWriter.visitField(Opcodes.ACC_PRIVATE, "target", Type.getDescriptor(targetClass), null, null);
        classWriter.visitField(Opcodes.ACC_PRIVATE, "logger", Type.getDescriptor(Logger.class), null, null);
        classWriter.visitEnd();
        //generatingConstructor
        generateConstructor(classWriter);
        //generating methods with logging
        Set<String> addedMethods = new HashSet<>();
        for (Class interfaze: interfaces) {
            for (java.lang.reflect.Method method: interfaze.getDeclaredMethods()) {
                String description = Type.getMethodDescriptor(method);
                if (!addedMethods.contains(method.getName() + description)) {
                    generateMethodWithLogging(classWriter, method, interfaze);
                    addedMethods.add(method.getName() + description);
                }
            }
        }

        return classWriter.toByteArray();
    }

    private void generateConstructor(ClassWriter classWriter) {
        generateMethod(classWriter, Opcodes.ACC_PUBLIC, "<init>", "(" + Type.getDescriptor(targetClass) +
                Type.getDescriptor(Logger.class) + ")V",
                new Function1V<GeneratorAdapter>() {
                    @Override
                    public void apply(GeneratorAdapter ga) {
                        ga.loadThis();
                        ga.invokeConstructor( Type.getType(Object.class), new Method("<init>", "()V"));
                        ga.loadThis();
                        ga.loadArg(0);
                        ga.putField(Type.getType("Proxy"), "target", Type.getType(targetClass));
                        ga.loadThis();
                        ga.loadArg(1);
                        ga.putField(Type.getType("Proxy"), "logger", Type.getType(Logger.class));
                        ga.loadThis();
                        ga.returnValue();
                    }
                });
    }

    private static void generateMethodWithLogging(ClassWriter classWriter, final java.lang.reflect.Method method,
                                                  final Class interfaze) {
        final Type proxyType = Type.getType("Proxy");
        final Type targetType = Type.getType(targetClass);
        final Type loggerType = Type.getType(Logger.class);
        String logMethodAndArgsDesc = null;
        String logResultDesc = null;
        String logTrowableDesc = null;
        try {
            logMethodAndArgsDesc = Type.getMethodDescriptor(
                    Logger.class.getMethod("appendMethodAndArgs", String.class, String.class, Object[].class));
            logResultDesc = Type.getMethodDescriptor(
                    Logger.class.getMethod("appendResult", Object.class));
            logTrowableDesc = Type.getMethodDescriptor(
                    Logger.class.getMethod("appendThrowable", Throwable.class));
        } catch (Exception ignored) {
        }
        final String logMethodAndArgs = logMethodAndArgsDesc;
        final String logResult = logResultDesc;
        final String logThrowable = logTrowableDesc;
        generateMethod(
                classWriter,
                Opcodes.ACC_PUBLIC,
                method.getName(),
                Type.getMethodDescriptor(method),
                new Function1V<GeneratorAdapter>() {
                    Type interfazeType = Type.getType(interfaze);

                    private void invokeInterface(GeneratorAdapter ga) {
                        try {
                            ga.invokeInterface(interfazeType, new Method(method.getName(), Type.getMethodDescriptor(method)));
                        } catch (Exception ignored) {
                        }
                    }

                    @Override
                    public void apply(GeneratorAdapter ga) {
                        ga.loadThis();
                        ga.getField(proxyType, "logger", loggerType);
                        ga.push(method.getDeclaringClass().getSimpleName());
                        ga.push(method.getName());
                        ga.loadArgArray();
                        try {
                            ga.invokeVirtual(loggerType, new Method("appendMethodAndArgs", logMethodAndArgs));
                        } catch (Exception ignored) {
                            System.err.println("type'o");
                        }
                        ga.loadThis();
                        ga.getField(proxyType, "target", targetType);
                        ga.loadArgs();
                        ga.invokeInterface(interfazeType, new Method(method.getName(), Type.getMethodDescriptor(method)));
                        if (!method.getReturnType().equals(void.class)) {
                            //ga.dup();
                            int resultLocal = ga.newLocal(Type.getType(Object.class));
                            ga.storeLocal(resultLocal);
                            ga.loadThis();
                            ga.getField(proxyType, "logger", loggerType);
                            ga.loadLocal(resultLocal);
                            try {
                                ga.invokeVirtual(loggerType, new Method("appendResult", logResult));
                            } catch (Exception ignored) {
                                System.err.println("type'o");
                            }
                        }
                        ga.returnValue();
                    }
                }
        );
    }

    private static void generateMethod(ClassWriter classWriter, int access, String name, String descriptor,
                                       Function1V<GeneratorAdapter> f) {
        MethodVisitor methodVisitor = classWriter.visitMethod(access, name, descriptor, null, null);
        GeneratorAdapter ga = new GeneratorAdapter(methodVisitor, access, name, descriptor);
        ga.visitCode();
        f.apply(ga);
        ga.endMethod();
    }
}
