package ru.fizteh.fivt.students.nikitaAntonov.proxy;

import java.lang.reflect.Proxy;

public class LoggingProxyFactory implements ru.fizteh.fivt.proxy.LoggingProxyFactory {
    
    @Override
    public Object createProxy(Object target, Appendable writer,
            Class... interfaces) {
 
        checkForNull(target);
        checkForNull(writer);
        checkForNull(interfaces);
        
        boolean haveMethods = false;
        for (Class i : interfaces) {
            checkForNull(i);
            checkCanImplement(target, i);
            if (i.getMethods().length > 0) {
                haveMethods = true;
            }
        }
        
        if (!haveMethods) {
            throw new IllegalArgumentException("There is no methods for proxing");
        }
        
        return Proxy.newProxyInstance(interfaces[0].getClassLoader(), interfaces, new InvocationHandler(target, writer));
    }

    private void checkCanImplement(Object o, Class<?> interfaceToImplement) {
        if (!interfaceToImplement.isAssignableFrom(o.getClass())) {
            throw new IllegalArgumentException("object don't implement interface " +  interfaceToImplement.getSimpleName());
        }
    }

    private static void checkForNull(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("All params musn't be null");
        }
    }

}
