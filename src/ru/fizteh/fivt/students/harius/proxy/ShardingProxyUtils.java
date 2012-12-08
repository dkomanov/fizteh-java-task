/*
 * ShardingProxyUtils.java
 * Dec 7, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.proxy;

import ru.fizteh.fivt.proxy.*;
import java.util.*;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/*
 * Type-checker for ShardingProxy */
public abstract class ShardingProxyUtils {

    public static final List<Class> INTEGRAL
        = Arrays.asList((Class)int.class, (Class)Integer.class,
                        (Class)long.class, (Class)Long.class);

    public static boolean isIntegral(Class type) {
        return INTEGRAL.contains(type);
    }

    public static boolean isCollectable(Class type) {
        return INTEGRAL.contains(type)
            || List.class.isAssignableFrom(type)
            || type.equals(void.class);
    }
}