package ru.fizteh.fivt.examples;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author Dmitriy Komanov (dkomanov@ya.ru)
 */
public class ClassLoaderExample {
    public static void main(String[] args) throws Exception {
        // ru.fizteh.fivt.examples.ClassLoaderHelper
        final String className = ClassLoaderHelper.class.getName();
        ClassLoader loader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                if (!name.equals(className)) {
                    return super.loadClass(name);
                }
                byte[] bytes = readBytes(name);
                return defineClass(name, bytes, 0, bytes.length);
            }
        };
        Class<?> defaultLoaded = Class.forName(className);
        Class<?> customLoaded = Class.forName(className, true, loader);
        assert defaultLoaded != customLoaded;

        Object defaultInstance = defaultLoaded.newInstance();
        Object customInstance = customLoaded.newInstance();
        assert !defaultInstance.getClass().equals(customInstance.getClass());
        assert defaultInstance.getClass().getName().equals(customInstance.getClass().getName());
    }

    private static byte[] readBytes(String name) {
        try {
            String fileName = name.replace('.', File.separatorChar) + ".class";
            File file = new File(fileName);
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
