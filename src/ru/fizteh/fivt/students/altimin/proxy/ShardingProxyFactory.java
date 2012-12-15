package ru.fizteh.fivt.students.altimin.proxy;

import ru.fizteh.fivt.bind.XmlBinder;
import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * User: altimin
 * Date: 12/7/12
 * Time: 1:30 AM
 */
public class ShardingProxyFactory implements ru.fizteh.fivt.proxy.ShardingProxyFactory {

    private static Object safeInvoke(Method method, Object target, Object[] objects) throws Throwable {
        try {
            return method.invoke(target, objects);
        } catch (Exception e) {
            throw e.getCause();
        }
    }

    @Override
    public Object createProxy(Object[] targets, Class[] interfaces) {
        ProxyUtils.check(targets, interfaces);

        class InvocationHandler implements java.lang.reflect.InvocationHandler {

            private Object[] targets;

            InvocationHandler(Object[] targets) {
                this.targets = targets;
            }

            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                method.setAccessible(true);
                if (method.isAnnotationPresent(DoNotProxy.class)) {
                    throw new RuntimeException(
                            String.format(
                                    "It's impossible to call method %s with @DoNotProxy annotation",
                                    method.getName()
                            )
                    );
                }
                if (method.isAnnotationPresent(Collect.class)) {
                    Class returnType = method.getReturnType();
                    if (returnType.equals(void.class) || returnType.equals(Void.class)) {
                        for (Object target: targets) {
                            safeInvoke(method, target, objects);
                        }
                        return null;
                    }
                    if (returnType.equals(Integer.class) || returnType.equals(int.class)) {
                        int result = 0;
                        for (Object target: targets) {
                            result += (Integer) safeInvoke(method, target, objects);
                        }
                        return result;
                    }
                    if (returnType.equals(Long.class) || returnType.equals(long.class)) {
                        long result = 0;
                        for (Object target: targets) {
                            result += (Long) safeInvoke(method, target, objects);
                        }
                        return result;
                    }
                    if (List.class.isAssignableFrom(returnType)) {
                        List result = new ArrayList();
                        for (Object target: targets) {
                            result.addAll((List) safeInvoke(method,target, objects));
                        }
                        return result;
                    }
                    throw new RuntimeException("Unexpected return type of method with @Collect annotation");
                }
                if (o == null) {
                    throw new NullPointerException();
                }
                if (objects == null || objects.length == 0) {
                    throw new RuntimeException("Expected int or long argument");
                }
                for (Object argument: objects) {
                    Class clazz = argument.getClass();
                    if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
                        int targetIndex = ((Integer) argument) % targets.length;
                        return safeInvoke(method, targets[targetIndex], objects);
                    }
                    if (clazz.equals(long.class) || clazz.equals(Long.class)) {
                        int targetIndex = (int)(((Long) argument) % targets.length);
                        return safeInvoke(method,targets[targetIndex], objects);
                    }
                }
                throw new RuntimeException("Expected int or long argument");
            }
        }
        return Proxy.newProxyInstance(interfaces[0].getClassLoader(), interfaces, new InvocationHandler(targets));
    }
}
