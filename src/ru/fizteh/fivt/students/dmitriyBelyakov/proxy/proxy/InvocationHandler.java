package ru.fizteh.fivt.students.dmitriyBelyakov.proxy.proxy;

import ru.fizteh.fivt.students.dmitriyBelyakov.proxy.ProxyUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class InvocationHandler implements java.lang.reflect.InvocationHandler {
    private Object[] targets;

    InvocationHandler(Object[] targets) {
        this.targets = targets;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        if (proxy == null || method == null) {
            throw new NullPointerException();
        }
        if ((args == null || args.length == 0) && !ProxyUtils.isCollect(method)) {
            throw new RuntimeException("One of arguments must be int or long.");
        }
        method.setAccessible(true);
        if (ProxyUtils.isDoNotProxy(method)) {
            throw new RuntimeException("This method not for proxy.");
        }
        if (ProxyUtils.isCollect(method)) {
            Class returnType = method.getReturnType();
            if (returnType.equals(void.class)) {
                for (Object target : targets) {
                    method.invoke(target, args);
                }
                return null;
            } else if (returnType.equals(int.class) || returnType.equals(Integer.class)) {
                int result = 0;
                for (Object target : targets) {
                    result += (Integer) method.invoke(target, args);
                }
                return result;
            } else if (returnType.equals(long.class) || returnType.equals(Long.class)) {
                long result = 0;
                for (Object target : targets) {
                    result += (Long) method.invoke(target, args);
                }
                return result;
            } else if (returnType.isAssignableFrom(List.class)) {
                List result = new ArrayList();
                for (Object target : targets) {
                    result.addAll((List) method.invoke(target, args));
                }
                return result;
            }
        }
        long num = ProxyUtils.getFirstIntOrLongArgument(args);
        return method.invoke(targets[(int) (num % targets.length)], args);
    }
}