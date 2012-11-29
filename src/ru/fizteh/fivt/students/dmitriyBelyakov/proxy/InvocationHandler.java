package ru.fizteh.fivt.students.dmitriyBelyakov.proxy;

import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;

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
        method.setAccessible(true);
        if (method.getAnnotation(DoNotProxy.class) != null) {
            throw new RuntimeException("Incorrect method.");
        }
        if (method.getAnnotation(Collect.class) != null) {
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
        if (proxy == null || method == null) {
            throw new NullPointerException();
        }
        if (args == null || args.length == 0) {
            throw new RuntimeException("One of arguments must be int or long.");
        }
        for (Object arg : args) {
            Class clazz = arg.getClass();
            if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
                int num = (Integer) arg % targets.length;
                return method.invoke(targets[num], args);
            } else if (clazz.equals(long.class) || clazz.equals(Long.class)) {
                int num = (int) ((Long) arg % targets.length);
                return method.invoke(targets[num], args);
            }
        }
        throw new IllegalArgumentException("Incorrect arguments");
    }
}