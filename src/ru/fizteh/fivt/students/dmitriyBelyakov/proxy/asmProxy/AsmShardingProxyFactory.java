package ru.fizteh.fivt.students.dmitriyBelyakov.proxy.asmProxy;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.GeneratorAdapter;
import ru.fizteh.fivt.proxy.ShardingProxyFactory;
import ru.fizteh.fivt.students.dmitriyBelyakov.proxy.ProxyUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AsmShardingProxyFactory implements ShardingProxyFactory {
    @Override
    public Object createProxy(final Object[] targets, Class[] interfaces) {
        ProxyUtils.throwExceptionIfArgumentsIsIncorrect(targets, interfaces);
        ProxyUtils.throwExceptionIfInterfacesContainsEqualsMethodSignature(interfaces);
        ClassWriter cw = newClassWriter();
        String[] interfacesName = new String[interfaces.length];
        for (int i = 0; i < interfaces.length; ++i) {
            interfacesName[i] = Type.getInternalName(interfaces[i]);
        }
        cw.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC, "Proxy", null, "java/lang/Object", interfacesName);
        cw.visitField(Opcodes.ACC_PRIVATE, "targets", Type.getDescriptor(ArrayList.class), null, null).visitEnd();
        generateConstructor(cw);
        generateLoadTargets(cw);
        for (final Class interfc : interfaces) {
            Method[] methods = interfc.getDeclaredMethods();
            for (final Method method : methods) {
                method.setAccessible(true);
                generateInterfaceMethod(cw, interfc, method, targets.length);
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

    private void generateConstructor(ClassWriter cw) {
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
    }

    private void generateLoadTargets(ClassWriter cw) {
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
    }

    private void generateInterfaceMethod(ClassWriter cw, final Class interfc, final Method method, final int targetsCount) {
        final Type arrayListType = Type.getType(ArrayList.class);
        final Type proxyType = Type.getType("Proxy");
        final Type interfaceType = Type.getType(interfc);
        //method.setAccessible(true);
        final String descriptor = Type.getMethodDescriptor(method);
        final String name = method.getName();
        final Class methodReturnType = method.getReturnType();
        generateMethod(cw, Opcodes.ACC_PUBLIC, name, descriptor,
                new Function1V<GeneratorAdapter>() {
                    @Override
                    public void apply(GeneratorAdapter ga) {
                        if (ProxyUtils.isDoNotProxy(method)) {
                            ga.throwException(Type.getType(RuntimeException.class), "This method not for proxy.");
                        } else if (!ProxyUtils.isCollect(method)) {
                            ga.loadThis();
                            ga.getField(proxyType, "targets", arrayListType);
                            int numOfArg = ProxyUtils.getNumberOfFirstIntOrLongArgument(method.getParameterTypes());
                            ga.loadArg(numOfArg);
                            if(method.getParameterTypes()[numOfArg].equals(int.class)) {
                                ga.cast(Type.INT_TYPE, Type.LONG_TYPE);
                            }
                            ga.push((long) targetsCount);
                            ga.math(GeneratorAdapter.REM, Type.LONG_TYPE);
                            ga.cast(Type.LONG_TYPE, Type.INT_TYPE);
                            ga.invokeVirtual(arrayListType, new org.objectweb.asm.commons.Method("get",
                                    "(I)Ljava/lang/Object;"));
                            ga.checkCast(interfaceType);
                            ga.loadArgs();
                            ga.invokeInterface(interfaceType, new org.objectweb.asm.commons.Method(name,
                                    descriptor));
                        } else {
                            Label forConditionLabel = ga.newLabel();
                            Label forLoopEnd = ga.newLabel();

                            //for (int i = 0, size = targets.size();
                            int iLocal = ga.newLocal(Type.INT_TYPE);
                            ga.push(0);
                            ga.storeLocal(iLocal);
                            int sizeLocal = ga.newLocal(Type.INT_TYPE);
                            ga.loadThis();
                            ga.getField(proxyType, "targets", arrayListType);
                            ga.invokeVirtual(arrayListType, new org.objectweb.asm.commons.Method("size", "()I"));
                            ga.storeLocal(sizeLocal);

                            // <Type> res = ...;
                            int resLocal = 0;
                            if (!methodReturnType.equals(void.class)) {
                                resLocal = ga.newLocal(Type.getType(methodReturnType));
                                if (methodReturnType.equals(int.class) || methodReturnType.equals(Integer.class)) {
                                    ga.push(0);
                                } else if (methodReturnType.equals(long.class) || methodReturnType.equals(Long.class)) {
                                    ga.push((long) 0);
                                } else if (methodReturnType.equals(List.class)) {
                                    ga.newInstance(Type.getType(ArrayList.class));
                                    ga.dup();
                                    ga.invokeConstructor(arrayListType, new org.objectweb.asm.commons.Method("<init>", "()V"));
                                } else {
                                    throw new IllegalArgumentException("Incorrect return type for method with @Collect.");
                                }
                                ga.storeLocal(resLocal);
                            }

                            // i < size;
                            ga.visitLabel(forConditionLabel);
                            ga.loadLocal(iLocal);
                            ga.loadLocal(sizeLocal);
                            ga.ifCmp(Type.INT_TYPE, Opcodes.IFGE, forLoopEnd);

                            // Object obj = targets.get(i);
                            ga.loadThis();
                            ga.getField(proxyType, "targets", arrayListType);
                            ga.loadLocal(iLocal);
                            ga.invokeVirtual(arrayListType, new org.objectweb.asm.commons.Method("get",
                                    "(I)Ljava/lang/Object;"));

                            // obj.getDeclaredMethod(...).invoke(obj, ...);
                            ga.checkCast(interfaceType);
                            ga.loadArgs();
                            ga.invokeInterface(interfaceType, new org.objectweb.asm.commons.Method(name,
                                    descriptor));

                            // ProxyUtils.merge(res, obj);
                            if (!methodReturnType.equals(void.class)) {
                                Type returnType = Type.getType(methodReturnType);
                                ga.loadLocal(resLocal);
                                ga.swap(returnType, returnType);
                                String returnTypeDescriptor = returnType.getDescriptor();
                                ga.invokeStatic(Type.getType(ProxyUtils.class),
                                        new org.objectweb.asm.commons.Method("merge", "("
                                                + returnTypeDescriptor
                                                + returnTypeDescriptor + ")"
                                                + returnTypeDescriptor));
                                ga.storeLocal(resLocal);
                            }

                            // i++)
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