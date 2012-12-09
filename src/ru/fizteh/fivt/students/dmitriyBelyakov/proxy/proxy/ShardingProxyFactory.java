package ru.fizteh.fivt.students.dmitriyBelyakov.proxy.proxy;

import ru.fizteh.fivt.students.dmitriyBelyakov.proxy.ProxyUtils;

import java.lang.reflect.Proxy;

public class ShardingProxyFactory implements ru.fizteh.fivt.proxy.ShardingProxyFactory {
    @Override
    public Object createProxy(Object[] targets, Class[] interfaces) {
        ProxyUtils.throwExceptionIfArgumentsIsIncorrect(targets, interfaces);
        return Proxy.newProxyInstance(interfaces[0].getClassLoader(), interfaces, new InvocationHandler(targets));
    }
}