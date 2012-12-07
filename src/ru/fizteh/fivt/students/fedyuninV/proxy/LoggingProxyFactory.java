package ru.fizteh.fivt.students.fedyuninV.proxy;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class LoggingProxyFactory implements ru.fizteh.fivt.proxy.LoggingProxyFactory{

    public Object createProxy(Object target, Appendable writer, Class... interfaces) {
        if (target == null  ||  writer == null  ||  interfaces == null  ||  interfaces.length == 0
                || Arrays.asList(interfaces).contains(null)) {
            throw new IllegalArgumentException("Null parameter found");
        }
        List<Class> declaredInterfaces = new ArrayList<>();
        declaredInterfaces.addAll(Arrays.asList(target.getClass().getInterfaces()));
        for (int i = 0; i < declaredInterfaces.size(); i++) {
            Class clazz = declaredInterfaces.get(i);
            if (clazz != null) {
                declaredInterfaces.addAll(Arrays.asList(clazz.getInterfaces()));
            }
        }
        if (!declaredInterfaces.containsAll(Arrays.asList(interfaces))) {
            throw new IllegalArgumentException("target doesn't support interface");
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
