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

    private boolean isCollectableClass(Class clazz) {
        return clazz.equals(int.class) || clazz.equals(Integer.class)
                || clazz.equals(long.class) || clazz.equals(Long.class)
                || clazz.equals(void.class) || clazz.equals(Void.class)
                || clazz.equals(List.class);
    }

    private void checkInterface(Class iface) {
        if (iface == null) {
            throw new IllegalArgumentException("Null interface passed as argument");
        }
        Method[] methods = iface.getMethods();
        if (methods == null || methods.length == 0) {
            throw new IllegalArgumentException("Empty interface passed as argument");
        }
        for (Method method: methods) {
            if (method.isAnnotationPresent(DoNotProxy.class)) {
                continue;
            }
            if (method.isAnnotationPresent(Collect.class)) {
                if (!isCollectableClass(method.getReturnType())) {
                    throw new IllegalStateException(
                            String.format(
                                    "Incorrect annotation: return type of method %s of interface %s is not collectable",
                                    method.getName(),
                                    iface.getSimpleName()
                            )
                    );
                }
                continue;
            }
            List<Class<?>> parameterTypes = Arrays.asList(method.getParameterTypes());
            if (!(parameterTypes.contains(int.class) || parameterTypes.contains(Integer.class)
                    || parameterTypes.contains(long.class) || parameterTypes.contains(Long.class))) {
                throw new IllegalArgumentException(
                        String.format(
                                "Method %s of interface %s has no int/long parameter",
                                method.getName(),
                                iface.getSimpleName()
                        )
                );
            }
        }
    }

    private void checkObject(Object object, Class[] ifaces) {
        Set<Class> interfaces = new HashSet<Class>(Arrays.asList(ifaces));
        Class[] implementedInterfaces = object.getClass().getInterfaces();
        boolean foundInterface = false;
        for (Class iface: implementedInterfaces) {
            if (interfaces.contains(iface)) {
                foundInterface = true;
            }
        }
        if (!foundInterface) {
            throw new IllegalArgumentException(
                    String.format(
                            "Class %s doesn't implement any of interfaces",
                            object.getClass().getSimpleName()
                    )
            );
        }
    }

    @Override
    public Object createProxy(Object[] targets, Class[] interfaces) {
        if (interfaces == null || interfaces.length == 0) {
            throw new IllegalArgumentException("Interfaces is empty");
        }
        if (targets == null || targets.length == 0) {
            throw new IllegalArgumentException("Targets is empty");
        }
        for (Class iface: interfaces) {
            checkInterface(iface);
        }
        for (Object target: targets) {
            checkObject(target, interfaces);
        }

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
                            try {
                                method.invoke(target, objects);
                            } catch (Throwable e) {
                                throw e.getCause();
                            }
                        }
                        return null;
                    }
                    if (returnType.equals(Integer.class) || returnType.equals(int.class)) {
                        int result = 0;
                        for (Object target: targets) {
                            try {
                                result += (Integer) method.invoke(target, objects);
                            } catch (Throwable e) {
                                throw e.getCause();
                            }
                        }
                        return result;
                    }
                    if (returnType.equals(Long.class) || returnType.equals(long.class)) {
                        long result = 0;
                        for (Object target: targets) {
                            try {
                                result += (Long) method.invoke(target, objects);
                            } catch (Throwable e) {
                                throw e.getCause();
                            }
                        }
                        return result;
                    }
                    if (List.class.isAssignableFrom(returnType)) {
                        List result = new ArrayList();
                        for (Object target: targets) {
                            try {
                                result.addAll((List) method.invoke(target, objects));
                            } catch (Throwable e) {
                                throw e.getCause();
                            }
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
                        return method.invoke(targets[targetIndex], objects);
                    }
                    if (clazz.equals(long.class) || clazz.equals(Long.class)) {
                        int targetIndex = (int)(((Long) argument) % targets.length);
                        return method.invoke(targets[targetIndex], objects);
                    }
                }
                throw new RuntimeException("Expected int or long argument");
            }
        }
        return Proxy.newProxyInstance(interfaces[0].getClassLoader(), interfaces, new InvocationHandler(targets));
    }
}
