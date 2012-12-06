package ru.fizteh.fivt.students.tolyapro.proxy;

import java.lang.reflect.Proxy;

public class LoggingProxyFactory implements
        ru.fizteh.fivt.proxy.LoggingProxyFactory {

    public static boolean implementsInterface(Object object, Class interf) {
        return interf.isInstance(object);
    }

    @Override
    public Object createProxy(Object target, Appendable writer,
            Class... interfaces) {
        if (target == null) {
            throw new IllegalArgumentException("target is null");
        }
        if (writer == null) {
            throw new IllegalArgumentException("writer is null");
        }
        if (interfaces == null || interfaces.length == 0) {
            throw new IllegalArgumentException("interfaces are empty");
        }
        for (int i = 0; i < interfaces.length; ++i) {
            if (interfaces[i] == null) {
                throw new IllegalArgumentException(i + " interface is null");
            }
            if (!implementsInterface(target, interfaces[i])) {
                throw new IllegalArgumentException(
                        "target doesn't implement interface "
                                + interfaces[i].toString());
            }
            if (interfaces[i].getMethods().length == 0) {
                throw new IllegalArgumentException("interface " + interfaces[i]
                        + "doesn't have any methods");
            }
        }
        return Proxy.newProxyInstance(interfaces[0].getClassLoader(),
                interfaces,
                new ru.fizteh.fivt.students.tolyapro.proxy.InvocationHandler(
                        target, writer));
    }

}
