package ru.fizteh.fivt.students.khusaenovTimur.proxy;

import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Timur
 * Date: 12/8/12
 * Time: 1:44 AM
 * To change this template use File | Settings | File Templates.
 */

public class LoggingProxyFactory implements ru.fizteh.fivt.proxy.LoggingProxyFactory{

    public Object createProxy(Object target, Appendable writer, Class... interfaces) {
        if (target == null  ||  writer == null  ||  interfaces == null  ||  interfaces.length == 0
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
                new InvocationHandler(target, writer));
    }
}
