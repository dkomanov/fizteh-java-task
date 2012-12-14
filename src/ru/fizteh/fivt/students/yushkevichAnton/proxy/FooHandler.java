package ru.fizteh.fivt.students.yushkevichAnton.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;

import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;
import ru.fizteh.fivt.proxy.ShardingProxyFactory;

class FooHandler implements InvocationHandler {
    private Object[] implementations;

    FooHandler(Object[] implementations) {
        this.implementations = implementations;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
        if (method.isAnnotationPresent(DoNotProxy.class)) {
            throw new IllegalAccessException("Cannot access this method");
        }
        if (method.isAnnotationPresent(Collect.class)) {
            Class returnType = method.getReturnType();
            if (returnType.equals(void.class) || returnType.equals(Void.class)) {
                for (Object implementation : implementations) {
                    method.invoke(implementation, arguments);
                }
                return null;
            }
            if (returnType.equals(Integer.class) || returnType.equals(int.class)) {
                int result = 0;
                for (Object implementation : implementations) {
                    result += (Integer) method.invoke(implementation, arguments);
                }
                return result;
            }
            if (returnType.equals(Long.class) || returnType.equals(long.class)) {
                long result = 0;
                for (Object implementation : implementations) {
                    result += (Long) method.invoke(implementation, arguments);
                }
                return result;
            }
            if (returnType.equals(List.class)) {
                List result = new ArrayList();
                for (Object implementation : implementations) {
                    result.add(method.invoke(implementation, arguments));
                }
                return result;
            }
            throw new UnsupportedOperationException("Unsupported return type");
        }
        // Everything is ok at this point
        for (Object argument : arguments) {
            Class classBar = argument.getClass();
            if (classBar.equals(int.class) || classBar.equals(Integer.class)) {
                int i = ((Integer) argument) % implementations.length;
                return method.invoke(implementations[i], arguments);
            }
            if (classBar.equals(long.class) || classBar.equals(Long.class)) {
                int i = (int) (((Long) argument) % implementations.length);
                return method.invoke(implementations[i], arguments);
            }
        }

        throw new AssertionError("Can't happen if used properly");
    }
}