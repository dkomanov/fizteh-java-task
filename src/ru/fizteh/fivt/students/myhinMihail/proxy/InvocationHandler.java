package ru.fizteh.fivt.students.myhinMihail.proxy;

import ru.fizteh.fivt.proxy.*;
import java.lang.reflect.Method;
import java.util.*;

public class InvocationHandler implements java.lang.reflect.InvocationHandler {
    private Object[] targets;

    public InvocationHandler(Object[] targets) {
        this.targets = targets;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        if (proxy == null || method == null) {
            throw new NullPointerException();
        }
        
        method.setAccessible(true);
        
        if (method.getAnnotation(DoNotProxy.class) != null) {
            throw new IllegalArgumentException("Bad method");
        }
       
        if (method.getAnnotation(Collect.class) != null) {
            Class<?> type = method.getReturnType();
            if (type.equals(void.class)) {
                for (Object target : targets) {
                    method.invoke(target, args);
                }
                return null;
            }
            
            if (type.equals(int.class) || type.equals(Integer.class)) {
                int result = 0;
                for (Object target : targets) {
                    result += (int) method.invoke(target, args);
                }
                return result;
            }
            
            if (type.equals(long.class) || type.equals(Long.class)) {
                long result = 0;
                for (Object target : targets) {
                    result += (long) method.invoke(target, args);
                }
                return result;
            }
            
            if (type.isAssignableFrom(List.class)) {
                List<?> result = new ArrayList<>();
                for (Object target : targets) {
                    result.addAll((List) method.invoke(target, args));
                }
                return result;
            }
            
            throw new IllegalArgumentException("Bad method");
        }
        
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Bad arguments");
        }
        
        for (Object arg : args) {
            if (arg == null) {
                break;
            }
            
            Class<?> clazz = arg.getClass();
            if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
                return method.invoke(targets[(int) arg % targets.length], args);
            }
            
            if (clazz.equals(long.class) || clazz.equals(Long.class)) {
                return method.invoke(targets[(int) ((Long) arg % targets.length)], args);
            }
            
            break;
        }
        
        throw new IllegalArgumentException("Bad arguments");
    }
}