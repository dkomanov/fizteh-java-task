package ru.fizteh.fivt.students.fedyuninV.proxy;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
        Set<Class<?>> declaredInterfaces = new HashSet<>(Arrays.asList(target.getClass().getInterfaces()));
        int methodsNum = 0;
        for (int i = 0; i < interfaces.length; i++) {
            if (interfaces[i] == null) {
                throw new IllegalArgumentException("Null parameter found");
            }
            methodsNum += interfaces[i].getMethods().length;
        }
        if (!declaredInterfaces.containsAll(Arrays.asList(interfaces))) {
            throw new IllegalAccessError("target doesn't support interface");
        }
        if (methodsNum == 0) {
            throw new IllegalArgumentException("No methods in interfaces");
        }
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),
                interfaces,
                new InvocationHandler(target, writer));
    }
}
