package ru.fizteh.fivt.students.levshinNikolay.proxy;

import java.lang.reflect.*;
import java.util.*;
import java.io.*;


/**
 * Levshin Nikolay
 * MIPT FIVT 196
 */
public class MyInvocationHandler implements InvocationHandler {
    Object target;
    Appendable writer;
    boolean soooLong = false;

    public MyInvocationHandler(Object target, Appendable writer) {
        this.target = target;
        this.writer = writer;
    }

    public String replace(String text) {
        text = text.replaceAll("\\\\", "\\\\\\\\");
        text = text.replaceAll("\n", "\\\\n");
        text = text.replaceAll("\r", "\\\\r");
        text = text.replaceAll("\b", "\\\\b");
        text = text.replaceAll("\t", "\\\\t");
        text = text.replaceAll("\f", "\\\\f");
        text = text.replaceAll("\"", "\\\\\"");
        return text;
    }

    public boolean isPrimitive(Class someClass) {
        return (someClass.isPrimitive() || someClass.equals(Double.class) || someClass.equals(Character.class)
                || someClass.equals(Byte.class) || someClass.equals(Integer.class) || someClass.equals(Float.class)
                || someClass.equals(Long.class) || someClass.equals(Boolean.class) || someClass.equals(Short.class));
    }

    public String printObject(Object forPrint, Map<Object, Object> parsedObjects) {
        if (forPrint == null) {
            return "null";
        }
        if (parsedObjects.put(forPrint, new Object()) != null) {
            throw new RuntimeException("Object contains link to itself");
        }
        Class clazz = forPrint.getClass();
        if (isPrimitive(clazz)) {
            parsedObjects.remove(forPrint);
            return forPrint.toString();
        }
        if (clazz.isEnum()) {
            parsedObjects.remove(forPrint);
            return ((Enum) forPrint).name();
        }
        if (clazz.isArray()) {
            String ans = arrToString(forPrint, parsedObjects);
            parsedObjects.remove(forPrint);
            return ans;
        }
        if (clazz.equals(String.class)) {
            parsedObjects.remove(forPrint);
            return "\"" + replace(forPrint.toString()) + "\"";
        }
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        builder.append(replace(forPrint.toString()));
        builder.append(']');
        parsedObjects.remove(forPrint);
        return builder.toString();
    }


    public String arrToString(Object forPrint, Map<Object, Object> parsedObjects) {
        StringBuilder builder = new StringBuilder();
        Object[] objArray;
        try {
            objArray = (Object[]) forPrint;
        } catch (ClassCastException primitiveArray) {
            objArray = new Object[Array.getLength(forPrint)];
            for (int i = 0; i < objArray.length; i++) {
                objArray[i] = Array.get(forPrint, i);
            }
        }
        builder.append(objArray.length);
        builder.append('{');
        for (int i = 0; i < objArray.length; i++) {
            builder.append(printObject(objArray[i], parsedObjects));
            if (i != objArray.length - 1) {
                builder.append(", ");
            }
        }
        builder.append('}');
        return builder.toString();
    }

    public String parseArgs(Object[] args, Map<Object, Object> parsedObjects) {
        String[] parsedArgs = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            parsedArgs[i] = printObject(args[i], parsedObjects);
            if (parsedArgs[i].length() > 60) {
                soooLong = true;
            }
        }
        StringBuilder builder = new StringBuilder();
        if (soooLong) {
            builder.append('\n');
        }
        for (int i = 0; i < args.length; i++) {
            if (soooLong) {
                builder.append("  ");
            }
            builder.append(parsedArgs[i]);
            if (i < args.length - 1) {
                builder.append(',');
                if (!soooLong) {
                    builder.append(' ');
                }
            }
            if (soooLong) {
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
            } catch (Throwable ex) {
                throw ex.getCause();
            }
        }
        soooLong = false;
        StringBuilder logger = new StringBuilder();
        logger.append(method.getDeclaringClass().getSimpleName());
        logger.append('.');
        logger.append(method.getName());
        logger.append('(');
        if (args != null) {
            logger.append(parseArgs(args, parsedObjects));
        }
        if (soooLong) {
            logger.append("  ");
        }
        logger.append(')');
        if (soooLong) {
            logger.append("\n");
        }
        try {
            Object result = method.invoke(target, args);
            if (!method.getReturnType().equals(void.class)) {
                if (soooLong) {
                    logger.append(' ');
                }
                logger.append(" returned ");
                parsedObjects.clear();
                logger.append(printObject(result, parsedObjects));
                if (soooLong) {
                    logger.append('\n');
                }
            }
            if (!soooLong) {
                logger.append('\n');
            }
            return result;
        } catch (Throwable ex) {
            ex = ex.getCause();
            if (soooLong) {
                logger.append(' ');
            }
            logger.append(" threw ");
            logger.append(ex.getClass().getName());
            logger.append(": ");
            logger.append(ex.getMessage());
            logger.append('\n');
            StackTraceElement[] traceElements = ex.getStackTrace();
            for (StackTraceElement traceElement : traceElements) {
                if (soooLong) {
                    logger.append("  ");
                }
                logger.append("  at ");
                logger.append(traceElement.toString());
                logger.append('\n');
            }
            throw ex;
        } finally {
            writer.append(logger.toString());
        }
    }
}
