package ru.fizteh.fivt.students.yushkevichAnton.proxy;

import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;
import ru.fizteh.fivt.proxy.ShardingProxyFactory;

import com.sun.deploy.net.proxy.ProxyUtils;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.GeneratorAdapter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

public class FooAsmProxyFactory extends FooProxyFactory {
    interface GeneratorApplier {
        public void apply(GeneratorAdapter generatorAdapter);
    }

    @Override
    public Object createProxy(Object[] targets, Class[] interfaces) {
        checkInterfaces(interfaces);
        checkTargets(targets, interfaces);

        ClassWriter classWriter = newClassWriter();
        String[] interfacesNames = new String[interfaces.length];
        for (int i = 0; i < interfaces.length; ++i) {
            interfacesNames[i] = Type.getInternalName(interfaces[i]);
        }

        classWriter.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, "Proxy", null, "java/lang/Object", interfacesNames);
        classWriter.visitField(Opcodes.ACC_PRIVATE, "targets", Type.getDescriptor(ArrayList.class), null, null).visitEnd();

        generateConstructor(classWriter);

        for (Class interfaceBar : interfaces) {
            Method[] methods = interfaceBar.getDeclaredMethods();
            for (Method method : methods) {
                generateInterfaceMethod(classWriter, interfaceBar, method, targets.length);
            }
        }

        classWriter.visitEnd();

        Object result;
        try {
            Class classBar = loadClass(classWriter.toByteArray());
            Constructor constructor = classBar.getConstructor(ArrayList.class);
            result = constructor.newInstance(new ArrayList(Arrays.asList(targets)));
        } catch (Exception t) {
            throw new RuntimeException(t.getMessage());
        }
        return result;
    }

    private final static GeneratorApplier constructorGeneratorApplier = new GeneratorApplier() {
        @Override
        public void apply(GeneratorAdapter generatorAdapter) {
            generatorAdapter.loadThis();
            generatorAdapter.invokeConstructor(Type.getType("java/lang/Object"), new org.objectweb.asm.commons.Method("<init>", "()V"));
            generatorAdapter.loadThis();
            generatorAdapter.loadArg(0);
            generatorAdapter.putField(Type.getType("Proxy"), "targets", Type.getType(ArrayList.class));
            generatorAdapter.returnValue();
        }
    };

    private void generateConstructor(ClassWriter cw) {
        generateMethod(cw, Opcodes.ACC_PUBLIC, "<init>", "(" + Type.getDescriptor(ArrayList.class) + ")V", constructorGeneratorApplier);
    }

    private void generateInterfaceMethod(ClassWriter cw, final Class interfaceBar, final Method method, final int targetsCount) {
        final Type arrayListType = Type.getType(ArrayList.class);
        final Type proxyType = Type.getType("Proxy");
        final Type interfaceType = Type.getType(interfaceBar);
        final String descriptor = Type.getMethodDescriptor(method);
        final String name = method.getName();
        final Class methodReturnType = method.getReturnType();
        generateMethod(cw, Opcodes.ACC_PUBLIC, name, descriptor, new GeneratorApplier() {
            public void apply(GeneratorAdapter generatorAdapter) {
                if (method.isAnnotationPresent(DoNotProxy.class)) {
                    generatorAdapter.throwException(Type.getType(RuntimeException.class), null);
                    return;
                }

                if (method.isAnnotationPresent(Collect.class)) {
                    generatorAdapter.loadThis();
                    generatorAdapter.getField(proxyType, "targets", arrayListType);

                    int argsCount = 0;
                    Set<Class> numClasses = new HashSet<Class>();
                    numClasses.add(int.class);
                    numClasses.add(Integer.class);
                    numClasses.add(long.class);
                    numClasses.add(Long.class);
                    for (Class classBar : method.getParameterTypes()) {
                        if (numClasses.contains(classBar)) {
                            break;
                        }
                        argsCount++;
                    }

                    generatorAdapter.loadArg(argsCount);
                    if (method.getParameterTypes()[argsCount].equals(int.class)) {
                        generatorAdapter.cast(Type.INT_TYPE, Type.LONG_TYPE);
                    }

                    generatorAdapter.push((long) targetsCount);
                    generatorAdapter.math(GeneratorAdapter.REM, Type.LONG_TYPE);
                    generatorAdapter.cast(Type.LONG_TYPE, Type.INT_TYPE);
                    generatorAdapter.invokeVirtual(arrayListType, new org.objectweb.asm.commons.Method("get", "(I)Ljava/lang/Object;"));
                    generatorAdapter.checkCast(interfaceType);
                    generatorAdapter.loadArgs();
                    generatorAdapter.invokeInterface(interfaceType, new org.objectweb.asm.commons.Method(name, descriptor));
                } else {
                    Label forConditionLabel = generatorAdapter.newLabel();
                    Label forLoopEnd = generatorAdapter.newLabel();

                    int iLocal = generatorAdapter.newLocal(Type.INT_TYPE);
                    generatorAdapter.push(0);
                    generatorAdapter.storeLocal(iLocal);

                    int sizeLocal = generatorAdapter.newLocal(Type.INT_TYPE);
                    generatorAdapter.loadThis();
                    generatorAdapter.getField(proxyType, "targets", arrayListType);
                    generatorAdapter.invokeVirtual(arrayListType, new org.objectweb.asm.commons.Method("size", "()I"));
                    generatorAdapter.storeLocal(sizeLocal);

                    int resLocal = 0;
                    if (!methodReturnType.equals(void.class)) {
                        resLocal = generatorAdapter.newLocal(Type.getType(methodReturnType));
                        if (methodReturnType.equals(int.class)) {
                            generatorAdapter.push(0);
                        } else if (methodReturnType.equals(Integer.class)) {
                            generatorAdapter.newInstance(Type.getType(Integer.class));
                            generatorAdapter.dup();
                            generatorAdapter.push(0);
                            //generatorAdapter.invokeConstructor(Type.getType(Integer.class), new org.objectweb.asm.commons.Method("<init>", "(I)V"));
                            generatorAdapter.invokeStatic(Type.getType(Integer.class), new org.objectweb.asm.commons.Method("valueOf", "(I)" + Type.getType(Integer.class).getDescriptor()));
                        } else if (methodReturnType.equals(long.class)) {
                            generatorAdapter.push(0L);
                        } else if (methodReturnType.equals(Long.class)) {
                            generatorAdapter.newInstance(Type.getType(Long.class));
                            generatorAdapter.dup();
                            generatorAdapter.push(0L);
                            //generatorAdapter.invokeConstructor(Type.getType(Long.class), new org.objectweb.asm.commons.Method("<init>", "(J)V"));
                            generatorAdapter.invokeStatic(Type.getType(Long.class), new org.objectweb.asm.commons.Method("valueOf", "(J)" + Type.getType(Long.class).getDescriptor()));
                        } else if (methodReturnType.equals(List.class)) {
                            generatorAdapter.newInstance(Type.getType(ArrayList.class));
                            generatorAdapter.dup();
                            generatorAdapter.invokeConstructor(arrayListType, new org.objectweb.asm.commons.Method("<init>", "()V"));
                        } else {
                            throw new IllegalArgumentException();
                        }
                        generatorAdapter.storeLocal(resLocal);
                    }

                    generatorAdapter.visitLabel(forConditionLabel);
                    generatorAdapter.loadLocal(iLocal);
                    generatorAdapter.loadLocal(sizeLocal);
                    generatorAdapter.ifCmp(Type.INT_TYPE, Opcodes.IFGE, forLoopEnd);

                    generatorAdapter.loadThis();
                    generatorAdapter.getField(proxyType, "targets", arrayListType);
                    generatorAdapter.loadLocal(iLocal);
                    generatorAdapter.invokeVirtual(arrayListType, new org.objectweb.asm.commons.Method("get", "(I)Ljava/lang/Object;"));

                    generatorAdapter.checkCast(interfaceType);
                    generatorAdapter.loadArgs();
                    generatorAdapter.invokeInterface(interfaceType, new org.objectweb.asm.commons.Method(name, descriptor));

                    if (!methodReturnType.equals(void.class)) {
                        Type returnType = Type.getType(methodReturnType);
                        generatorAdapter.loadLocal(resLocal);
                        generatorAdapter.swap(returnType, returnType);
                        String returnTypeDescriptor = returnType.getDescriptor();
                        final org.objectweb.asm.commons.Method merge = new org.objectweb.asm.commons.Method("merge", "(" +
                                                                                                                     returnTypeDescriptor + returnTypeDescriptor + ")" + returnTypeDescriptor);
                        generatorAdapter.invokeStatic(Type.getType(ProxyUtils.class), merge);
                        generatorAdapter.storeLocal(resLocal);
                    }

                    generatorAdapter.iinc(iLocal, 1);
                    generatorAdapter.goTo(forConditionLabel);

                    generatorAdapter.visitLabel(forLoopEnd);

                    if (!methodReturnType.equals(void.class)) {
                        generatorAdapter.loadLocal(resLocal);
                    }
                }
                generatorAdapter.returnValue();
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

    private static void generateMethod(ClassWriter cw, int access, String name, String descriptor, GeneratorApplier generatorApplier) {
        MethodVisitor mv = cw.visitMethod(access, name, descriptor, null, null);
        GeneratorAdapter generatorAdapter = new GeneratorAdapter(mv, access, name, descriptor);
        generatorAdapter.visitCode();
        generatorApplier.apply(generatorAdapter);
        generatorAdapter.endMethod();
    }
}