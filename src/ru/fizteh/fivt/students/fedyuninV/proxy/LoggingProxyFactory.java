package ru.fizteh.fivt.students.fedyuninV.proxy;

import java.lang.reflect.Proxy;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class LoggingProxyFactory implements ru.fizteh.fivt.proxy.LoggingProxyFactory{

    @Override
    public Object createProxy(Object target, Appendable writer, Class... interfaces) {
        if (target == null  ||  writer == null  ||  interfaces == null) {
            throw new RuntimeException("Null parameter found");
        }
        for (int i = 0; i < interfaces.length; i++) {
            if (interfaces[i] == null) {
                throw new RuntimeException("Null parameter found");
            }
        }
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),
                interfaces,
                new InvocationHandler(target, writer));
    }
}
