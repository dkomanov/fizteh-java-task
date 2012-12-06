package ru.fizteh.fivt.students.dmitriyBelyakov.proxy.proxy;

import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;
import ru.fizteh.fivt.students.dmitriyBelyakov.proxy.ProxyUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ShardingProxyFactory implements ru.fizteh.fivt.proxy.ShardingProxyFactory {
    @Override
    public Object createProxy(Object[] targets, Class[] interfaces) {
        ProxyUtils.throwExceptionIsArgumentsIsIncorrect(targets, interfaces);
        return Proxy.newProxyInstance(interfaces[0].getClassLoader(), interfaces, new InvocationHandler(targets));
    }
}