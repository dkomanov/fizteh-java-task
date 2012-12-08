package ru.fizteh.fivt.students.khusaenovTimur.proxy;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Timur
 * Date: 12/8/12
 * Time: 1:43 AM
 * To change this template use File | Settings | File Templates.
 */

public class InvocationHandler implements java.lang.reflect.InvocationHandler{
    private Appendable writer;
    private Object target;
    private boolean longFormat;
    private final int LENGTH_FOR_LONG_FORMAT = 60;
    private Map<String, String> screenMap = new HashMap<>();
    private final Object PARSED = new Object();

    public InvocationHandler(Object target, Appendable writer) {
        this.target = target;
        this.writer = writer;
        screenMap.put("\n", "\\\\n");
        screenMap.put("\r", "\\\\r");
        screenMap.put("\b", "\\\\b");
        screenMap.put("\t", "\\\\t");
        screenMap.put("\f", "\\\\f");
        screenMap.put("\"", "\\\\\"");
    }

    private String screen(String stringForScreen) {
        stringForScreen = stringForScreen.replaceAll("\\\\", "\\\\\\\\");
        for (Map.Entry<String, String> it: screenMap.entrySet()) {
            stringForScreen = stringForScreen.replaceAll(it.getKey(), it.getValue());
        }
        return stringForScreen;
    }

    private boolean isPrimitive(Class classExample) {
        return (classExample.isPrimitive()
                ||  classExample.equals(Integer.class)
                ||  classExample.equals(Boolean.class)
                ||  classExample.equals(Double.class)
                ||  classExample.equals(Float.class)
                ||  classExample.equals(Byte.class)
                ||  classExample.equals(Long.class)
                ||  classExample.equals(Short.class)
                ||  classExample.equals(Character.class));
    }


    private String wrapInQuotes(String string) {
        StringBuilder builder = new StringBuilder();
        builder.append('\"');
        builder.append(screen(string));
        builder.append('\"');
        return builder.toString();
    }

    private String arrayToString(Object toPrint, Map<Object, Object> parsedObjects) {
        StringBuilder builder = new StringBuilder();
        Object[] array;
        try {
            array = (Object[]) toPrint;
        } catch (ClassCastException primitiveArray) {
            array = new Object[Array.getLength(toPrint)];
            for (int i = 0; i < array.length; i++) {
                array[i] = Array.get(toPrint, i);
            }
        }
        builder.append(array.length);
        builder.append('{');
        for (int i = 0; i < array.length; i++) {
            builder.append(printObject(array[i], parsedObjects));
            if (i != array.length - 1) {
                builder.append(", ");
            }
        }
        builder.append('}');
        return builder.toString();
    }

    private String printObject(Object toPrint, Map<Object, Object> parsedObjects) {
        if (toPrint == null) {
            return "null";
        }
        if (parsedObjects.put(toPrint, PARSED) != null) {
            throw new RuntimeException("Object contains link to itself");
        }
        Class clazz = toPrint.getClass();
        if (isPrimitive(clazz)) {
            parsedObjects.remove(toPrint);
            return toPrint.toString();
        }
        if (clazz.isEnum()) {
            parsedObjects.remove(toPrint);
            return ((Enum) toPrint).name();
        }
        if (clazz.isArray()) {
            String ans = arrayToString(toPrint, parsedObjects);
            parsedObjects.remove(toPrint);
            return ans;
        }
        if (clazz.equals(String.class)) {
            parsedObjects.remove(toPrint);
            return wrapInQuotes(toPrint.toString());
        }
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        builder.append(screen(toPrint.toString()));
        builder.append(']');
        parsedObjects.remove(toPrint);
        return builder.toString();
    }

    private String parseArgs(Object[] args, Map<Object, Object> parsedObjects) {
        String[] parsedArgs = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            parsedArgs[i] = printObject(args[i], parsedObjects);
            if (parsedArgs[i].length() > LENGTH_FOR_LONG_FORMAT) {
                longFormat = true;
            }
        }
        StringBuilder builder = new StringBuilder();
        if (longFormat) {
            builder.append('\n');
        }
        for (int i = 0; i < args.length; i++) {
            if (longFormat) {
                builder.append("  ");
            }
            builder.append(parsedArgs[i]);
            if (i < args.length - 1) {
                builder.append(',');
                if (!longFormat) {
                    builder.append(' ');
                }
            }
            if (longFormat) {
                builder.append('\n');
            }
        }
        return builder.toString();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Map<Object, Object> parsedObjects = new IdentityHashMap<>();
        method.setAccessible(true);
        if (method.getDeclaringClass().equals(Object.class)) {
            try {
                return method.invoke(target, args);
            } catch (Throwable ignored) {
            }
        }
        longFormat = false;
        StringBuilder logger = new StringBuilder();
        logger.append(method.getDeclaringClass().getSimpleName());
        logger.append('.');
        logger.append(method.getName());
        logger.append('(');
        if (args != null) {
            logger.append(parseArgs(args, parsedObjects));
        }
        if (longFormat) {
            logger.append("  ");
        }
        logger.append(')');
        if (longFormat) {
            logger.append("\n");
        }
        try {
            Object result = method.invoke(target, args);
            if (!method.getReturnType().equals(void.class)) {
                if (longFormat) {
                    logger.append(' ');
                }
                logger.append(" returned ");
                parsedObjects.clear();
                logger.append(printObject(result, parsedObjects));
                if (longFormat) {
                    logger.append('\n');
                }
            }
            if (!longFormat) {
                logger.append('\n');
            }
            return result;
        } catch (Throwable ex) {
            ex = ex.getCause();
            if (longFormat) {
                logger.append(' ');
            }
            logger.append(" threw ");
            logger.append(ex.getClass().getName());
            logger.append(": ");
            logger.append(ex.getMessage());
            logger.append('\n');
            StackTraceElement[] traceElements = ex.getStackTrace();
            for (StackTraceElement traceElement: traceElements) {
                if (longFormat) {
                    logger.append("  ");
                }
                logger.append("  ");
                logger.append(traceElement.toString());
                logger.append('\n');
            }
            throw ex;
        } finally {
            writer.append(logger.toString());
        }
    }
}
