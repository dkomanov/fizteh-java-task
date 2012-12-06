package ru.fizteh.fivt.students.fedyuninV.proxy;

import java.lang.reflect.Proxy;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class LoggingProxyFactory implements ru.fizteh.fivt.proxy.LoggingProxyFactory{

    @Override
    public Object createProxy(Object target, Appendable writer, Class... interfaces) {
        if (target == null  ||  writer == null  ||  interfaces == null  ||  interfaces.length == 0) {
            throw new IllegalArgumentException("Null parameter found");
        }
        int methodsNum = 0;
        for (int i = 0; i < interfaces.length; i++) {
            if (interfaces[i] == null) {
                throw new IllegalArgumentException("Null parameter found");
            }
            methodsNum += interfaces[i].getMethods().length;
        }
        if (methodsNum == 0) {
            throw new IllegalArgumentException("No methods in interfaces");
        }
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),
                interfaces,
                new InvocationHandler(target, writer));
    }
}
