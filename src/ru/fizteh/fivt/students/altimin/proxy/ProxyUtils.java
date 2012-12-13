package ru.fizteh.fivt.students.altimin.proxy;

import org.objectweb.asm.Type;
import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;
import sun.reflect.generics.scope.MethodScope;

import java.lang.reflect.Method;
import java.util.*;

/**
 * User: altimin
 * Date: 12/12/12
 * Time: 9:28 PM
 */
public class ProxyUtils {
    public static boolean isCollectableClass(Class clazz) {
        return clazz.equals(int.class) || clazz.equals(Integer.class)
                || clazz.equals(long.class) || clazz.equals(Long.class)
                || clazz.equals(void.class) || clazz.equals(Void.class)
                || clazz.equals(List.class);
    }

    public static void checkInterface(Class iface) {
        if (iface == null) {
            throw new IllegalArgumentException("Null interface passed as argument");
        }
        Method[] methods = iface.getMethods();
        if (methods == null || methods.length == 0) {
            throw new IllegalArgumentException("Empty interface passed as argument");
        }
        for (Method method: methods) {
            if (method.isAnnotationPresent(DoNotProxy.class)) {
                continue;
            }
            if (method.isAnnotationPresent(Collect.class)) {
                if (!isCollectableClass(method.getReturnType())) {
                    throw new IllegalStateException(
                            String.format(
                                    "Incorrect annotation: return type of method %s of interface %s is not collectable",
                                    method.getName(),
                                    iface.getSimpleName()
                            )
                    );
                }
                continue;
            }
            List<Class<?>> parameterTypes = Arrays.asList(method.getParameterTypes());
            if (!(parameterTypes.contains(int.class) || parameterTypes.contains(Integer.class)
                    || parameterTypes.contains(long.class) || parameterTypes.contains(Long.class))) {
                throw new IllegalArgumentException(
                        String.format(
                                "Method %s of interface %s has no int/long parameter",
                                method.getName(),
                                iface.getSimpleName()
                        )
                );
            }
        }
    }

    public static String getMethodDescription(Method method) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(method.getName());
        stringBuilder.append(";");
        for (Class clazz: method.getParameterTypes()) {
            stringBuilder.append(Type.getDescriptor(clazz));
            stringBuilder.append(";");
        }
        return stringBuilder.toString();
    }

    public static void checkInterfaces(Class[] ifaces) {
        Map <String, Class> returnTypes = new HashMap<String, Class>();
        Map <String, Boolean> hasDoNotProxyAnnotation = new HashMap<String, Boolean>();
        Map <String, Boolean> hasCollectAnnotation = new HashMap<String, Boolean>();
        for (Class iface : ifaces) {
            Method[] methods = iface.getDeclaredMethods();
            for (Method method : methods)  {
                method.setAccessible(true);
                String description = getMethodDescription(method);
                Class returnType = returnTypes.get(description);
                if (returnType == null) {
                    returnTypes.put(description, method.getReturnType());
                    hasCollectAnnotation.put(description, method.isAnnotationPresent(Collect.class));
                    hasDoNotProxyAnnotation.put(description, method.isAnnotationPresent(DoNotProxy.class));
                } else {
                    if (!returnType.equals(method.getReturnType())) {
                        throw new IllegalArgumentException(
                                "Incorrect interfaces: two methods with same name and parameters have different return type"
                        );
                    }
                    if (!hasCollectAnnotation.get(description).equals(method.isAnnotationPresent(Collect.class))) {
                        throw new IllegalArgumentException(
                                "Incorrect interfaces: two methods with same name and parameters and return type have different annotations"
                        );
                    }
                    if (!hasDoNotProxyAnnotation.get(description).equals(method.isAnnotationPresent(DoNotProxy.class))) {
                        throw new IllegalArgumentException(
                                "Incorrect interfaces: two methods with same name and parameters and return type have different annotations"
                        );
                    }
                }
            }
        }
    }



    public static void checkObject(Object object, Class[] ifaces) {
        Set<Class> implementedInterfaces = new HashSet<Class>(Arrays.asList(object.getClass().getInterfaces()));
        for (Class iface: ifaces) {
            if (!implementedInterfaces.contains(iface)) {
                throw new IllegalArgumentException("All classes should implement all interfaces");
            }
        }
    }

    public static void check(Object[] targets, Class[] interfaces) {
        if (interfaces == null || interfaces.length == 0) {
            throw new IllegalArgumentException("Interfaces is empty");
        }
        if (targets == null || targets.length == 0) {
            throw new IllegalArgumentException("Targets is empty");
        }
        for (Class iface: interfaces) {
            checkInterface(iface);
        }
        checkInterfaces(interfaces);
        for (Object target: targets) {
            checkObject(target, interfaces);
        }
    }
}
