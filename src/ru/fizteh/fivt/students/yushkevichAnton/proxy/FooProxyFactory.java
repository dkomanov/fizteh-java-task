package ru.fizteh.fivt.students.yushkevichAnton.proxy;

import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;
import ru.fizteh.fivt.proxy.ShardingProxyFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

public class FooProxyFactory implements ShardingProxyFactory {
    @Override
    public Object createProxy(Object[] targets, Class[] interfaces) {
        assertCorrectArgument(targets != null && targets.length > 0);
        assertCorrectArgument(interfaces != null && interfaces.length > 0);

        checkInterfaces(interfaces);
        checkTargets(targets, interfaces);

        ClassLoader classLoader = targets[0].getClass().getClassLoader();

        InvocationHandler invocationHandler = new FooHandler(targets);

        Object proxy = Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);

        return proxy;
    }

    protected boolean shouldCollect(Class classBar) {
        return classBar.equals(int.class) || classBar.equals(Integer.class) ||
               classBar.equals(long.class) || classBar.equals(Long.class) ||
               classBar.equals(void.class) || classBar.equals(Void.class) ||
               classBar.equals(List.class);
    }

    protected void assertCorrectArgument(boolean assertion) {
        if (!assertion) {
            throw new IllegalArgumentException();
        }
    }

    protected void checkTargets(Object[] targets, Class[] interfaces) {
        Set<Class> interfacesNeedToImplement = new HashSet<Class>();
        interfacesNeedToImplement.addAll(Arrays.asList(interfaces));

        for (Object target : targets) {
            boolean ok = false;
            for (Class interfaceBar : target.getClass().getInterfaces()) {
                if (interfacesNeedToImplement.contains(interfaceBar)) {
                    ok = true;
                }
            }
            assertCorrectArgument(ok);
        }
    }

    protected void checkInterfaces(Class[] interfaces) {
        for (Class interfaceBar : interfaces) {
            assertCorrectArgument(interfaceBar != null);

            Method[] methods = interfaceBar.getMethods();

            assertCorrectArgument(methods.length > 0);

            for (Method method : methods) {
                if (method.isAnnotationPresent(DoNotProxy.class)) {
                    continue;
                }
                if (method.isAnnotationPresent(Collect.class)) {
                    assertCorrectArgument(shouldCollect(method.getReturnType()));
                    continue;
                }
                boolean ok = false;
                for (Class<?> classBar : method.getParameterTypes()) {
                    if (classBar.equals(int.class) || classBar.equals(Integer.class) ||
                        classBar.equals(long.class) || classBar.equals(Long.class)) {
                        ok = true;
                    }
                }
                assertCorrectArgument(ok);
            }
        }
    }
}