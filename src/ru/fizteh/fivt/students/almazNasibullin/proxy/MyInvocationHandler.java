package ru.fizteh.fivt.students.almazNasibullin.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;

/**
 * 30.11.12
 * @author almaz
 */

public class MyInvocationHandler implements  InvocationHandler {
    private Object[] targets;

    public MyInvocationHandler(Object[] targets) {
        this.targets = targets;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method == null) {
            throw new IllegalArgumentException("Nullpointer method");
        }

        if (method.getAnnotation(DoNotProxy.class) != null) {
            throw new IllegalArgumentException("Bad method: flag DoNotProxy is set");
        }
        if (method.getAnnotation(Collect.class) == null) {
            for (Object o : args) {
                if (o != null) {
                    if (o.getClass().equals(int.class) || o.getClass().equals(Integer.class)) {
                        return method.invoke(targets[(Integer)o % targets.length], args);
                    }
                    if (o.getClass().equals(long.class) || o.getClass().equals(Long.class)) {
                        Long l = (Long)o;
                        return method.invoke(targets[(int)(l % targets.length)], args);
                    }
                }
            }
        } else {
            Class returnClass = method.getReturnType();
            if (returnClass.equals(void.class)) {
                for (Object target : targets) {
                    method.invoke(target, args);
                }
                return null;
            }
            if (returnClass.equals(int.class) || returnClass.equals(Integer.class)) {
                Integer obj = 0;
                for (Object target : targets) {
                    obj += (Integer)method.invoke(target, args);
                }
                return obj;
            }
            if (returnClass.equals(long.class) || returnClass.equals(Long.class)) {
                Long obj = 0L;
                for (Object target : targets) {
                    obj += (Long)method.invoke(target, args);
                }
                return obj;
            }
            if (returnClass.equals(List.class)) {
                List obj = new ArrayList();
                for (Object target : targets) {
                    obj.addAll((List)method.invoke(target, args));
                }
                return obj;
            }
            throw new IllegalArgumentException("Unsuitable method is found");
        }
        throw new IllegalArgumentException("No suitable arguments");
    }
}
