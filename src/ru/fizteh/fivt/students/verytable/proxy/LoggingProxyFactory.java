package ru.fizteh.fivt.students.verytable.proxy;

import java.lang.reflect.Proxy;

public class LoggingProxyFactory implements ru.fizteh.fivt.proxy.LoggingProxyFactory {

    @Override
    public Object createProxy(Object target, Appendable writer, Class... interfaces) {

        if (target == null) {
            throw new IllegalArgumentException("Error: no target passed.");
        }
        if (writer == null) {
            throw new IllegalArgumentException("Error: no writer passed.");
        }
        if (interfaces == null || interfaces.length == 0) {
            throw new IllegalArgumentException("Error: no interfaces passed.");
        }

        for (int i = 0; i < interfaces.length; ++i) {
            if (interfaces[i] == null || interfaces[i].getMethods().length == 0) {
                throw new IllegalArgumentException("Error: " + interfaces[i]
                                                   + " doesn't have any methods.");
            }
            if (!interfaces[i].isInstance(target)) {
                throw new IllegalArgumentException("Error: target doesn't "
                                                   + "implement "
                                                   + interfaces[i].getSimpleName());
            }
        }

        return Proxy.newProxyInstance(target.getClass().getClassLoader(), interfaces,
                                      new InvocationHandler(target, writer));
    }
}
