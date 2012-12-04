package ru.fizteh.fivt.students.frolovNikolay.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class InvocationHandler implements java.lang.reflect.InvocationHandler {
    private Object target;
    private Appendable writer;
    
    public InvocationHandler(Object target, Appendable writer) {
        this.target = target;
        this.writer = writer;
    }
    
    private boolean isPrimitive(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz.equals(Character.class) || clazz.equals(Short.class)
                || clazz.equals(Integer.class) || clazz.equals(Long.class)
                || clazz.equals(Float.class) || clazz.equals(Double.class)
                || clazz.equals(Boolean.class) || clazz.equals(Byte.class);
    }
    
    private void specialToString(Class<?> clazz, Object arg, StringBuilder stream) {
        if (arg == null) {
            stream.append("null");
        } else if (clazz.isArray()) {
            Object[] args = (Object[]) arg;
            stream.append(args.length + "{");
            for (int i = 0; i < args.length; ++i) {
                specialToString(args[i].getClass(), args[i], stream);
                if (i + 1 != args.length) {
                    stream.append(", ");
                }
            }
            stream.append("}");
        } else if (clazz.equals(String.class)) {
            stream.append("\\\"" + arg + "\\\"");
        } else if (clazz.isEnum()) {
            stream.append(((Enum) arg).name());
        } else if (isPrimitive(clazz)) {
            stream.append(arg.toString());
        } else if (clazz.equals(Object.class)) {
            stream.append('[' + arg.toString() + ']');
        }
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method == null) {
            throw new IllegalArgumentException("method: null pointer");
        }
        method.setAccessible(true);
        boolean isExtendedOutput = false;
        Class<?>[] parameters = method.getParameterTypes();
        ArrayList<String> parametersNames = new ArrayList<String>();
        for (int i = 0; i < parameters.length; ++i) {
            StringBuilder stream = new StringBuilder();
            specialToString(parameters[i], args[i], stream);
            String currentName = stream.toString();
            if (currentName.length() > 60) {
                isExtendedOutput = true;
            }
            parametersNames.add(currentName);
        }
        
        writer.append(method.getDeclaringClass().getSimpleName() + '.');
        writer.append(method.getName() + '(');
        if (isExtendedOutput) {
            writer.append('\n');
        }
        for (int i = 0; i < parametersNames.size(); ++i) {
            if (isExtendedOutput) {
                writer.append("  ");
            }
            writer.append(parametersNames.get(i));
            if (i + 1 != parametersNames.size()) {
                writer.append(',');
            }
            if (isExtendedOutput) {
                writer.append('\n');
            } else if (i + 1 != parametersNames.size()) {
                writer.append(' ');
            }
        }
        if (isExtendedOutput) {
            writer.append("  ");
        }
        writer.append(')');
        if (isExtendedOutput) {
            writer.append('\n');
        } else {
            writer.append(' ');
        }
        
        Object result = null;
        try {
            result = method.invoke(target, args);
        } catch (Throwable exception) {
            if (isExtendedOutput) {
                writer.append("  ");
            }
            writer.append(exception.getClass().getCanonicalName() + ": " + exception.getMessage() + '\n');
            StackTraceElement[] traceElements = exception.getStackTrace();
            for (StackTraceElement iter : traceElements) {
                if (isExtendedOutput) {
                    writer.append("  ");
                }
                writer.append("  ");
                writer.append(iter.toString() + '\n');
            }
            return null;
        }
        if (!method.getReturnType().equals(void.class) && !method.getReturnType().equals(Void.class)) {
            StringBuilder stream = new StringBuilder();
            specialToString(result.getClass(), result, stream);
            if (isExtendedOutput) {
                writer.append("  ");
            }
            writer.append("returned " + stream.toString());
        }
        writer.append('\n');
        return result;
    }
}