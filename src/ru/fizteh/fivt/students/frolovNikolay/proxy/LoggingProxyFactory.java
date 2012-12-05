package ru.fizteh.fivt.students.frolovNikolay.proxy;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class LoggingProxyFactory implements ru.fizteh.fivt.proxy.LoggingProxyFactory {

    @Override
    public Object createProxy(Object target, Appendable writer, Class... interfaces) {
        if (target == null) {
            throw new IllegalArgumentException("target: null pointer");
        }
        if (writer == null) {
            throw new IllegalArgumentException("writer: null pointer");
        }
        if (interfaces == null) {
            throw new IllegalArgumentException("interfaces: null pointer");
        }
        if (interfaces.length == 0) {
            throw new IllegalArgumentException("interfaces: empty");
        }
        
        ArrayList<Class<?>> interfacesArray = new ArrayList<Class<?>>(Arrays.asList(target.getClass().getInterfaces()));
        HashSet<Class<?>> sortedInterfaces = new HashSet<Class<?>>(interfacesArray);
        for (Class<?> iter : interfaces) {
            if (iter.getMethods().length == 0) {
                throw new IllegalArgumentException("interface " + iter.getSimpleName() + "doesn't have any methods");
            }
            if (!sortedInterfaces.contains(iter)) {
                throw new IllegalArgumentException("target doesn't implement: " + iter.getSimpleName());
            }
        }
        return Proxy.newProxyInstance(interfaces[0].getClassLoader(), interfaces, new InvocationHandler(target, writer));
    }
}