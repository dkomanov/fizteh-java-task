package ru.fizteh.fivt.students.dmitriyBelyakov.proxy;

import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ShardingProxyFactory implements ru.fizteh.fivt.proxy.ShardingProxyFactory {
    @Override
    public Object createProxy(Object[] targets, Class[] interfaces) {
        if (targets == null || targets.length == 0) {
            throw new IllegalArgumentException("No one target found.");
        }
        if (interfaces == null || interfaces.length == 0) {
            throw new IllegalArgumentException("No one interface found.");
        }
        for (Class face : interfaces) {
            if (!face.isInterface()) {
                throw new IllegalArgumentException(face.getName() + " is not interface.");
            }
            if (face == null || face.getMethods().length == 0) {
                throw new IllegalArgumentException("One of interfaces hasn't methods.");
            }
            Method[] methods = face.getMethods();
            for (Method method : methods) {
                if (method.getAnnotation(DoNotProxy.class) != null) {
                    continue;
                }
                Class returnType = method.getReturnType();
                if (method.getAnnotation(Collect.class) != null
                        && !(returnType.equals(void.class) || returnType.equals(int.class)
                        || returnType.equals(Integer.class) || returnType.equals(long.class)
                        || returnType.equals(Long.class) || returnType.equals(List.class))) {
                    throw new IllegalStateException("Incorrect annotation.");
                }
                if (method.getAnnotation(Collect.class) != null) {
                    continue;
                }
                HashSet<Class> parameterTypes = new HashSet<Class>(Arrays.asList(method.getParameterTypes()));
                if (!parameterTypes.contains(int.class) && !parameterTypes.contains(long.class)
                        && !parameterTypes.contains(Integer.class) && !parameterTypes.contains(Long.class)) {
                    throw new IllegalArgumentException("All methods must have int or long argument.");
                }
            }
        }
        for (Object target : targets) {
            if (target == null) {
                throw new IllegalArgumentException("NULL target.");
            }
            boolean flag = false;
            Class[] targetInterfaces = target.getClass().getInterfaces();
            HashSet<Class> interfces = new HashSet<>(Arrays.asList(interfaces));
            for (Class interfc : targetInterfaces) {
                if (interfces.contains(interfc)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                throw new IllegalArgumentException("One of targets doesn't implement interface from interfaces.");
            }
        }
        return Proxy.newProxyInstance(interfaces[0].getClassLoader(), interfaces, new InvocationHandler(targets));
    }
}