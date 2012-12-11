package ru.fizteh.fivt.students.mesherinIlya.asmproxy;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

import java.lang.reflect.Constructor;

import ru.fizteh.fivt.proxy.DoNotProxy;
import ru.fizteh.fivt.proxy.Collect;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

public class AsmShardingProxyFactory implements ru.fizteh.fivt.proxy.ShardingProxyFactory {
    
    private static ClassWriter newClassWriter() {
        int flags = ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS;
        return new ClassWriter(flags);
    }

    private static Class<?> loadClass(byte[] bytes) {
        class LocalClassLoader extends ClassLoader {
            public Class<?> defineClass(byte[] bytes) {
                return super.defineClass(null, bytes, 0, bytes.length);
            }
        }
        return new LocalClassLoader().defineClass(bytes);
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
    
    private void Validation(Object[] targets, Class[] interfaces) {
        if (targets == null) {
            throw new IllegalArgumentException("Targets is null.");
        } else if (interfaces == null) {
            throw new IllegalArgumentException("Interfaces is null.");
        } else if (targets.length == 0) {
            throw new IllegalArgumentException("There are no targets.");
        } else if (interfaces.length == 0) {
            throw new IllegalArgumentException("There are no interfaces.");
        }
        for (Object target : targets) {
            if (target == null) {
                throw new IllegalArgumentException("Some of targets are null.");
            }
        }
        for (Class intrface : interfaces) {
            if (intrface == null) {
                throw new IllegalArgumentException("Some of interfaces are null.");
            }
        }
        HashSet<Class> setOfInterfaces = new HashSet<Class>(Arrays.asList(interfaces));
        for (Object target : targets) {
            Class[] ourInterfaces = target.getClass().getInterfaces();
            boolean contains = true;
            for (Class intrface : ourInterfaces) {
                if (!setOfInterfaces.contains(intrface)) {
                    contains = false;
                    break;
                }
            }
            if (ourInterfaces.length == 0 || !contains) {
                throw new IllegalArgumentException("Some of targets don't implement all these interfaces.");
            }
        }
        for (Class intrface : interfaces) {
            java.lang.reflect.Method[] methods = intrface.getMethods();
            if (methods.length == 0) {
                throw new IllegalArgumentException("Some of interfaces haven't got any methods.");
            }
            for (java.lang.reflect.Method method : methods) {
                if (method.getAnnotation(DoNotProxy.class) == null) {
                    if (method.getAnnotation(Collect.class) == null) {
                        Set<Class> parameterTypes = new HashSet<Class>(Arrays.asList(method.getParameterTypes()));
                        if (!parameterTypes.contains(int.class)
                                && !parameterTypes.contains(Integer.class)
                                && !parameterTypes.contains(long.class) 
                                && !parameterTypes.contains(Long.class)) {
                            throw new IllegalArgumentException(
                                    "There must be at least one integer or long number among the parameters.");
                        }
                    } else {
                        Class returnType = method.getReturnType();
                        if (!returnType.equals(void.class)
                                && !returnType.equals(int.class)
                                && !returnType.equals(Integer.class)
                                && !returnType.equals(long.class)
                                && !returnType.equals(Long.class) 
                                && !returnType.equals(List.class)) 
                        {
                            throw new IllegalStateException("The return type of method isn't supported. ");
                        }
                    }
                }
            }   
        }
    }    

    private interface Function1V<T> {
        void apply(T value);
    }

    @Override
    public Object createProxy(final Object[] targets, Class[] interfaces) {
            
        Validation(targets, interfaces);
        
        String[] interfacesNames = new String[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            interfacesNames[i] = Type.getInternalName(interfaces[i]);
        }
        
        //Starting class generation        
        ClassWriter cw = newClassWriter();
        cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC, "Proxy", null, "java/lang/Object", interfacesNames);
        cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL,
                "targets", Type.getType(ArrayList.class).getDescriptor(), null, null)
                .visitEnd();
        
        //Generating interface methods
        for (final Class intrface : interfaces) {
            java.lang.reflect.Method[] methods = intrface.getDeclaredMethods();
            for (final java.lang.reflect.Method method : methods) {
                
                method.setAccessible(true);

                Function1V<GeneratorAdapter> methodGenerator = new Function1V<GeneratorAdapter>() {
                    
                    @Override
                    public void apply(GeneratorAdapter ga) {
                        if (method.getAnnotation(DoNotProxy.class) != null) {
                            ga.throwException(Type.getType(IllegalStateException.class),
                                    "This method can't be invoked using the proxy.");
                        } else if (method.getAnnotation(Collect.class) != null) {
                            Label forContinue = ga.newLabel();
                            Label forEnding = ga.newLabel();
                            //for (int i = 0, 
                            int i = ga.newLocal(Type.INT_TYPE);
                            ga.push(0);
                            ga.storeLocal(i);
                            //int size = targets.size();
                            int size = ga.newLocal(Type.INT_TYPE);
                            ga.loadThis();
                            ga.getField(Type.getType("Proxy"), "targets", Type.getType(ArrayList.class));
                            ga.invokeVirtual(Type.getType(ArrayList.class), new Method("size", "()I"));
                            ga.storeLocal(size);
                            // size_t summand = ...;
                            int summand = 0;
                            Class returnType = method.getReturnType();
                            if (!returnType.equals(void.class)) {
                                summand = ga.newLocal(Type.getType(returnType));
                                if (returnType.equals(int.class)) {
                                    ga.push(0);
                                } else if (returnType.equals(Integer.class)) {
                                    ga.newInstance(Type.getType(Integer.class));
                                    ga.dup();
                                    ga.push(0);
                                    ga.invokeConstructor(Type.getType(Integer.class), new Method("<init>", "(I)V"));
                                } else if (returnType.equals(long.class)) {
                                    ga.push(0L);
                                } else if (returnType.equals(Long.class)) {
                                    ga.newInstance(Type.getType(Long.class));
                                    ga.dup();
                                    ga.push(0L);
                                    ga.invokeConstructor(Type.getType(Long.class), new Method("<init>", "(J)V"));
                                } else if (returnType.equals(List.class)) {
                                    ga.newInstance(Type.getType(ArrayList.class));
                                    ga.dup();
                                    ga.invokeConstructor(Type.getType(ArrayList.class), new Method("<init>", "()V"));
                                } else {
                                    throw new IllegalArgumentException("The type isn't supported.");
                                }
                                ga.storeLocal(summand);
                            }
                            // forContinue: if (i >= size) goto forEnding;
                            ga.visitLabel(forContinue);
                            ga.loadLocal(i);
                            ga.loadLocal(size);
                            ga.ifCmp(Type.INT_TYPE, Opcodes.IFGE, forEnding);
                            // Object target = targets.get(i);
                            ga.loadThis();
                            ga.getField(Type.getType("Proxy"), "targets", Type.getType(ArrayList.class));
                            ga.loadLocal(i);
                            ga.invokeVirtual(Type.getType(ArrayList.class), new Method("get","(I)Ljava/lang/Object;"));
                            // target.getDeclaredMethod(...).invoke(obj, ...);
                            ga.checkCast(Type.getType(intrface));
                            ga.loadArgs();
                            ga.invokeInterface(Type.getType(intrface), new Method(method.getName(), Type.getMethodDescriptor(method)));
                            // add summand to result
                            if (!returnType.equals(void.class)) {
                                Type type = Type.getType(returnType);
                                ga.loadLocal(summand);
                                ga.swap(type, type);
                                if (returnType.equals(List.class)) {
                                    String descriptor = type.getDescriptor();
                                    ga.invokeVirtual(Type.getType(ArrayList.class),
                                            new Method("addAll", "(" + descriptor + ")V"));
                                } else {
                                    ga.math(GeneratorAdapter.ADD, Type.getType(returnType)); 
                                }
                                ga.storeLocal(summand);
                            }

                            // i++; goto forContinue;
                            ga.iinc(i, 1);
                            ga.goTo(forContinue);
                            
                            //forEnding:
                            ga.visitLabel(forEnding);

                            if (!returnType.equals(void.class)) {
                                ga.loadLocal(summand);
                            }

                        } else if (method.getAnnotation(Collect.class) == null) {
                            //а здесь просто вызовем функцию у целевого объекта
                            ga.loadThis();
                            ga.getField(Type.getType("Proxy"), "targets", Type.getType(ArrayList.class));
                            int numArg = 0;
                            Class[] args = method.getParameterTypes();
                            for (int i = 0; i < args.length; i++) {
                                if (args[i].equals(int.class) || args[i].equals(Integer.class)
                                        || args[i].equals(long.class) || args[i].equals(Long.class))
                                {
                                    numArg = i;
                                    break;
                                }
                            }
                            ga.loadArg(numArg);
                            if (method.getParameterTypes()[numArg].equals(int.class)) {
                                ga.cast(Type.INT_TYPE, Type.LONG_TYPE);
                            }
                            ga.push((long) targets.length);
                            ga.math(GeneratorAdapter.REM, Type.LONG_TYPE);
                            ga.cast(Type.LONG_TYPE, Type.INT_TYPE);
                            ga.invokeVirtual(Type.getType(ArrayList.class), new Method("get", "(I)Ljava/lang/Object;"));
                            ga.checkCast(Type.getType(intrface));
                            ga.loadArgs();
                            ga.invokeInterface(Type.getType(intrface), 
                                    new Method(method.getName(), Type.getMethodDescriptor(method)));    
                        
                        }
                    } //apply        
                }; //function1V
                
                generateMethod(cw, Opcodes.ACC_PUBLIC, method.getName(), Type.getMethodDescriptor(method), methodGenerator);
                                
            } //for (method)
        }  //for (interface)
        
        //methods are generated
        
        //generating constructor
        Function1V<GeneratorAdapter> constructorGenerator = new Function1V<GeneratorAdapter>() {

            @Override
            public void apply(GeneratorAdapter ga) {
                ga.loadThis();
                ga.invokeConstructor(Type.getType("java/lang/Object"), new Method("<init>", "()V"));
                ga.loadThis();
                ga.loadArg(0);
                ga.putField(Type.getType("Proxy"), "targets", Type.getType(ArrayList.class));
                ga.returnValue();
            }
        };
        
        generateMethod(cw, Opcodes.ACC_PUBLIC, "<init>", "(" + Type.getDescriptor(ArrayList.class) + ")V",
                constructorGenerator);
        
        //constructor generated       
        
        cw.visitEnd();
        try {
            Class shardingProxy = loadClass(cw.toByteArray());
            Constructor constructor = shardingProxy.getConstructor(ArrayList.class);
            constructor.setAccessible(true);
            return constructor.newInstance(new ArrayList(Arrays.asList(targets)));
        } catch (Throwable t) {
            throw new RuntimeException("Failed to create proxy.", t);
        }
        
    }

}
