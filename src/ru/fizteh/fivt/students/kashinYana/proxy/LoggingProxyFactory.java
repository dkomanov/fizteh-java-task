package ru.fizteh.fivt.students.kashinYana.proxy;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

/**
 * Kashinskaya Yana
 */

public class LoggingProxyFactory implements ru.fizteh.fivt.proxy.LoggingProxyFactory {

    LoggingProxyFactory() {

    }

    boolean possibleToString(Class classExample) {
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

    String print(Object args) {
        if (args == null) {
            return "null";
        } else if (possibleToString(args.getClass())) {
            return args.toString();
        } else if (args.getClass().equals(String.class)) {
            return "\"" + args + "\"";
        } else if (args.getClass().isEnum()) {
            Enum enumm = (Enum) args;
            return enumm.name();
        } else if (args.getClass().isArray()) {
            Object[] list = (Object[]) args;
            String answer;
            answer = list.length + "{";
            for (Object t : list) {
                answer += print(t);
            }
            answer += "}";
            return answer;
        } else if (args.getClass().equals(Object.class)) {
            return "[" + args.toString() + "]";
        }
        return "i don't know";
    }

    public Object createProxy(Object target, Appendable writer, Class... interfaces) {

        if (target == null || writer == null) {
            throw new IllegalArgumentException("Don't give me null argv");
        }

        class ConsoleLoggerInvocationHandler
                implements InvocationHandler {
            private final Object target;
            private Appendable append;

            ConsoleLoggerInvocationHandler(Object target, Appendable writer) {
                this.target = target;
                append = writer;
            }

            @Override
            public Object invoke(Object proxy, Method method,
                                 Object[] args) throws Throwable {
                String stringToLog = "";
                String nameMethod = method.toString();
                String forPrint = nameMethod.substring(0, nameMethod.indexOf("("));
                String[] nameMethods = forPrint.split("[\\s.]+");
                stringToLog += nameMethods[nameMethods.length - 2] + "." + nameMethods[nameMethods.length - 1];
                stringToLog += "(";

                ArrayList<String> answer = new ArrayList<String>();
                boolean isWide = false;
                for (int i = 0; i < args.length; i++) {
                    answer.add(print(args[i]));
                    if (answer.get(answer.size() - 1).length() > 60) {
                        isWide = true;
                    }
                }
                for (int i = 0; i < answer.size(); i++) {
                    if (i == 0 && isWide) {
                        stringToLog += "\n";
                    }
                    if (isWide) {
                        stringToLog += "  ";
                    }
                    stringToLog += answer.get(i);
                    if (i < answer.size() - 1) {
                        stringToLog += ",";
                    }
                    if (isWide) {
                        stringToLog += "\n";
                    } else {
                        if (i < answer.size() - 1) {
                            stringToLog += " ";
                        }
                    }
                }
                if (isWide) {
                    stringToLog += "  ";
                }
                stringToLog += ")";
                Object returned = new Object();
                try {
                    returned = method.invoke(target, args);
                    if (returned != null) {
                        if (isWide) {
                            stringToLog += "\n  ";
                        } else {
                            stringToLog += " ";
                        }
                        stringToLog += "returned ";
                        stringToLog += print(returned);
                    }
                } catch (Exception e) {
                    StringWriter writer = new StringWriter();
                    e.printStackTrace(new PrintWriter((writer)));
                    if (isWide) {
                        stringToLog += "\n  ";
                    } else {
                        stringToLog += " ";
                    }
                    stringToLog += writer.toString();
                } finally {
                    stringToLog += "\n";
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
