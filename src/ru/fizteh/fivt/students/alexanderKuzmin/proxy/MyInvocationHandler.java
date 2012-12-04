package ru.fizteh.fivt.students.alexanderKuzmin.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;

/**
 * @author Alexander Kuzmin group 196 Class MyInvocationHandler
 * 
 */

public class MyInvocationHandler implements java.lang.reflect.InvocationHandler {

    private Object[] targets;

    public MyInvocationHandler(Object[] targets) {
        this.targets = targets;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {

        if (method == null || proxy == null || args == null || args.length == 0) {
            throw new NullPointerException("Incorrect input.");
        }
        if (method.getAnnotation(DoNotProxy.class) != null) {
            throw new IllegalArgumentException("This method has DoNotProxy annotation.");
        }
        
        if (method.getAnnotation(Collect.class) == null) {
            for (Object arg : args) {
                if (arg != null) {
                    Class<?> clazz = arg.getClass();
                    if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
                        return method.invoke(
                                targets[(int) arg % targets.length], args);
                    }
                    if (clazz.equals(long.class) || clazz.equals(Long.class)) {
                        return method.invoke(
                                targets[(int) ((Long) arg % targets.length)],
                                args);
                    }
                } else {
                    throw new NullPointerException("A null argument in args.");
                }
            }
        } else {
            Class<?> returnType = method.getReturnType();
            if (returnType.equals(void.class)) {
                for (Object target : targets) {
                    method.invoke(target, args);
                }
                return null;
            }
            if (returnType.equals(int.class)
                    || returnType.equals(Integer.class)) {
                Integer answ = 0;
                for (Object target : targets) {
                    answ += (Integer) method.invoke(target, args);
                }
                return answ;
            }
            if (returnType.equals(long.class)
                    || returnType.equals(Long.class)) {
                Long answ = 0L;
                for (Object target : targets) {
                    answ += (Long) method.invoke(target, args);
                }
                return answ;
            }
            if (returnType.equals(List.class)) {
                List<?> answ = new ArrayList<>();
                for (Object target : targets) {
                    answ.addAll((List) method.invoke(target, args));
                }
                return answ;
            }
            throw new IllegalArgumentException("Incorrect return type of method.");
        }
        throw new IllegalArgumentException("Incorrect args.");
    }

}
