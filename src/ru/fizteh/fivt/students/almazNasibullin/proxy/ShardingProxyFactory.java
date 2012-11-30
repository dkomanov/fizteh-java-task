package ru.fizteh.fivt.students.almazNasibullin.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;

/**
 * 30.11.12
 * @author almaz
 */

public class ShardingProxyFactory implements ru.fizteh.fivt.proxy.ShardingProxyFactory {

    public Object createProxy(Object[] targets, Class[] interfaces) {
        if (targets == null) {
            throw new IllegalArgumentException("Nullpointer targets");
        }
        if (interfaces == null) {
            throw new IllegalArgumentException("Nullpointer interfaces");
        }
        if (targets.length == 0) {
            throw new IllegalArgumentException("Empty targets");
        }
        if (interfaces.length == 0) {
            throw new IllegalArgumentException("Empty interfaces");
        }

        for (Object o : targets) {
            if (o == null) {
                throw new IllegalArgumentException("Nullpointer target");
            }
            Class[] allInterfaces = o.getClass().getInterfaces();
            Set<Class> curInterfaces = new HashSet<Class>(Arrays.asList(interfaces));
            boolean contains = false;
            for (Class clazz : allInterfaces) {
                if (curInterfaces.contains(clazz)) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                throw new IllegalArgumentException
                        ("Target implements no interface from interfaces");
            }
        }

        for (Class clazz : interfaces) {
            if (clazz == null) {
                throw new IllegalArgumentException("Nullpointer interface");
            }
            Method[] methods = clazz.getMethods();
            if (methods.length == 0) {
                throw new IllegalArgumentException("Interface has no method");
            }
            if (clazz.isInterface()) {
                for (Method m : methods) {
                    if (m.getAnnotation(DoNotProxy.class) == null) {
                        if (m.getAnnotation(Collect.class) == null) {
                            Set<Class> parameters = new HashSet<Class>
                                    (Arrays.asList(m.getParameterTypes()));
                            if (!parameters.contains(int.class) &&
                                    !parameters.contains(long.class) &&
                                    !parameters.contains(Integer.class) &&
                                    !parameters.contains(Long.class)) {
                                throw new IllegalArgumentException
                                        ("Bad Parameter Types of method");
                            }
                        } else {
                            Class returnClass = m.getReturnType();
                            if (!returnClass.equals(void.class) &&
                                    !returnClass.equals(int.class) &&
                                    !returnClass.equals(long.class) &&
                                    !returnClass.equals(Integer.class) &&
                                    !returnClass.equals(Long.class) &&
                                    !returnClass.equals(List.class)) {
                                throw new IllegalArgumentException("Incorrect return type of method");
                            }
                        }
                    }
                }
            } else {
                throw new IllegalArgumentException("Class is not an interface");
            }
        }
        return Proxy.newProxyInstance(interfaces[0].getClassLoader(), interfaces,
                new MyInvocationHandler(targets));
    }
}
