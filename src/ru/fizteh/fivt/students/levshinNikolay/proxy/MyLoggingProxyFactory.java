package ru.fizteh.fivt.students.levshinNikolay.proxy;

import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * Levshin Nikolay
 * MIPT FIVT 196
 */
public class MyLoggingProxyFactory {

    public Object createProxy(Object target, Appendable writer, Class... interfaces) {
        if (target == null || writer == null || interfaces == null || interfaces.length == 0
                || Arrays.asList(interfaces).contains(null)) {
            throw new IllegalArgumentException("Null parameter found");
        }
        int methodsNum = 0;
        for (int i = 0; i < interfaces.length; i++) {
            if (!interfaces[i].isAssignableFrom(target.getClass())) {
                throw new IllegalArgumentException("target doesn't support interface");
            }
            methodsNum += interfaces[i].getMethods().length;
        }
        if (methodsNum == 0) {
            throw new IllegalArgumentException("No methods in interfaces");
        }
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),
                interfaces,
                new MyInvocationHandler(target, writer));
    }

}
