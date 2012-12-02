package ru.fizteh.fivt.students.myhinMihail.proxy;

import ru.fizteh.fivt.proxy.*;
import java.lang.reflect.*;
import java.util.*;

public class ShardingProxyFactory implements ru.fizteh.fivt.proxy.ShardingProxyFactory {

    public Object createProxy(Object[] targets, Class[] interfaces) {
        if (interfaces == null || interfaces.length == 0) {
            throw new IllegalArgumentException("Empty interfaces");
        }
        
        if (targets == null || targets.length == 0) {
            throw new IllegalArgumentException("Empty targets");
        }
        
        for (Class<?> clazz : interfaces) {
            if (clazz == null || clazz.getMethods().length == 0 || !clazz.isInterface()) {
                throw new IllegalArgumentException("Bad interface");
            }

            for (Method method : clazz.getMethods()) {
                if (method.getAnnotation(DoNotProxy.class) == null) {
                    Class<?> type = method.getReturnType();
                    if (method.getAnnotation(Collect.class) != null) {
                        if (type.equals(void.class) || type.equals(int.class) || type.equals(Integer.class) 
                            || type.equals(long.class) || type.equals(Long.class) || type.equals(List.class)) {
                            continue;
                        }
                        
                        throw new IllegalStateException("Bad annotation");
                    }
                    
                    Set<Class<?>> types = new HashSet<>(Arrays.asList(method.getParameterTypes()));
                    if (!(types.contains(int.class) || types.contains(long.class)
                        || types.contains(Integer.class) || types.contains(Long.class))) {
                        throw new IllegalArgumentException("Bad arguments type");
                    }
                }
            }
        }
        
        for (Object target : targets) {
            if (target == null) {
                throw new NullPointerException();
            }
            
            boolean found = false;
            Set<Class> interfacesSet = new HashSet<>(Arrays.asList(interfaces));
            for (Class<?> clazz : target.getClass().getInterfaces()) {
                if (interfacesSet.contains(clazz)) {
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                throw new IllegalArgumentException("No interface for target");
            }
        }
        
        return Proxy.newProxyInstance(interfaces[0].getClassLoader(), interfaces, new InvocationHandler(targets));
    }
}