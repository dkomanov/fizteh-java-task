/*
 * ShardingProxyInvokationHandler.java
 * Dec 7, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.proxy;

import ru.fizteh.fivt.proxy.*;
import java.lang.reflect.*;
import java.util.*;

/*
 * InvocationHandler sharding implementation
 */
public class ShardingProxyInvocationHandler
    implements InvocationHandler {

    private Object[] targets;
    
    /* Saves targets for sharding */
    protected ShardingProxyInvocationHandler(Object[] targets) {
        this.targets = targets;
    }

    @Override
    public Object invoke(Object proxy,
        Method method, Object[] args) throws Throwable {

        method.setAccessible(true);
        if (method.getAnnotation(DoNotProxy.class) != null) {
            throw new IllegalStateException("'Do-not-proxy' method called");
        }
        try {
            if (method.getAnnotation(Collect.class) != null) {
                return invokeCollect(proxy, method, args);
            } else {
                return invokeSingle(proxy, method, args);
            }
        } catch (InvocationTargetException wrapped) {
            throw wrapped.getCause();
        }
    }

    /* Invoke all and collect */
    private Object invokeCollect(Object proxy,
        Method method, Object[] args) throws Throwable {

        Class returns = method.getReturnType();
        if (returns.equals(void.class)) {
            for (Object target : targets) {
                method.invoke(target, args);
            }
            return null;
        } else if (ShardingProxyUtils.isIntegral(returns)) {
            long result = 0;
            for (Object target : targets) {
                result += ((Number)method.invoke(target, args)).longValue();
            }
            if (Integer.class.isAssignableFrom(returns)
                || int.class.isAssignableFrom(returns)) {
                return (int)result;
            } else {
                return result;
            }
        } else if (ShardingProxyUtils.isCollectable(returns)) {
            List result = new ArrayList();
            for (Object target : targets) {
                result.addAll((List)method.invoke(target, args));
            }
            return result;
        } else {
            throw new IllegalStateException(
                "Return type not collectable");
        }
    }

    /* Invoke requested target */
    private Object invokeSingle(Object proxy,
        Method method, Object[] args) throws Throwable {

        Number which = null;
        for (Object param : args) {
            if (ShardingProxyUtils.isIntegral(param.getClass())) {
                which = ((Number)param);
                break;
            }
        }

        if (which == null) {
            throw new IllegalArgumentException(
                "At least one integral argument needed");
        }
        return method.invoke(
            targets[(int)(which.longValue() % targets.length)], args);
    }
}