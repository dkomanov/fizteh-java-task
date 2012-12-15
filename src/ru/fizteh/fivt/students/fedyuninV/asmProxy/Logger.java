package ru.fizteh.fivt.students.fedyuninV.asmProxy;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class Logger {
    private Appendable writer;
    private boolean tooLong;
    private final int ARG_MAX_LENGTH = 60;
    private Map<String, String> screenMap = new HashMap<>();
    private final Object PARSED = new Object();

    public Logger(Appendable writer) {
        this.writer = writer;
        screenMap.put("\n", "\\\\n");
        screenMap.put("\r", "\\\\r");
        screenMap.put("\b", "\\\\b");
        screenMap.put("\t", "\\\\t");
        screenMap.put("\f", "\\\\f");
        screenMap.put("\"", "\\\\\"");
    }

    private String screen(String s) {
        s = s.replaceAll("\\\\", "\\\\\\\\");
        for (Map.Entry<String, String> it: screenMap.entrySet()) {
            s = s.replaceAll(it.getKey(), it.getValue());
        }
        return s;
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


    private String wrapInQuotes(String s) {
        StringBuilder builder = new StringBuilder();
        builder.append('\"');
        builder.append(screen(s));
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
            if (parsedArgs[i].length() > ARG_MAX_LENGTH) {
                tooLong = true;
            }
        }
        StringBuilder builder = new StringBuilder();
        if (tooLong) {
            builder.append('\n');
        }
        for (int i = 0; i < args.length; i++) {
            if (tooLong) {
                builder.append("  ");
            }
            builder.append(parsedArgs[i]);
            if (i < args.length - 1) {
                builder.append(',');
                if (!tooLong) {
                    builder.append(' ');
                }
            }
            if (tooLong) {
                builder.append('\n');
            }
        }
        return builder.toString();
    }

    public void appendMethodAndArgs(String className, String methodName, Object args[]) {
        Map<Object, Object> parsedObjects = new IdentityHashMap<>();
        tooLong = false;
        try {
            writer.append(className);
            writer.append('.');
            writer.append(methodName);
            writer.append('(');
            if (args != null) {
                writer.append(parseArgs(args, parsedObjects));
            }
            if (tooLong) {
                writer.append("  ");
            }
        } catch (Exception ignored) {
        }
    }

    public void appendResult(Object result) {
        Map<Object, Object> parsedObjects = new IdentityHashMap<>();
        try {
            if (tooLong) {
                writer.append(' ');
            }
            writer.append(" returned ");
            parsedObjects.clear();
            writer.append(printObject(result, parsedObjects));
            if (tooLong) {
                writer.append('\n');
            }
        } catch (Exception ignored) {

        }
    }

    public void appendThrowable(Throwable ex) {
        try {
            if (tooLong) {
                writer.append(' ');
            }
            writer.append(" threw ");
            writer.append(ex.getClass().getName());
            writer.append(": ");
            writer.append(ex.getMessage());
            writer.append('\n');
            StackTraceElement[] traceElements = ex.getStackTrace();
            for (StackTraceElement traceElement: traceElements) {
                if (tooLong) {
                    writer.append("  ");
                }
                writer.append("  ");
                writer.append(traceElement.toString());
                writer.append('\n');
            }
        } catch (Exception ignored) {
        }
    }
}
