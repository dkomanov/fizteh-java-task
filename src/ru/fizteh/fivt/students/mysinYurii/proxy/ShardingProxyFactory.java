package ru.fizteh.fivt.students.mysinYurii.proxy;

import java.util.List;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;

public class ShardingProxyFactory implements ru.fizteh.fivt.proxy.ShardingProxyFactory {
    
    public Object createProxy(Object[] targets, Class[] interfaces) {
        if (targets == null) {
            throw new IllegalArgumentException("Targets array is null");
        } else if (interfaces == null) {
            throw new IllegalArgumentException("Interfaces array is null");
        } else if (targets.length == 0) {
            throw new IllegalArgumentException("Targets array is empty");
        } else if (interfaces.length == 0) {
            throw new IllegalArgumentException("Interfaces array is empty");
        }
        for (Object targ : targets) {
            if (targ == null) {
                throw new IllegalArgumentException("Null target");
            }
            boolean implementsSomething = false;
            Set<Class> setOfInterfaces = new HashSet<Class>();
            setOfInterfaces.addAll(Arrays.asList(targ.getClass().getInterfaces()));
            for (Class inter : interfaces) {
                if (inter == null) {
                    throw new IllegalArgumentException("Null interface");
                }
                if (!setOfInterfaces.contains(inter)) {
                    throw new IllegalArgumentException(inter.getName() + " has no implementation");
                }
            }
        }
        for (Class currInterface : interfaces) {
            if (currInterface == null) {
                throw new IllegalArgumentException("Null interface");
            }
            Method[] methods = currInterface.getMethods();
            if (methods.length == 0) {
                throw new IllegalArgumentException(currInterface.getName() + " has no methods");
            }
            for (Method currMethod : methods) {
                if (currMethod.getAnnotation(DoNotProxy.class) != null) {
                    continue;
                }
                if (currMethod.getAnnotation(Collect.class) != null) {
                    if (!isSupported(currMethod.getReturnType())) {
                        throw new IllegalStateException("Return type of " + currMethod.getName() + " doesn't supported");
                    }
                } else {
                    Set<Class> argSet = new HashSet<Class>(Arrays.asList(currMethod.getParameterTypes()));
                    if (!argSet.contains(int.class) 
                            && !argSet.contains(long.class)) {
                        throw new IllegalArgumentException("No int or long parameter in " + currMethod.getName());
                    }
                }
            }
        }
        return Proxy.newProxyInstance(interfaces[0].getClassLoader(), interfaces, new ProxyInvocationHandler(targets)); 
    }

    private boolean isSupported(Class<?> returnType) {
        return (returnType.equals(int.class)
                || returnType.equals(long.class)
                || returnType.equals(List.class)
                || returnType.equals(void.class));
    }

}
