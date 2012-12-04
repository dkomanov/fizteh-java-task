package ru.fizteh.fivt.students.alexanderKuzmin.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;

/**
 * @author Alexander Kuzmin group 196 Class ShardingProxyFactor
 * 
 */

public class ShardingProxyFactory implements
        ru.fizteh.fivt.proxy.ShardingProxyFactory {

    @SuppressWarnings({ "rawtypes" })
    @Override
    public Object createProxy(Object[] targets, Class[] interfaces) {
        if (targets == null || targets.length == 0 || interfaces == null
                || interfaces.length == 0) {
            throw new NullPointerException("Incorrect input.");
        }

        for (Object target : targets) {
            if (target == null) {
                throw new NullPointerException("A null target in targets.");
            }
            Set<Class> curInterfaces = new HashSet<Class>(
                    Arrays.asList(interfaces));
            boolean include = false;
            Class<?>[] ourInterfaces = target.getClass().getInterfaces();
            for (Class<?> interfac : ourInterfaces) {
                if (curInterfaces.contains(interfac)) {
                    include = true;
                    break;
                }
            }
            if (!include) {
                throw new NoSuchElementException(
                        "There are not interfaces for target.");
            }
        }

        for (Class interf : interfaces) {
            if (interf == null) {
                throw new NullPointerException(
                        "A null interface in interfaces.");
            }
            Method[] methods = interf.getMethods();
            if (methods.length == 0) {
                throw new NoSuchMethodError("Interface hasn't any method.");
            }
            if (!interf.isInterface()) {
                throw new NoSuchElementException(
                        "There are not classes in interface.");
            }
            for (Method method : methods) {
                if (method.getAnnotation(DoNotProxy.class) == null) {
                    if (method.getAnnotation(Collect.class) == null) {
                        Set<Class> parameterTypes = new HashSet<Class>(
                                Arrays.asList(method.getParameterTypes()));
                        if (!(parameterTypes.contains(int.class)
                                || parameterTypes.contains(Integer.class)
                                || parameterTypes.contains(long.class) || parameterTypes
                                    .contains(Long.class))) {
                            throw new IllegalArgumentException(
                                    "Incorrect parameter types of method.");
                        }
                    } else {
                        Class returnType = method.getReturnType();
                        if (!(returnType.equals(void.class)
                                || returnType.equals(int.class)
                                || returnType.equals(Integer.class)
                                || returnType.equals(long.class)
                                || returnType.equals(Long.class) || returnType
                                    .equals(List.class))) {
                            throw new IllegalArgumentException(
                                    "Incorrect return type of method.");
                        }
                    }
                }
            }
        }
        return Proxy.newProxyInstance(interfaces[0].getClassLoader(),
                interfaces, new MyInvocationHandler(targets));
    }
}
