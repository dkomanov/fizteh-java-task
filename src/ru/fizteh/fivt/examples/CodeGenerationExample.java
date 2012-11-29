package ru.fizteh.fivt.examples;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import java.io.PrintStream;

/**
 * @author Dmitriy Komanov (dkomanov@ya.ru)
 */
public class CodeGenerationExample {

    public static void main(String[] args) throws Exception {
        {
            Class<?> clazz = loadClass(helloWorld());
            System.out.println(clazz);
            clazz.getDeclaredMethod("main", String[].class)
                    .invoke(null, (Object) args);
        }

        {
            Class<?> clazz = loadClass(runnableWithHelloWorld());
            System.out.println(clazz);
            Runnable runnable = (Runnable) clazz.newInstance();
            runnable.run();
        }

        {
            Class<?> clazz = loadClass(runnableWithField());
            System.out.println(clazz);
            Runnable runnable = (Runnable) clazz.getConstructor(String.class).newInstance("Hi");
            runnable.run();
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

    private static byte[] helloWorld() {
        ClassWriter cw = newClassWriter();
        cw.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC, "HelloWorldMain", null,
                "java/lang/Object", null);

        generateMethod(cw, Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                "main", "([Ljava/lang/String;)V",
                new Function1V<GeneratorAdapter>() {
                    @Override
                    public void apply(GeneratorAdapter ga) {
                        generateHelloWorld(ga);
                        ga.returnValue();
                    }
                });

        return cw.toByteArray();
    }

    private static byte[] runnableWithHelloWorld() {
        ClassWriter cw = newClassWriter();
        cw.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC, "HelloWorldRunnable", null,
                "java/lang/Object", new String[]{"java/lang/Runnable"});

        generateMethod(cw, Opcodes.ACC_PUBLIC, "<init>", "()V",
                new Function1V<GeneratorAdapter>() {
                    @Override
                    public void apply(GeneratorAdapter ga) {
                        ga.loadThis();
                        ga.invokeConstructor(
                                Type.getType("java/lang/Object"),
                                new Method("<init>", "()V")
                        );
                        ga.returnValue();
                    }
                });

        generateMethod(cw, Opcodes.ACC_PUBLIC, "run", "()V",
                new Function1V<GeneratorAdapter>() {
                    @Override
                    public void apply(GeneratorAdapter ga) {
                        generateHelloWorld(ga);
                        ga.returnValue();
                    }
                });

        return cw.toByteArray();
    }

    private static byte[] runnableWithField() {
        final Type type = Type.getType("LRunnableWithField;");
        final String fieldName = "message";

        ClassWriter cw = newClassWriter();
        cw.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC, type.getInternalName(), null,
                "java/lang/Object", new String[]{"java/lang/Runnable"});

        cw.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL,
                fieldName, "Ljava/lang/String;", null, null)
                .visitEnd();

        generateMethod(cw, Opcodes.ACC_PUBLIC, "<init>", "(Ljava/lang/String;)V",
                new Function1V<GeneratorAdapter>() {
                    @Override
                    public void apply(GeneratorAdapter ga) {
                        ga.loadThis();
                        ga.dup();
                        ga.invokeConstructor(
                                Type.getType("java/lang/Object"),
                                new Method("<init>", "()V")
                        );
                        ga.loadArg(0);
                        ga.putField(type, fieldName, Type.getType(String.class));
                        ga.returnValue();
                    }
                });

        generateMethod(cw, Opcodes.ACC_PUBLIC, "run", "()V",
                new Function1V<GeneratorAdapter>() {
                    @Override
                    public void apply(GeneratorAdapter ga) {
                        // char[] chars = this.message.toCharArray();
                        ga.loadThis();
                        ga.getField(type, fieldName, Type.getType(String.class));
                        ga.invokeVirtual(Type.getType(String.class), new Method("toCharArray", "()[C"));
                        int charsLocal = ga.newLocal(Type.getType(char[].class));
                        ga.storeLocal(charsLocal);

                        Label forConditionLabel = ga.newLabel();
                        Label forLoopEnd = ga.newLabel();

                        // for (int i = 0;
                        int iLocal = ga.newLocal(Type.INT_TYPE);
                        ga.push(0);
                        ga.storeLocal(iLocal);

                        // i < chars.length;
                        ga.visitLabel(forConditionLabel);
                        ga.loadLocal(iLocal);
                        ga.loadLocal(charsLocal);
                        ga.arrayLength();
                        ga.ifCmp(Type.INT_TYPE, Opcodes.IFGE, forLoopEnd);

                        // System.out.println(chars[i]);
                        Type printStreamType = Type.getType(PrintStream.class);
                        ga.getStatic(Type.getType(System.class), "out", printStreamType);
                        ga.loadLocal(charsLocal);
                        ga.loadLocal(iLocal);
                        ga.arrayLoad(Type.CHAR_TYPE);
                        ga.invokeVirtual(printStreamType, new Method("println", "(C)V"));

                        // i++)
                        ga.iinc(iLocal, 1);
                        ga.goTo(forConditionLabel);

                        ga.visitLabel(forLoopEnd);
                        ga.returnValue();
                    }
                });

        return cw.toByteArray();
    }

    private static ClassWriter newClassWriter() {
        int flags = ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS;
        return new ClassWriter(flags);
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

    private static void generateHelloWorld(GeneratorAdapter ga) {
        Type printStreamType = Type.getType(PrintStream.class);
        ga.getStatic(Type.getType(System.class), "out", printStreamType);
        ga.push("Hello, World!");
        ga.invokeVirtual(printStreamType, new Method("println", "(Ljava/lang/String;)V"));
    }

    private interface Function1V<T> {
        void apply(T value);
    }
}
