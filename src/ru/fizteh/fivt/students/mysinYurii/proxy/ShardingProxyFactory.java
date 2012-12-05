package ru.fizteh.fivt.students.mysinYurii.proxy;

import java.awt.List;
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
        Set<Class> setOfInterfaces = new HashSet<Class>(Arrays.asList(interfaces));
        for (Object targ : targets) {
            if (targ == null) {
                throw new IllegalArgumentException("Null target");
            }
            boolean implementsSomething = false;
            for (Class currInterface : interfaces) {
                if (setOfInterfaces.contains(currInterface)) {
                    implementsSomething = true;
                    break;
                }
            }
            if (!implementsSomething) {
                throw new IllegalArgumentException(targ.getClass().getName() + "doesn't implement any of given interfaces");
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
                        throw new IllegalArgumentException("Return type of " + currMethod.getName() + "doesn't supported");
                    } else {
                        Set<Class> argSet = new HashSet<Class>(Arrays.asList(currMethod.getParameterTypes()));
                        if (!argSet.contains(int.class) 
                                && !argSet.contains(long.class)) {
                            throw new IllegalArgumentException("No int or long parameter");
                        }
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
