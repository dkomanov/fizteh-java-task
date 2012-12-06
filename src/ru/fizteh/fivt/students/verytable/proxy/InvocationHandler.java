package ru.fizteh.fivt.students.verytable.proxy;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

public class InvocationHandler implements java.lang.reflect.InvocationHandler {

    private final Object target;
    private Appendable writer;

    InvocationHandler(Object target, Appendable writer) {
        this.target = target;
        this.writer = writer;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method == null) {
            throw new IllegalArgumentException("Error: empty method.");
        }
        if (method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(target, args);
        }
        method.setAccessible(true);

        StringBuilder sb = new StringBuilder();
        Map prepared = Collections.synchronizedMap(new IdentityHashMap());

        sb.append(method.getDeclaringClass().getSimpleName()).append(Constants.dot);
        sb.append(method.getName()).append(Constants.leftBracket);

        boolean isLongOutputMode = HandlerUtils.prepareArgs(args, sb, prepared);

        if (isLongOutputMode) {
            sb.append(Constants.bigIndent).append(Constants.rightBracket);
            sb.append(Constants.endl).append(Constants.smallIndent);
        } else {
            sb.append(Constants.rightBracket);
        }

        try {
            Object result = method.invoke(target, args);
            prepared.clear();
            sb.append(Constants.returned);
            sb.append(HandlerUtils.prepareObjectToPrint(result, prepared));
            sb.append(Constants.endl);
            return result;
        } catch (Throwable ex) {
            HandlerUtils.prepareExceptionToPrint(ex, sb, isLongOutputMode);
            return null;
        } finally {
            writer.append(sb.toString());
        }
    }

}
