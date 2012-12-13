package ru.fizteh.fivt.students.yuliaNikonova.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;

public class InvocationHandler implements java.lang.reflect.InvocationHandler {
    Object[] targets;

    public InvocationHandler(Object[] targets) {
        this.targets = targets;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (proxy == null) {
            throw new IllegalArgumentException("Null proxy");
        }
        if (method == null) {
            throw new IllegalArgumentException("Null method");
        }
        method.setAccessible(true);
        if (method.getAnnotation(DoNotProxy.class) != null) {
            throw new IllegalArgumentException("Method has DoNotProxy annotation");
        }

        if (method.getAnnotation(Collect.class) == null) {
            for (Object obj : args) {
                if (obj.getClass().equals(int.class) || obj.getClass().equals(Integer.class)) {
                    try {
                        return method.invoke(targets[(Integer) obj % targets.length], args);
                    } catch (InvocationTargetException e) {
                        throw e.getTargetException();
                    }
                }

                if (obj.getClass().equals(long.class) || obj.getClass().equals(Long.class)) {
                    try {
                        return method.invoke(targets[(int) ((Long) obj % targets.length)], args);
                    } catch (InvocationTargetException e) {
                        throw e.getTargetException();
                    }
                }
            }
        } else {
            Class returnType = method.getReturnType();
            try {
                if (returnType.equals(void.class)) {
                    for (Object target : targets) {
                        method.invoke(target, args);
                    }
                    return null;
                }

                if (returnType.equals(int.class) || returnType.equals(Integer.class)) {
                    int res = 0;
                    for (Object target : targets) {
                        res += (Integer) method.invoke(target, args);
                    }

                    return res;
                }

                if (returnType.equals(long.class) || returnType.equals(Long.class)) {
                    long res = 0;
                    for (Object target : targets) {
                        res += (Long) method.invoke(target, args);
                    }

                    return res;
                }

                if (returnType.equals(List.class)) {
                    // System.out.println("LIST!!!!");
                    List res = new ArrayList();
                    for (Object target : targets) {
                        res.addAll((List) method.invoke(target, args));
                    }

                    return res;
                }
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }

            throw new IllegalArgumentException("Method has unsupported return type");
        }

        return null;
    }
}
