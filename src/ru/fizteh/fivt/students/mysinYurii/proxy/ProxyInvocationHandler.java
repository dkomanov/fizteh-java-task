package ru.fizteh.fivt.students.mysinYurii.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;

public class ProxyInvocationHandler implements InvocationHandler {
    private Object[] targets;
    
    public ProxyInvocationHandler(Object[] newTargets) {
        targets = newTargets;
    }
    
    boolean isNumber(Class<?> someClass) {
        return (someClass.equals(int.class)
                || someClass.equals(long.class));
    }

    @Override
    public Object invoke(Object object, Method method, Object[] arguments)
            throws Throwable {
        if (object == null) {
            throw new IllegalArgumentException("Object is null");
        } else if (method == null) {
            throw new IllegalArgumentException("Method is null");
        }
        method.setAccessible(true);
        if (method.getAnnotation(DoNotProxy.class) != null) {
            throw new IllegalArgumentException(method.getName() + ": has annotation DoNotProxy");
        } else if (method.getAnnotation(Collect.class) == null) {
            for (int i = 0; i < arguments.length; ++i) {
                if (arguments[i] != null) {
                    if (arguments[i].getClass().equals(int.class) || arguments[i].getClass().equals(Integer.class)) {
                        try {
                            return method.invoke(targets[(int) i % arguments.length], arguments);
                        } catch (InvocationTargetException e) {
                            throw e.getCause();                            
                        }
                    } else if (arguments[i].getClass().equals(long.class) || arguments[i].getClass().equals(Long.class)) {
                        try {
                            return method.invoke(targets[(int) ((long) i % arguments.length)], arguments);
                        } catch (InvocationTargetException e) {
                            throw e.getCause();
                        }
                    }
                }
            }
            throw new IllegalArgumentException("Int or long variable not found");
        } else {
            Class<?> returnType = method.getReturnType();
            if (returnType.equals(void.class)) {
                for (Object targ : targets) {
                    method.invoke(targ, arguments);
                }
                return null;
            } else if (returnType.equals(int.class)) {
                int resValue = 0;
                for (Object targ : targets) {
                    resValue += (int) method.invoke(targ, arguments);
                }
                return resValue;
            } else if (returnType.equals(long.class)) {
                long resValue = 0;
                for (Object targ : targets) {
                    resValue += (long) method.invoke(targ, arguments);
                }
                return resValue;
            } else if (returnType.equals(List.class)) {
                List<?> resList = new ArrayList<>();
                for (Object targ : targets) {
                    resList.addAll((List) method.invoke(targ, arguments));
                }
                return resList;
            } else {
                throw new IllegalArgumentException("Return value is not a void, int, long or List: " + method.getName());
            }
        }
    }
}
