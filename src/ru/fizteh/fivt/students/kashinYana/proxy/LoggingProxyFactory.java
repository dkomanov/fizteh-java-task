package ru.fizteh.fivt.students.kashinYana.proxy;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.IdentityHashMap;

/**
 * Kashinskaya Yana
 */

public class LoggingProxyFactory implements ru.fizteh.fivt.proxy.LoggingProxyFactory {

    public LoggingProxyFactory() {

    }

    private void recursionGetInterfaces(HashSet<Class<?>> sortedInterfaces, ArrayList<Class<?>> interfaces) {
        for (Class<?> iterface : interfaces) {
            if (!sortedInterfaces.contains(iterface)) {
                sortedInterfaces.add(iterface);
                recursionGetInterfaces(sortedInterfaces,
                        new ArrayList<Class<?>>(Arrays.asList(iterface.getInterfaces())));
            }
        }
    }

    public Object createProxy(Object target, Appendable writer, Class... interfaces) {

        if (target == null) {
            throw new IllegalArgumentException("Don't give me null targer, 1-args");
        }
        if (writer == null) {
            throw new IllegalArgumentException("Don't give me null writer, 2-args");
        }
        if (interfaces == null) {
            throw new IllegalArgumentException("Don't give me null interfaces, 3-args");
        }
        if (interfaces.length == 0) {
            throw new IllegalArgumentException("Don't give me empty interfaces");
        }
        ArrayList<Class<?>> interfacesTarget;
        HashSet<Class<?>> sortedInterfaces;
        try {
            interfacesTarget = new ArrayList<Class<?>>(Arrays.asList(target.getClass().getInterfaces()));
            sortedInterfaces = new HashSet<Class<?>>();
            recursionGetInterfaces(sortedInterfaces, interfacesTarget);
        } catch (Exception e) {
            throw new IllegalArgumentException("Don't take interfaces");
        }
        for (Class<?> iterface : interfaces) {
            if (iterface == null) {
                throw new IllegalArgumentException("interface null");
            }
            if (iterface.getMethods().length == 0) {
                throw new IllegalArgumentException("Interface have got several methods");
            }
            if (!sortedInterfaces.contains(iterface)) {
                throw new IllegalArgumentException("target doesn't implement: " + iterface.getSimpleName());
            }
        }

        class ConsoleLoggerInvocationHandler implements InvocationHandler {

            private final Object target;
            private Appendable append;

            ConsoleLoggerInvocationHandler(Object target, Appendable writer) {
                this.target = target;
                append = writer;
            }

            boolean isPrimitiveType(Class classExample) {
                return (classExample.isPrimitive()
                        || classExample.equals(Integer.class)
                        || classExample.equals(Boolean.class)
                        || classExample.equals(Double.class)
                        || classExample.equals(Float.class)
                        || classExample.equals(Byte.class)
                        || classExample.equals(Long.class)
                        || classExample.equals(Short.class)
                        || classExample.equals(Character.class));
            }


            String screening(String string) {
                string = string.replaceAll("\\\\", "\\\\\\\\");    //это действительно так пишется, тестрировано
                string = string.replaceAll("\n", "\\\\n");
                string = string.replaceAll("\t", "\\\\t");
                string = string.replaceAll("\f", "\\\\f");
                string = string.replaceAll("\"", "\\\\\"");
                string = string.replaceAll("\b", "\\\\b");
                string = string.replaceAll("\r", "\\\\r");
                return string;
            }

            String print(Object args, IdentityHashMap<Object, Object> cycle) throws IllegalAccessException {
                if (args == null) {
                    return "null";
                }
                if (cycle.containsKey(args)) {
                    throw new RuntimeException("I found cycle indent.");
                } else {
                    cycle.put(args, null);
                }
                if (isPrimitiveType(args.getClass())) {
                    return args.toString();
                } else if (args.getClass().equals(String.class)) {
                    return "\"" + screening(args.toString()) + "\"";
                } else if (args.getClass().isEnum()) {
                    Enum enumm = (Enum) args;
                    return enumm.name();
                } else if (args.getClass().isArray()) {
                    int size = Array.getLength(args);
                    String answer;
                    answer = size + "{";
                    for (int i = 0; i < size; i++) {
                        IdentityHashMap<Object, Object> arrayCycleForChildren =
                                new IdentityHashMap<Object, Object>(cycle);
                        answer += print(Array.get(args, i), arrayCycleForChildren);
                        if (i < size - 1) {
                            answer += ", ";
                        }
                    }
                    answer += "}";
                    return answer;
                } else if (args.equals(Object.class)) {
                    return "[" + screening(args.toString()) + "]";
                } else {
                    throw new IllegalAccessException("unknown class");
                }
            }

            @Override
            public Object invoke(Object proxy, Method method,
                                 Object[] args) throws Throwable {

                if (method == null) {
                    throw new IllegalArgumentException("method is null");
                }
                if (method.getName().equals("equals") || method.getName().equals("hashCode") ||
                        method.getName().equals("toString")) {
                    return method.invoke(target, args);
                }
                method.setAccessible(true);

                String stringToLog = "";
                stringToLog += method.getDeclaringClass().getSimpleName() + ".";
                stringToLog += method.getName() + "(";


                ArrayList<String> answer = new ArrayList<String>();
                boolean isWiden = false;

                if (args != null) {
                    for (int i = 0; i < args.length; i++) {
                        answer.add(print(args[i], new IdentityHashMap<Object, Object>()));
                        if (answer.get(answer.size() - 1).length() > 60) {
                            isWiden = true;
                        }
                    }
                }

                if (isWiden) {
                    stringToLog += "\n";
                }

                for (int i = 0; i < answer.size(); i++) {
                    if (isWiden) {
                        stringToLog += "  ";
                    }
                    stringToLog += answer.get(i);
                    if (i < answer.size() - 1) {
                        stringToLog += ",";
                    }
                    if (isWiden) {
                        stringToLog += "\n";
                    } else if (i < answer.size() - 1) {
                        stringToLog += " ";
                    }
                }
                if (isWiden) {
                    stringToLog += "  ";
                }
                stringToLog += ")";


                Object returned;
                try {
                    returned = method.invoke(target, args);

                    if (!method.getReturnType().equals(void.class) && !method.getReturnType().equals(Void.class)) {
                        if (isWiden) {
                            stringToLog += "\n  ";
                        } else {
                            stringToLog += " ";
                        }
                        stringToLog += "returned ";
                        stringToLog += print(returned, new IdentityHashMap<Object, Object>()) + "\n";
                    } else {
                        stringToLog += "\n";
                    }
                } catch (InvocationTargetException e) {
                    if (isWiden) {
                        stringToLog += "\n  ";
                    } else {
                        stringToLog += " ";
                    }
                    stringToLog += "threw ";
                    stringToLog += e.getTargetException().getClass().getCanonicalName() + ": "
                            + e.getTargetException().getMessage() + '\n';
                    StackTraceElement[] traceElements = e.getTargetException().getStackTrace();
                    for (int i = 0; i < Math.min(2, traceElements.length); i++) {
                        if (isWiden) {
                            stringToLog += "  ";
                        }
                        stringToLog += "  ";
                        stringToLog += traceElements[i].toString() + "\n";
                    }
                    append.append(stringToLog);
                    throw e.getTargetException();
                }
                append.append(stringToLog);
                return returned;
            }
        }

        Object instance = Proxy.newProxyInstance(
                ClassLoader.getSystemClassLoader(),
                interfaces,
                new ConsoleLoggerInvocationHandler(target, writer)
        );

        return instance;
    }

}
