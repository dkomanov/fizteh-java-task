package ru.fizteh.fivt.students.konstantinPavlov.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;

public class ShardingProxyFactory implements
        ru.fizteh.fivt.proxy.ShardingProxyFactory {

    @Override
    public Object createProxy(Object[] targets, Class[] interfaces) {
        if (interfaces == null || interfaces.length == 0) {
            throw new IllegalArgumentException(
                    "there must be at least one interface");
        }
        if (targets == null || targets.length == 0) {
            throw new IllegalArgumentException(
                    "there must be at least one target");
        }

        for (Class<?> nextInterface : interfaces) {
            if (nextInterface == null || nextInterface.getMethods().length == 0
                    || !nextInterface.isInterface()) {
                throw new IllegalArgumentException(
                        "incorrect interface: the interface is empty or it is not interface");
            }

            for (Method method : nextInterface.getMethods()) {
                if (method.getAnnotation(DoNotProxy.class) == null) {
                    Class<?> type = method.getReturnType();
                    if (method.getAnnotation(Collect.class) != null) {
                        if (type.equals(void.class) || type.equals(long.class)
                                || type.equals(int.class)
                                || type.equals(Integer.class)
                                || type.equals(Long.class)
                                || type.equals(List.class)) {
                            continue;
                        }

                        throw new IllegalStateException(
                                "invalid method return type");
                    }

                    HashSet<Class<?>> types = new HashSet<>(
                            Arrays.asList(method.getParameterTypes()));
                    if (!types.contains(long.class)
                            && !types.contains(int.class)
                            && !types.contains(Long.class)
                            && !types.contains(Integer.class)) {
                        throw new IllegalArgumentException(
                                "one of the methods have not long or int type");
                    }
                }
            }
        }

        for (Object target : targets) {
            if (target == null) {
                throw new IllegalArgumentException("null target");
            }

            boolean interfaceWasFounded = false;
            HashSet<Class> interfacesSet = new HashSet<>(
                    Arrays.asList(interfaces));
            for (Class<?> nextInterface : target.getClass().getInterfaces()) {
                if (interfacesSet.contains(nextInterface)) {
                    interfaceWasFounded = true;
                    break;
                }
            }

            if (!interfaceWasFounded) {
                throw new IllegalArgumentException(
                        "can't find an interface for one of the targets");
            }
        }

        return Proxy.newProxyInstance(interfaces[0].getClassLoader(),
                interfaces, new InvocationHandler(targets));
    }
}