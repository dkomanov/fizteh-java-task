package ru.fizteh.fivt.students.nikitaAntonov.proxy;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.Map;
import ru.fizteh.fivt.students.nikitaAntonov.utils.Utils;

class InvocationHandler implements java.lang.reflect.InvocationHandler {

    static final int MAX_ARG_LEN = 60;

    private Object target;
    private Appendable writer;

    public InvocationHandler(Object target, Appendable writer) {
        this.target = target;
        this.writer = writer;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {

        method.setAccessible(true);

        if (method.getDeclaringClass().equals(Object.class)) {
            return invokeWithoutLogging(method, args);
        }

        StringBuilder log = new StringBuilder();
        boolean needIdent = writeCallInformation(log, method, args);

        try {
            Object result = method.invoke(target, args);
            if (!method.getReturnType().equals(void.class)) {
                if (!needIdent) {
                    log.append(' ');
                }

                writeIdentIfNeed(log, needIdent);
                log.append("returned ");
                writeObject(log, result, new IdentityHashMap<>());
            }

            log.append("\n");
            return result;
        } catch (Throwable ex) {
            ex = ex.getCause();
            writeIdentIfNeed(log, needIdent);
            if (!needIdent) {
                log.append(" ");
            }

            log.append("threw ").append(ex.getClass().getName()).append(": ")
                    .append(ex.getMessage()).append("\n");

            StackTraceElement[] traceElements = ex.getStackTrace();

            int stringStackTrace = 2;
            if (traceElements.length < stringStackTrace) {
                stringStackTrace = traceElements.length;
            }

            for (int i = 0; i < stringStackTrace; i++) {
                writeIdentIfNeed(log, needIdent);
                log.append(traceElements[i].toString());
            }
            log.append('\n');
            throw ex;
        } finally {
            writer.append(log);
        }
    }

    private boolean writeCallInformation(StringBuilder log, Method method,
            Object[] args) {
        log.append(method.getDeclaringClass().getSimpleName()).append('.')
                .append(method.getName()).append('(');

        boolean needIdent = false;
        if (args != null) {
            Map<Object, Object> alreadyFounded = new IdentityHashMap<>();
            needIdent = writeArgsInformation(log, args, alreadyFounded);
        }

        writeIdentIfNeed(log, needIdent);
        log.append(')');

        return needIdent;
    }

    private void writeIdentIfNeed(StringBuilder log, boolean needIdent) {
        final int ident = 2;

        if (!needIdent) {
            return;
        }

        log.append("\n");
        for (int i = 0; i < ident; ++i) {
            log.append(" ");
        }
    }

    private boolean writeArgsInformation(StringBuilder log, Object[] args,
            Map<Object, Object> already) {

        boolean needIdent = false;

        StringBuilder[] params = new StringBuilder[args.length];
        for (int i = 0; i < args.length; ++i) {
            params[i] = new StringBuilder();
            writeObject(params[i], args[i], already);
            if (params[i].length() > MAX_ARG_LEN) {
                needIdent = true;
            }
        }

        for (int i = 0; i < params.length; ++i) {
            writeIdentIfNeed(log, needIdent);
            log.append(params[i]);
            if (i + 1 != params.length) {
                log.append(", ");
            }
        }

        return needIdent;
    }

    private void writeObject(StringBuilder log, Object o,
            Map<Object, Object> already) {

        if (already.containsKey(o)) {
            throw new RuntimeException("Cyclic link detected!");
        }

        if (o == null) {
            log.append("null");
            return;
        }

        Class cl = o.getClass();

        if (isPrimitive(cl)) {
            log.append(o.toString());
            return;
        }

        if (cl.isEnum()) {
            log.append(((Enum) o).name());
            return;
        }

        if (cl.isArray()) {
            already.put(o, null);
            writeArray(log, o, already);
            already.remove(o);
            return;
        }

        if (cl.equals(String.class)) {
            log.append("\"").append(Utils.escapeJavaString(o.toString()))
                    .append("\"");
            return;
        }

        log.append("[").append(Utils.escapeJavaString(o.toString()))
                .append("]");
    }

    private void writeArray(StringBuilder log, Object o,
            Map<Object, Object> already) {

        int len = Array.getLength(o);

        log.append(len).append("{");

        for (int i = 0; i < len; ++i) {
            writeObject(log, Array.get(o, i), already);

            if (i + 1 < len) {
                log.append(", ");
            }
        }

        log.append("}");
    }

    private Object invokeWithoutLogging(Method method, Object args[]) throws Throwable {
        try {
            return method.invoke(target, args);
        } catch (Throwable e) {
            throw e.getCause();
        }
    }

    public static boolean isPrimitive(Class clazz) {
        return clazz.isPrimitive() || clazz.equals(Boolean.class)
                || clazz.equals(Byte.class) || clazz.equals(Character.class)
                || clazz.equals(Short.class) || clazz.equals(Integer.class)
                || clazz.equals(Long.class) || clazz.equals(Float.class)
                || clazz.equals(Double.class);
    }

}
