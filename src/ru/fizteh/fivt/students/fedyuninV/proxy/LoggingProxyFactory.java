package ru.fizteh.fivt.students.fedyuninV.proxy;

import java.lang.reflect.Proxy;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class LoggingProxyFactory implements ru.fizteh.fivt.proxy.LoggingProxyFactory{

    @Override
    public Object createProxy(Object target, Appendable writer, Class... interfaces) {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),
                interfaces,
                new InvocationHandler(target, writer));
    }
}
