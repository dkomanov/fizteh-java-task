package ru.fizteh.fivt.students.tolyapro.proxy;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class InvocationHandler implements java.lang.reflect.InvocationHandler {

    Object target;
    Appendable writer;

    private String escapeString(String string) {
        String result = string;
        result = result.replaceAll("\t", "\\\\t");
        result = result.replaceAll("\b", "\\\\b");
        result = result.replaceAll("\n", "\\\\n");
        result = result.replaceAll("\r", "\\\\r");
        result = result.replaceAll("\f", "\\\\f");
        result = result.replaceAll("\'", "\\\\'");
        result = result.replaceAll("\"", "\\\\\"");
        return result;
    }

    private String toString(Object object) {
        if (object == null) {
            return "null";
        } else if (object.getClass().isArray()) {
            if (!object.getClass().getComponentType().isPrimitive()) {
                Object[] tmpArray = (Object[]) object;
                StringBuilder result = new StringBuilder();
                result.append(new Integer(tmpArray.length).toString());
                result.append('{');
                for (int i = 0; i < tmpArray.length; ++i) {
                    result.append(toString(tmpArray[i]));
                    if (i != tmpArray.length - 1) {
                        result.append(", ");
                    }
                }
                result.append('}');
                return result.toString();
            } else {
                int length = Array.getLength(object);
                Object[] outputArray = new Object[length];
                for (int i = 0; i < length; ++i) {
                    outputArray[i] = Array.get(object, i);
                }
                return toString(outputArray);
            }
        } else if (object.getClass().equals(Enum.class)) {
            return ((Enum) object).name();
        } else if (object.getClass().equals(String.class)) {
            return "\"" + escapeString((String) object) + "\"";
        } else if (ru.fizteh.fivt.students.tolyapro.proxy.DetecterOfPrimitiveTypes
                .isPrimitive(object)) {
            return object.toString();
        } else if (object.getClass().equals(Object.class)) {
            return "[" + escapeString(object.toString()) + "]";
        } else {
            throw new RuntimeException("Can't toString object properly "
                    + object.toString());
        }
    }

    public InvocationHandler(Object target, Appendable writer) {
        this.target = target;
        this.writer = writer;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        final int magicConst = 60;
        boolean extendedMode = false;
        String methodName = method.getName();
        if (methodName.length() > magicConst) {
            extendedMode = true;
        }
        ArrayList<String> argsAsStrings = new ArrayList<String>();
        if (args != null) {
            for (int i = 0; i < args.length; ++i) {
                if (args[i].toString().length() > magicConst) {
                    extendedMode = true;
                }
                argsAsStrings.add(toString(args[i]));
            }
        }
        writer.append(method.getDeclaringClass().getSimpleName() + '.');
        writer.append(method.getName() + '(');
        for (int i = 0; i < argsAsStrings.size(); ++i) {
            writer.append(argsAsStrings.get(i));
            if (i != argsAsStrings.size() - 1) {
                writer.append(", ");
            }
            if (extendedMode) {
                writer.append('\n');
            }
        }
        writer.append(") ");
        Object returned = null;
        try {
            returned = method.invoke(target, args);
            if (returned != null) {
                writer.append("returned ");
                writer.append(toString(returned));
            }
        } catch (Exception e) {
            writer.append('\n');
            StackTraceElement[] elements = e.getStackTrace();
            writer.append("threw"
                    + e.getClass().toString().replaceFirst("class", "")
                    + " Message:" + e.getMessage() + '\n');
            for (int i = 0; i < elements.length; ++i) {
                if (extendedMode) {
                    writer.append("  ");
                }
                writer.append("  at ");
                writer.append(elements[i].toString());
                if (i != elements.length - 1) {
                    writer.append('\n');
                }
            }
        }
        writer.append('\n');
        return returned;
    }
}
