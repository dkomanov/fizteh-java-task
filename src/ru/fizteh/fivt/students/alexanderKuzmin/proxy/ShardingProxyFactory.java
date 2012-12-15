package ru.fizteh.fivt.students.alexanderKuzmin.proxy;

import java.lang.reflect.Proxy;
import asmProxy.ProxySharingClass;

/**
 * @author Alexander Kuzmin group 196 Class ShardingProxyFactor
 * 
 */

public class ShardingProxyFactory implements
        ru.fizteh.fivt.proxy.ShardingProxyFactory {

    @Override
    public Object createProxy(Object[] targets, Class[] interfaces) {
        ProxySharingClass.throwIncorrectArgument(targets, interfaces);
        return Proxy.newProxyInstance(interfaces[0].getClassLoader(), interfaces, new MyInvocationHandler(targets));
    }
}