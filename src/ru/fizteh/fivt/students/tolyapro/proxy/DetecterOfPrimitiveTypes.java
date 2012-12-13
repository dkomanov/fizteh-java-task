package ru.fizteh.fivt.students.tolyapro.proxy;

/**
 * @author tolyapro
 */
public class DetecterOfPrimitiveTypes {

    private static boolean isWrapperType(Class clazz) {
        return clazz.equals(Boolean.class) || clazz.equals(Integer.class)
                || clazz.equals(Character.class) || clazz.equals(Byte.class)
                || clazz.equals(Short.class) || clazz.equals(Double.class)
                || clazz.equals(Long.class) || clazz.equals(Float.class);
    }

    public static boolean isPrimitive(Object object) {
        Class clazz = object.getClass();
        return clazz.isPrimitive() || clazz.isEnum()
                || clazz.equals(String.class) || isWrapperType(clazz);
    }
}
