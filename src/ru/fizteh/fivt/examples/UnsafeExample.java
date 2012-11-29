package ru.fizteh.fivt.examples;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author Dmitriy Komanov (dkomanov@ya.ru)
 */
public class UnsafeExample {

    private static final Unsafe unsafeInstance;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafeInstance = Unsafe.class.cast(field.get(null));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to get Unsafe instance", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to obtain theUnsafe field", e);
        }
    }

    private static class NoDefaultConstructorClass {
        public final int n;
        public final String s;

        private NoDefaultConstructorClass(int n, String s) {
            this.n = n;
            this.s = s;
        }
    }

    public static void main(String[] args) throws Exception {
        Object o = unsafeInstance.allocateInstance(NoDefaultConstructorClass.class);
        assert o != null;
        NoDefaultConstructorClass c = (NoDefaultConstructorClass) o;
        assert c.n == 0;
        assert c.s == null;
    }
}
