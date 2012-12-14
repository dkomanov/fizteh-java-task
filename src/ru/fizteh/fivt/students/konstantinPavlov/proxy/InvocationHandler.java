package ru.fizteh.fivt.students.konstantinPavlov.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;

public class InvocationHandler implements java.lang.reflect.InvocationHandler {
    private final Object[] targets;

    InvocationHandler(Object[] targets) {
        this.targets = targets;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {

        if (method == null) {
            throw new RuntimeException("null method");
        }

        if (method.getAnnotation(DoNotProxy.class) != null) {
            throw new RuntimeException("impossible to proxy this method");
        }

        method.setAccessible(true);

        if (method.getAnnotation(Collect.class) != null) {
            try {
                Class<?> returningType = method.getReturnType();
                if (returningType.equals(void.class)) {
                    for (Object target : targets) {
                        method.invoke(target, args);
                    }
                    return null;
                } else {
                    if (returningType.equals(Long.class)
                            || returningType.equals(long.class)) {
                        long result = 0;
                        for (Object target : targets) {
                            result += (Long) method.invoke(target, args);
                        }
                        return result;
                    } else {
                        if (returningType.equals(Integer.class)
                                || returningType.equals(int.class)) {
                            int result = 0;
                            for (Object target : targets) {
                                result += (Integer) method.invoke(target, args);
                            }
                            return result;
                        } else {
                            if (returningType.isAssignableFrom(List.class)) {
                                List<?> result = new ArrayList<>();
                                for (Object target : targets) {
                                    result.addAll((List) method.invoke(target,
                                            args));
                                }
                                return result;
                            } else {
                                throw new IllegalArgumentException(
                                        "invalid method return type");
                            }
                        }
                    }
                }
            } catch (InvocationTargetException expt) {
                throw expt.getTargetException();
            }
        }

        if (proxy == null) {
            throw new RuntimeException("null proxy");
        }

        if (args == null || args.length == 0) {
            throw new RuntimeException(
                    "incorrect arguments: there must be at least one int or long argument");
        }

        for (Object arg : args) {
            try {
                Class<?> clazz = arg.getClass();
                if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
                    return method.invoke(
                            targets[(Integer) arg % targets.length], args);
                } else {
                    if (clazz.equals(long.class) || clazz.equals(Long.class)) {
                        return method.invoke(
                                targets[(int) ((Long) arg % targets.length)],
                                args);
                    }
                }
            } catch (InvocationTargetException expt) {
                throw expt.getTargetException();
            }
        }

        throw new IllegalArgumentException("invalid type of the argument");
    }
}