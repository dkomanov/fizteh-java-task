package ru.fizteh.fivt.students.dmitriyBelyakov.proxy.asmProxy;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.GeneratorAdapter;
import ru.fizteh.fivt.proxy.ShardingProxyFactory;
import ru.fizteh.fivt.students.dmitriyBelyakov.proxy.ProxyUtils;
import ru.fizteh.fivt.students.dmitriyBelyakov.proxy.proxy.test.ClassForTests;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AsmShardingProxyFactory implements ShardingProxyFactory {
    @Override
    public Object createProxy(final Object[] targets, Class[] interfaces) {
        ProxyUtils.throwExceptionIsArgumentsIsIncorrect(targets, interfaces);
        ClassWriter cw = newClassWriter();
        String[] interfacesName = new String[interfaces.length];
        for (int i = 0; i < interfaces.length; ++i) {
            interfacesName[i] = Type.getInternalName(interfaces[i]);
        }
        cw.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC, "Proxy", null, "java/lang/Object", interfacesName);
        cw.visitField(Opcodes.ACC_PRIVATE, "targets", Type.getDescriptor(ArrayList.class), null, null).visitEnd();
        generateMethod(cw, Opcodes.ACC_PUBLIC, "<init>", "()V",
                new Function1V<GeneratorAdapter>() {
                    @Override
                    public void apply(GeneratorAdapter ga) {
                        ga.loadThis();
                        ga.invokeConstructor(
                                Type.getType("java/lang/Object"),
                                new org.objectweb.asm.commons.Method("<init>", "()V")
                        );
                        ga.returnValue();
                    }
                });
        generateMethod(cw, Opcodes.ACC_PRIVATE, "loadTargets", "(" + Type.getDescriptor(ArrayList.class) + ")V",
                new Function1V<GeneratorAdapter>() {
                    @Override
                    public void apply(GeneratorAdapter ga) {
                        ga.loadThis();
                        ga.loadArg(0);
                        ga.putField(Type.getType("Proxy"), "targets", Type.getType(ArrayList.class));
                        ga.returnValue();
                    }
                });
        for (final Class interfc : interfaces) {
            Method[] methods = interfc.getDeclaredMethods();
            for (final Method method : methods) {
                method.setAccessible(true);
                String descriptor = Type.getMethodDescriptor(method);
                generateMethod(cw, Opcodes.ACC_PUBLIC, method.getName(), descriptor,
                        new Function1V<GeneratorAdapter>() {
                            @Override
                            public void apply(GeneratorAdapter ga) {
                                try {
                                    if (!ProxyUtils.isCollect(method)) {
                                        ga.loadThis();
                                        ga.getField(Type.getType("Proxy"), "targets", Type.getType(ArrayList.class));
                                        ga.loadArgArray();
                                        ga.invokeStatic(Type.getType(ProxyUtils.class), new org.objectweb.asm.commons.Method("getFirstIntOrLongArgument",
                                                Type.getMethodDescriptor(ProxyUtils.class.getDeclaredMethod("getFirstIntOrLongArgument", Object[].class))));
                                        ga.push((long) targets.length);
                                        ga.math(GeneratorAdapter.REM, Type.getType(long.class));
                                        ga.cast(Type.getType(long.class), Type.getType(int.class));
                                        ga.invokeVirtual(Type.getType(ArrayList.class), new org.objectweb.asm.commons.Method("get",
                                                "(" + Type.getDescriptor(int.class) + ")" + Type.getDescriptor(Object.class)));
                                        ga.checkCast(Type.getType(interfc));
                                        ga.loadArgs();
                                        ga.invokeInterface(Type.getType(interfc), new org.objectweb.asm.commons.Method(method.getName(),
                                                Type.getMethodDescriptor(method)));
                                    } else {
                                        // char[] chars = this.message.toCharArray();

                                        Label forConditionLabel = ga.newLabel();
                                        Label forLoopEnd = ga.newLabel();

                                        //for (int i = 0;
                                        int iLocal = ga.newLocal(Type.INT_TYPE);
                                        ga.push(0);
                                        ga.storeLocal(iLocal);
                                        int sizeLocal = ga.newLocal(Type.INT_TYPE);
                                        ga.loadThis();
                                        ga.getField(Type.getType("Proxy"), "targets", Type.getType(ArrayList.class));
                                        ga.invokeVirtual(Type.getType(ArrayList.class), new org.objectweb.asm.commons.Method("size", "()I"));
                                        ga.storeLocal(sizeLocal);
                                        int resLocal = 0;
                                        if (!method.getReturnType().equals(void.class)) {
                                            resLocal = ga.newLocal(Type.getType(method.getReturnType()));
                                            if (!method.getReturnType().equals(List.class)) {
                                                ga.push(0.0);
                                                ga.cast(Type.getType(double.class), Type.getType(method.getReturnType()));
                                            } else {
                                                ga.newInstance(Type.getType(ArrayList.class));
                                                ga.dup();
                                                ga.invokeConstructor(Type.getType(ArrayList.class), new org.objectweb.asm.commons.Method("<init>", "()V"));
                                            }
                                            ga.storeLocal(resLocal);
                                        }
                                        boolean first = true;

                                        // i < chars.length;
                                        ga.visitLabel(forConditionLabel);
                                        ga.loadLocal(iLocal);
                                        ga.loadLocal(sizeLocal);
                                        //printTypeOnTheTop(ga);
                                        //ga.ifICmp(Opcodes.IFGE, forLoopEnd);
                                        ga.ifCmp(Type.INT_TYPE, Opcodes.IFGE, forLoopEnd);

                                        // System.out.println(chars[i]);
                                        //Type printStreamType = Type.getType(PrintStream.class);
                                        //ga.getStatic(Type.getType(System.class), "out", printStreamType);
                                        ga.loadThis();
                                        ga.getField(Type.getType("Proxy"), "targets", Type.getType(ArrayList.class));
                                        ga.loadLocal(iLocal);
                                        ga.invokeVirtual(Type.getType(ArrayList.class), new org.objectweb.asm.commons.Method("get", "(I)Ljava/lang/Object;"));
                                        //ga.loadLocal(iLocal);
                                        //ga.push(0);
                                        //ga.arrayLoad(Type.getType(Object.class));
                                        ga.checkCast(Type.getType(interfc));
                                        ga.loadArgs();
                                        ga.invokeInterface(Type.getType(interfc), new org.objectweb.asm.commons.Method(method.getName(),
                                                Type.getMethodDescriptor(method)));
                                        //ga.invokeVirtual(printStreamType, new org.objectweb.asm.commons.Method("println", "(Ljava/lang/Object;)V"));
                                        if (!method.getReturnType().equals(void.class)) {
                                            ga.loadLocal(resLocal);
                                            ga.swap(Type.getType(method.getReturnType()), Type.getType(method.getReturnType()));
                                            ga.invokeStatic(Type.getType(ProxyUtils.class),
                                                    new org.objectweb.asm.commons.Method("merge", "("
                                                            + Type.getDescriptor(method.getReturnType())
                                                            + Type.getDescriptor(method.getReturnType()) + ")"
                                                            + Type.getDescriptor(method.getReturnType())));
                                            ga.storeLocal(resLocal);
                                        }

                                        // i++)
                                        ga.iinc(iLocal, 1);
                                        ga.goTo(forConditionLabel);

                                        ga.visitLabel(forLoopEnd);

                                        if (!method.getReturnType().equals(void.class)) {
                                            //System.out.println(resLocal);
                                            ga.loadLocal(resLocal); // ???
                                            //if (!method.getReturnType().equals(List.class)) {
                                            //    ga.cast(Type.getType(method.getReturnType()), Type.getType(method.getReturnType()));
                                            //}
                                            //ga.storeLocal(resLocal);
                                        }

                                        /*
                                        ga.loadThis();
                                        ga.getField(Type.getType("Proxy"), "targets", Type.getType(ArrayList.class));
                                        //ga.loadArgArray();
                                        //ga.invokeStatic(Type.getType(ProxyUtils.class), new org.objectweb.asm.commons.Method("getFirstIntOrLongArgument",
                                        //        Type.getMethodDescriptor(ProxyUtils.class.getDeclaredMethod("getFirstIntOrLongArgument", Object[].class))));
                                        ga.push(0);
                                        //ga.math(GeneratorAdapter.REM, Type.getType(long.class));
                                        //ga.cast(Type.getType(long.class), Type.getType(int.class));
                                        ga.invokeVirtual(Type.getType(ArrayList.class), new org.objectweb.asm.commons.Method("get",
                                                "(" + Type.getDescriptor(int.class) + ")" + Type.getDescriptor(Object.class)));
                                        ga.checkCast(Type.getType(interfc));
                                        ga.loadArgs();
                                        ga.invokeInterface(Type.getType(interfc), new org.objectweb.asm.commons.Method(method.getName(),
                                                Type.getMethodDescriptor(method)));
                                                */


                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    System.exit(1);
                                }
                                ga.returnValue();
                            }
                        });
            }
        }
        cw.visitEnd();
        try {
            Class clazz = loadClass(cw.toByteArray());
            Object obj = clazz.newInstance();
            Method method = clazz.getDeclaredMethod("loadTargets", ArrayList.class);
            method.setAccessible(true);
            method.invoke(obj, new ArrayList(Arrays.asList(targets)));
            return obj;
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException("Cannot create proxy object.");
        }
    }

    private void printTypeOnTheTop(GeneratorAdapter ga) {
        Type printStreamType = Type.getType(PrintStream.class);
        ga.dup();
        ga.getStatic(Type.getType(System.class), "out", printStreamType);
        ga.swap(Type.getType(Object.class), printStreamType);
        //ga.invokeVirtual(Type.getType(Object.class), new org.objectweb.asm.commons.Method("getClass", "()" + Type.getDescriptor(Class.class)));
        //ga.invokeVirtual(Type.getType(Class.class), new org.objectweb.asm.commons.Method("getName", "()" + Type.getDescriptor(String.class)));
        ga.invokeVirtual(printStreamType, new org.objectweb.asm.commons.Method("println", "(I)V"));
    }

    private ClassWriter newClassWriter() {
        int flags = ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS;
        return new ClassWriter(flags);
    }

    private Class loadClass(byte[] bytes) {
        class LocalClassLoader extends ClassLoader {
            public Class defineClass(byte[] bytes) {
                return super.defineClass(null, bytes, 0, bytes.length);
            }
        }
        return new LocalClassLoader().defineClass(bytes);
    }

    private static void generateMethod(ClassWriter cw, int access, String name, String descriptor,
                                       Function1V<GeneratorAdapter> f) {
        MethodVisitor mv = cw.visitMethod(access, name, descriptor, null, null);
        GeneratorAdapter ga = new GeneratorAdapter(mv, access, name, descriptor);
        ga.visitCode();
        f.apply(ga);
        ga.endMethod();
    }

    private interface Function1V<T> {
        void apply(T value);
    }
}