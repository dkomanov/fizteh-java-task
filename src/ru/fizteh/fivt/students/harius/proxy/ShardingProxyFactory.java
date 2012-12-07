/*
 * ShardingProxyFactory.java
 * Dec 7, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.proxy;

import ru.fizteh.fivt.proxy.*;
import java.util.*;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/*
 * Factory for ShardingProxy
 */
public class ShardingProxyFactory
    implements ru.fizteh.fivt.proxy.ShardingProxyFactory {

    /* Create proxy with provided properties */
    @Override
    public Object createProxy(Object[] targets, Class[] interfaces) {
        checkCorrectness(targets, interfaces);
        return Proxy.newProxyInstance(
            interfaces[0].getClassLoader(),
            interfaces,
            new ShardingProxyInvocationHandler(targets));
    }

    /* Check correctness of the input */
    private void checkCorrectness(Object[] targets, Class[] interfaces) {
        if (targets == null || interfaces == null) {
            throw new IllegalArgumentException("Null argument");
        }
        if (targets.length == 0 || interfaces.length == 0) {
            throw new IllegalArgumentException("Empty argument");
        }
        for (Object target : targets) {
            if (target == null) {
                throw new IllegalArgumentException("Null target");
            }
            checkInheritance(target, interfaces);
        }
        for (Class iface : interfaces) {
            if (iface == null) {
                throw new IllegalArgumentException("Null interface");
            }
            if (iface.getMethods().length == 0) {
                throw new IllegalArgumentException("Empty interface");
            }
            checkAnnotations(iface);
        }
    }

    /* Check the target inherits properly from the interfaces */
    private void checkInheritance(Object target, Class[] interfaces) {
        List<Class<?>> inherited = Arrays.asList(
            target.getClass().getInterfaces());
        List<Class> needed = Arrays.asList(interfaces);
        if (!inherited.containsAll(needed)) {
            throw new IllegalArgumentException(
                "Target doesn't implement interfaces");
        }
    }

    /* Check there are no annotation conflicts */
    private void checkAnnotations(Class iface) {
        for (Method method : iface.getMethods()) {
            if (method.getAnnotation(DoNotProxy.class) != null) {
                continue;
            }
            if (method.getAnnotation(Collect.class) != null) {
                if (!ShardingProxyUtils.isCollectable(
                    method.getReturnType())) {

                    throw new IllegalStateException(
                        "Return type not collectable");
                }
            } else {
                List<Class<?>> params
                    = Arrays.asList(method.getParameterTypes());
                if (Collections.disjoint(
                    params, ShardingProxyUtils.INTEGRAL)) {

                    throw new IllegalArgumentException(
                        "At least one integral argument needed");
                }
            }
        }
    }
}