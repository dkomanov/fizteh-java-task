/*
 * ShardingProxyInvokationHandler.java
 * Dec 7, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.proxy;

import java.lang.reflect.*;

public class ShardingProxyInvokationHandler
    implements InvocationHandler {

    private Object[] targets;
    
    public ShardingProxyInvokationHandler(Object[] targets) {
        this.targets = targets;
    }

    @Override
    public Object invoke(Object proxy,
        Method method, Object[] args) throws Throwable {

        // 
        return null;
    }
}