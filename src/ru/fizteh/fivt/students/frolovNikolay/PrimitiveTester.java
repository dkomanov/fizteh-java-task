package ru.fizteh.fivt.students.frolovNikolay;

public class PrimitiveTester {
    
    public static boolean isPrimitive(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz.equals(Character.class) || clazz.equals(Short.class)
                || clazz.equals(Integer.class) || clazz.equals(Long.class)
                || clazz.equals(Float.class) || clazz.equals(Double.class)
                || clazz.equals(Boolean.class) || clazz.equals(Byte.class);
    }
}
