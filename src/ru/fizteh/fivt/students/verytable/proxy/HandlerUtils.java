package ru.fizteh.fivt.students.verytable.proxy;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Map;

public class HandlerUtils {

    private static final HashSet<Class<?>> PRIMITIVE_TYPES = getTypes();

    public static boolean isPrimitiveOrWrappedType(Class<?> clazz) {
        return PRIMITIVE_TYPES.contains(clazz);
    }

    private static HashSet<Class<?>> getTypes() {
        HashSet<Class<?>> types = new HashSet<>();
        types.add(Boolean.class);
        types.add(boolean.class);
        types.add(Character.class);
        types.add(char.class);
        types.add(Byte.class);
        types.add(byte.class);
        types.add(Short.class);
        types.add(short.class);
        types.add(Integer.class);
        types.add(int.class);
        types.add(Long.class);
        types.add(long.class);
        types.add(Float.class);
        types.add(float.class);
        types.add(Double.class);
        types.add(double.class);
        types.add(Void.class);
        types.add(void.class);
        return types;
    }

    public static String wrap(String s, char leftSymbol, char rightSymbol) {

        StringBuilder sb = new StringBuilder();
        s = s.replaceAll("\\\\", "\\\\\\\\");
        s = s.replaceAll("\t", "\\\\t");
        s = s.replaceAll("\b", "\\\\b");
        s = s.replaceAll("\n", "\\\\n");
        s = s.replaceAll("\r", "\\\\r");
        s = s.replaceAll("\f", "\\\\f");
        s = s.replaceAll("\'", "\\\\\'");
        s = s.replaceAll("\"", "\\\\\"");
        return sb.append(leftSymbol).append(s).append(rightSymbol).toString();
    }

    public static String prepareArrayToPrint(Object arrayToPrint,
                                             Map prepared) throws Throwable {

        Object[] array;
        try {
            array = (Object[]) arrayToPrint;
        } catch (ClassCastException ex) {
            array = new Object[Array.getLength(arrayToPrint)];
            for (int i = 0; i < array.length; i++) {
                array[i] = Array.get(arrayToPrint, i);
            }
        }

        StringBuilder sb = new StringBuilder();

        sb.append(array.length);
        sb.append(Constants.leftBrace);

        for (int i = 0; i < array.length; ++i) {
            sb.append(wrap(prepareObjectToPrint(array[i], prepared),
                           Constants.doubleQuote, Constants.doubleQuote));
            if (i != array.length - 1) {
                sb.append(Constants.comma).append(Constants.smallIndent);
            }
        }

        sb.append(Constants.rightBrace);
        return sb.toString();
    }

    public static String prepareObjectToPrint(Object objectToPrint,
                                              Map prepared) throws Throwable {

        if (objectToPrint == null) {
            return Constants.nullConst;
        }

        if (prepared.containsKey(objectToPrint)) {
            throw new RuntimeException("Error: " + objectToPrint
                                       + " has cyclic reference.");
        }
        prepared.put(objectToPrint, null);
        Class objectToPrintClass = objectToPrint.getClass();

        if (isPrimitiveOrWrappedType(objectToPrintClass)) {
            return objectToPrint.toString();
        } else if (objectToPrintClass.equals(String.class)) {
            return wrap(objectToPrint.toString(), Constants.doubleQuote,
                        Constants.doubleQuote);
        } else if (objectToPrintClass.isEnum()) {
            return ((Enum) objectToPrint).name();
        } else if (objectToPrintClass.isArray()) {
            return prepareArrayToPrint(objectToPrint, prepared);
        } else {
            return wrap(objectToPrint.toString(), Constants.leftSquareBracket,
                        Constants.rightSquareBracket);
        }
    }

    public static boolean prepareArgs(Object[] args, StringBuilder sb,
                                      Map prepared) throws Throwable {

        boolean isLongOutputMode = false;
        String[] result = new String[args.length];

        for (int i = 0; i < args.length; ++i) {
            result[i] = prepareObjectToPrint(args[i], prepared);
            if (result[i].length() > Constants.maxArgLen) {
                isLongOutputMode = true;
            }
        }

        if (isLongOutputMode) {
            sb.append(Constants.endl);
        }

        for (int i = 0; i < args.length; ++i) {
            if (isLongOutputMode) {
                sb.append(Constants.bigIndent);
            }
            sb.append(result[i]);
            if (i < args.length - 1) {
                sb.append(Constants.comma);
                if (!isLongOutputMode) {
                    sb.append(Constants.smallIndent);
                }
            }
            if (isLongOutputMode) {
                sb.append(Constants.endl);
            }
        }
        return isLongOutputMode;
    }

    public static void prepareExceptionToPrint(Throwable ex, StringBuilder sb,
                                               boolean isLongOutputMode
                                               ) throws Throwable {

        ex = ex.getCause();
        sb.append(Constants.threw).append(ex.getClass().getSimpleName());
        sb.append(Constants.colon).append(Constants.smallIndent);
        sb.append(ex.getMessage()).append(Constants.endl);

        StackTraceElement[] elements = ex.getStackTrace();
        for (StackTraceElement element : elements) {
            if (isLongOutputMode) {
                sb.append(Constants.bigIndent);
            }
            sb.append(Constants.bigIndent).append(element.toString());
            sb.append(Constants.endl);
        }
        throw ex;
    }

}
