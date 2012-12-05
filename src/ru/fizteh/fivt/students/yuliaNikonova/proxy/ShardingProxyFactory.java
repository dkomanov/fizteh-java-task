package ru.fizteh.fivt.students.yuliaNikonova.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;

public class ShardingProxyFactory implements ru.fizteh.fivt.proxy.ShardingProxyFactory {

    @Override
    public Object createProxy(Object[] targets, Class[] interfaces) {
        if (interfaces == null || interfaces.length == 0) {
            throw new IllegalArgumentException("Empty interfaces");
        }

        if (targets == null || targets.length == 0) {
            throw new IllegalArgumentException("Empty targets");
        }

        for (Class<?> clazz : interfaces) {
            if (clazz == null) {
                throw new IllegalArgumentException("Null interface: " + clazz);
            }

            if (clazz.getMethods().length == 0) {
                throw new IllegalArgumentException(clazz + " interface without methods");
            }

            if (!clazz.isInterface()) {
                throw new IllegalArgumentException(clazz + "is not an interface");
            }

            for (Method method : clazz.getMethods()) {
                if (method.getAnnotation(DoNotProxy.class) == null) {
                    Class<?> type = method.getReturnType();
                    if (method.getAnnotation(Collect.class) != null) {
                        if (type.equals(void.class) || type.equals(int.class) || type.equals(Integer.class) || type.equals(long.class) || type.equals(Long.class) || type.equals(List.class)) {
                            continue;
                        }

                        throw new IllegalStateException("Unsupported method return type " + type);
                    }

                    Set<Class<?>> types = new HashSet<Class<?>>(Arrays.asList(method.getParameterTypes()));
                    if (!(types.contains(int.class) || types.contains(long.class) || types.contains(Integer.class) || types.contains(Long.class))) {
                        throw new IllegalArgumentException("Arguments type doesn't contain integer numbers");
                    }
                }
            }
        }

        for (Object target : targets) {
            if (target == null) {
                throw new IllegalArgumentException("Null target");
            }

            boolean found = false;
            Set<Class> interfacesSet = new HashSet<Class>(Arrays.asList(interfaces));
            for (Class<?> clazz : target.getClass().getInterfaces()) {
                if (interfacesSet.contains(clazz)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                throw new IllegalArgumentException("No interface for target");
            }
        }

        return Proxy.newProxyInstance(interfaces[0].getClassLoader(), interfaces, new InvocationHandler(targets));
    }
}
