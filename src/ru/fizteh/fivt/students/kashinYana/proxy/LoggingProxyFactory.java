package ru.fizteh.fivt.students.kashinYana.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Kashinskaya Yana
 */

public class LoggingProxyFactory implements ru.fizteh.fivt.proxy.LoggingProxyFactory {

    public LoggingProxyFactory() {

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
        ArrayList<Class<?>> interfacesTarget;
        HashSet<Class<?>> sortedInterfaces;
        try {
            interfacesTarget = new ArrayList<Class<?>>(Arrays.asList(target.getClass().getInterfaces()));
            sortedInterfaces = new HashSet<Class<?>>(interfacesTarget);
        } catch (Exception e) {
            throw new IllegalArgumentException("Don't take interfaces");
        }
        for (Class<?> iterface : interfaces) {
            if(iterface == null) {
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

            String print(Object args) throws IllegalAccessException {
                if (args == null) {
                    return "null";
                } else if (isPrimitiveType(args.getClass())) {
                    return args.toString();
                } else if (args.getClass().equals(String.class)) {
                    return "\"" + screening(args.toString()) + "\"";
                } else if (args.getClass().isEnum()) {
                    Enum enumm = (Enum) args;
                    return enumm.name();
                } else if (args.getClass().isArray()) {
                    Object[] list = (Object[]) args;
                    String answer;
                    answer = list.length + "{";
                    for (int i = 0; i < list.length; i++) {
                        answer += print(list[i]);
                        if (i < list.length - 1) {
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
                method.setAccessible(true);

                String stringToLog = "";
                stringToLog += method.getDeclaringClass().getSimpleName() + ".";
                stringToLog += method.getName() + "(";


                ArrayList<String> answer = new ArrayList<String>();
                boolean isWiden = false;

                if(args != null) {
                    for (int i = 0; i < args.length; i++) {
                        answer.add(print(args[i]));
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
                        stringToLog += print(returned) + "\n";
                    } else {
                        stringToLog += "\n";
                    }
                } catch (Exception e) {
                    if (isWiden) {
                        stringToLog += "\n  ";
                    } else {
                        stringToLog += " ";
                    }
                    stringToLog += e.getClass().getCanonicalName() + ": " + e.getMessage() + '\n';
                    StackTraceElement[] traceElements = e.getStackTrace();
                    for (StackTraceElement iterator : traceElements) {
                        if (isWiden) {
                            stringToLog += "  ";
                        }
                        stringToLog += "  ";
                        stringToLog += iterator.toString() + "\n";
                    }
                    throw  e;
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
