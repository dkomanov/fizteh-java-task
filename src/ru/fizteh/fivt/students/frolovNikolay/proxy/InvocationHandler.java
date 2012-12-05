package ru.fizteh.fivt.students.frolovNikolay.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import ru.fizteh.fivt.students.frolovNikolay.PrimitiveTester;

public class InvocationHandler implements java.lang.reflect.InvocationHandler {
    private Object target;
    private Appendable writer;
    
    public InvocationHandler(Object target, Appendable writer) {
        this.target = target;
        this.writer = writer;
    }
    
    private void escapingString(String arg, StringBuilder stream) {
        for (int i = 0; i < arg.length(); ++i) {
            if (arg.charAt(i) == '\t') {
                stream.append("\\t");
            } else if (arg.charAt(i) == '\b') {
                stream.append("\\b");
            } else if (arg.charAt(i) == '\n') {
                stream.append("\\n");
            } else if (arg.charAt(i) == '\r') {
                stream.append("\\r");
            } else if (arg.charAt(i) == '\f') {
                stream.append("\\f");
            } else if (arg.charAt(i) == '\'') {
                stream.append("\\\'");
            } else if (arg.charAt(i) == '\"') {
                stream.append("\\\"");
            } else if (arg.charAt(i) == '\\') {
                stream.append("\\\\");
            } else {
                stream.append(arg.charAt(i));
            }
        }
    }
    
    private void specialToString(Class<?> clazz, Object arg, StringBuilder stream,
            IdentityHashMap<Object, Object> cycleInterrupter) {
        if (arg == null) {
            stream.append("null");
        } else {
            if (cycleInterrupter.containsKey(arg)) {
                throw new RuntimeException("cyclical references");
            }
            cycleInterrupter.put(arg, null);
            if (clazz.isArray()) {
            Object[] args = (Object[]) arg;
            stream.append(args.length + "{");
            for (int i = 0; i < args.length; ++i) {
                specialToString(args[i].getClass(), args[i], stream, cycleInterrupter);
                if (i + 1 != args.length) {
                    stream.append(", ");
                }
            }
            stream.append("}");
            } else if (clazz.equals(String.class)) {
                stream.append("\"");
                escapingString(arg.toString(), stream);
                stream.append("\"");
            } else if (clazz.isEnum()) {
                stream.append(((Enum) arg).name());
            } else if (PrimitiveTester.isPrimitive(clazz)) {
                stream.append(arg.toString());
            } else if (clazz.equals(Object.class)) {
                stream.append("[");
                escapingString(arg.toString(), stream);
                stream.append("]");
            } else {
                throw new RuntimeException("unsupported class: " + clazz.getSimpleName());
            }
        }
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method == null) {
            throw new RuntimeException("method: null pointer");
        }
        method.setAccessible(true);
        boolean isExtendedOutput = false;
        Class<?>[] parameters = method.getParameterTypes();
        ArrayList<String> parametersNames = new ArrayList<String>();
        for (int i = 0; i < parameters.length; ++i) {
            IdentityHashMap<Object, Object> cycleInterrupter = new IdentityHashMap<Object, Object>(); 
            StringBuilder stream = new StringBuilder();
            specialToString(parameters[i], args[i], stream, cycleInterrupter);
            String currentName = stream.toString();
            if (currentName.length() > 60) {
                isExtendedOutput = true;
            }
            parametersNames.add(currentName);
        }
        
        writer.append(method.getDeclaringClass().getSimpleName() + ".");
        writer.append(method.getName() + "(");
        if (isExtendedOutput) {
            writer.append("\n");
        }
        for (int i = 0; i < parametersNames.size(); ++i) {
            if (isExtendedOutput) {
                writer.append("  ");
            }
            writer.append(parametersNames.get(i));
            if (i + 1 != parametersNames.size()) {
                writer.append(",");
            }
            if (isExtendedOutput) {
                writer.append("\n");
            } else if (i + 1 != parametersNames.size()) {
                writer.append(" ");
            }
        }
        if (isExtendedOutput) {
            writer.append("  ");
        }
        writer.append(")");
        if (isExtendedOutput) {
            writer.append("\n");
        } else {
            writer.append(" ");
        }
        
        Object result = null;
        try {
            result = method.invoke(target, args);
        } catch (Throwable exception) {
            if (isExtendedOutput) {
                writer.append("  ");
            }
            writer.append(exception.getClass().getCanonicalName() + ": " + exception.getMessage() + '\n');
            if (isExtendedOutput) {
                writer.append("  ");
            }
            writer.append("  ");
            writer.append(exception.getStackTrace()[0].toString() + "\n");
            throw exception;
            
        }
        if (!method.getReturnType().equals(void.class) && !method.getReturnType().equals(Void.class)) {
            StringBuilder stream = new StringBuilder();
            IdentityHashMap<Object, Object> cycleInterrupter = new IdentityHashMap<Object, Object>();
            specialToString(result.getClass(), result, stream, cycleInterrupter);
            if (isExtendedOutput) {
                writer.append("  ");
            }
            writer.append("returned " + stream.toString());
        }
        writer.append("\n");
        return result;
    }
}