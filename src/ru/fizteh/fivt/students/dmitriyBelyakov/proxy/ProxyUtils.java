package ru.fizteh.fivt.students.dmitriyBelyakov.proxy;

import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ProxyUtils {
    public static void throwExceptionIsArgumentsIsIncorrect(Object[] targets, Class[] interfaces) {
        if (targets == null || targets.length == 0) {
            throw new IllegalArgumentException("No one target found.");
        }
        if (interfaces == null || interfaces.length == 0) {
            throw new IllegalArgumentException("No one interface found.");
        }
        for (Class face : interfaces) {
            if (face == null || face.getMethods().length == 0) {
                throw new IllegalArgumentException("One of interfaces hasn't methods.");
            }
            if (!face.isInterface()) {
                throw new IllegalArgumentException(face.getName() + " is not interface.");
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
    }

    public static boolean isDoNotProxy(Method method) {
        return method.getAnnotation(DoNotProxy.class) != null;
    }

    public static boolean isCollect(Method method) {
        return method.getAnnotation(Collect.class) != null;
    }

    public static long getFirstIntOrLongArgument(Object[] args) {
        for (Object arg : args) {
            Class clazz = arg.getClass();
            if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
                return (long) ((Integer)arg);
            } else if (clazz.equals(long.class) || clazz.equals(Long.class)) {
                return (Long) arg;
            }
        }
        throw new IllegalArgumentException("Incorrect arguments.");
    }

    public static int merge(int i1, int i2) {
        return i1 + i2;
    }

    public static Integer merge(Integer i1, Integer i2) {
        return i1 + i2;
    }

    public static long merge(long l1, long l2) {
        return l1 + l2;
    }

    public static Long merge(Long l1, Long l2) {
        return l1 + l2;
    }

    public static List merge(List l1, List l2) {
        l1.addAll(l2);
        return l1;
    }
}