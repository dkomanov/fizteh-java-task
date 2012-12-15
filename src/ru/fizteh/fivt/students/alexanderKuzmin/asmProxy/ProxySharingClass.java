package ru.fizteh.fivt.students.alexanderKuzmin.asmProxy;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.Type;
import ru.fizteh.fivt.proxy.Collect;

import ru.fizteh.fivt.proxy.DoNotProxy;

/**
 * @author Alexander Kuzmin group 196 Class ProxySharingClass
 * 
 */

public class ProxySharingClass {

    public static boolean isDoNotProxy(Method method) {
        return method.getAnnotation(DoNotProxy.class) != null;
    }

    public static boolean isCollect(Method method) {
        return method.getAnnotation(Collect.class) != null;
    }

    public static void throwIncorrectArgument(Object[] targets,
            Class<?>[] interfaces) {
        if (targets == null || targets.length == 0 || interfaces == null
                || interfaces.length == 0) {
            throw new IllegalArgumentException("Incorrect input.");
        }

        for (Object target : targets) {
            if (target == null) {
                throw new IllegalArgumentException("A null target in targets.");
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
                throw new IllegalArgumentException(
                        "There are not interfaces for target.");
            }
        }

        for (Class<?> interf : interfaces) {
            if (interf == null) {
                throw new IllegalArgumentException(
                        "A null interface in interfaces.");
            }
            Method[] methods = interf.getMethods();
            if (methods.length == 0) {
                throw new IllegalArgumentException(
                        "Interface hasn't any method.");
            }
            if (!interf.isInterface()) {
                throw new IllegalArgumentException(
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
                        Class<?> returnType = method.getReturnType();
                        if (!(returnType.equals(void.class)
                                || returnType.equals(int.class)
                                || returnType.equals(Integer.class)
                                || returnType.equals(long.class)
                                || returnType.equals(Long.class) || returnType
                                    .equals(List.class))) {
                            throw new IllegalStateException(
                                    "Incorrect return type of method.");
                        }
                    }
                }
            }
        }
    }

    public static long getArgument(Object[] args) {
        for (Object arg : args) {
            Class<?> clazz = arg.getClass();
            if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
                return (long) ((Integer) arg);
            } else if (clazz.equals(long.class) || clazz.equals(Long.class)) {
                return (long) arg;
            }
        }
        throw new IllegalArgumentException("Incorrect arguments.");
    }

    public static void throwExceptionIfInterfacesContainsEqualsMethodSignature(
            Class<?>[] interfaces) {
        HashSet<String> signatures = new HashSet<>();
        for (Class<?> interfc : interfaces) {
            Method[] methods = interfc.getDeclaredMethods();
            for (Method method : methods) {
                StringBuilder signature = new StringBuilder();
                signature.append(method.getName()).append("(");
                Class<?>[] argTypes = method.getParameterTypes();
                for (Class<?> type : argTypes) {
                    signature.append(Type.getDescriptor(type));
                }
                signature.append(")");
                if (signatures.contains(signature.toString())) {
                    throw new IllegalArgumentException(
                            "Conflict methods in interfaces.");
                } else {
                    signatures.add(signature.toString());
                }
            }
        }
    }

}