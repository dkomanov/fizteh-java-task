
package ru.fizteh.fivt.students.mesherinIlya.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;




class NeverSleepingEye implements InvocationHandler {
    private Object[] targets;
    
    public NeverSleepingEye(Object[] targets) {
        this.targets = targets;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        
        if (method.getAnnotation(DoNotProxy.class) != null) {
            throw new IllegalStateException("This method can't be invoked using the proxy.");
        }

        method.setAccessible(true);

     try {
        if (method.getAnnotation(Collect.class) != null) {
            Class returnType = method.getReturnType();
            if (returnType.equals(void.class)) {
                for (Object target : targets) {
                    method.invoke(target, args);
                }
                return null;
            } else if (returnType.equals(int.class) || returnType.equals(Integer.class)) {
                int result = 0;
                for (Object target : targets) {
                    result += (Integer) method.invoke(target, args);
                }
                return result;
            } else if (returnType.equals(long.class) || returnType.equals(Long.class)) {
                long result = 0;
                for (Object target : targets) {
                    result += (Long) method.invoke(target, args);
                }
                return result;
            } else if (returnType.equals(List.class)) {
                List result = new ArrayList();
                for (Object target : targets) {
                    result.addAll((List)method.invoke(target, args));
                }
                return result;
            } else {
                throw new IllegalStateException("The type isn't supported.");
                //return null;
            }
        } else {
            for (Object numberArgument : args) {
                if (numberArgument.getClass().equals(int.class) 
                        || numberArgument.getClass().equals(Integer.class)) {
                    return method.invoke(targets[(Integer)numberArgument % targets.length], args);
                }
                if (numberArgument.getClass().equals(long.class) 
                        || numberArgument.getClass().equals(Long.class)) {
                    return method.invoke(targets[(int)((Long)numberArgument % targets.length)], args);
                }
            }    
            
            throw new IllegalArgumentException(
                    "There must be at least one integer or long number among the parameters.");
        }
     } catch (InvocationTargetException e) {
         throw e.getTargetException();
     }
        
    }

}



public class ShardingProxyFactory implements ru.fizteh.fivt.proxy.ShardingProxyFactory {
    
    
    @Override
    public Object createProxy(Object[] targets, Class[] interfaces)  {
    
        if (targets == null) {
            throw new IllegalArgumentException("Targets is null.");
        } else if (interfaces == null) {
            throw new IllegalArgumentException("Interfaces is null.");
        } else if (targets.length == 0) {
            throw new IllegalArgumentException("There are no targets.");
        } else if (interfaces.length == 0) {
            throw new IllegalArgumentException("There are no interfaces.");
        }
        
        for (Object target : targets) {
            if (target == null) {
                throw new IllegalArgumentException("Some of targets are null.");
            }
        }
        
        for (Class intrface : interfaces) {
            if (intrface == null) {
                throw new IllegalArgumentException("Some of interfaces are null.");
            }
        }
          
        HashSet<Class> setOfInterfaces = new HashSet<Class>(Arrays.asList(interfaces));
        for (Object target : targets) {
            Class[] ourInterfaces = target.getClass().getInterfaces();
            boolean contains = true;
            for (Class intrface : ourInterfaces) {
                if (!setOfInterfaces.contains(intrface)) {
                    contains = false;
                    break;
                }
            }
            
            if (ourInterfaces.length == 0 || !contains) {
                throw new IllegalArgumentException("Some of targets don't implement all these interfaces.");
            }
            
        }
        
        for (Class intrface : interfaces) {
            Method[] methods = intrface.getMethods();
            
            if (methods.length == 0) {
                throw new IllegalArgumentException("Some of interfaces haven't got any methods.");
            }
            
            for (Method method : methods) {
                if (method.getAnnotation(DoNotProxy.class) == null) {
                    if (method.getAnnotation(Collect.class) == null) {
                        Set<Class> parameterTypes = new HashSet<Class>(Arrays.asList(method.getParameterTypes()));
                        if (!parameterTypes.contains(int.class)
                                && !parameterTypes.contains(Integer.class)
                                && !parameterTypes.contains(long.class) 
                                && !parameterTypes.contains(Long.class)) {
                            throw new IllegalArgumentException(
                                    "There must be at least one integer or long number among the parameters.");
                        }
                    } else {
                        Class returnType = method.getReturnType();
                        if (!returnType.equals(void.class)
                                && !returnType.equals(int.class)
                                && !returnType.equals(Integer.class)
                                && !returnType.equals(long.class)
                                && !returnType.equals(Long.class) 
                                && !returnType.equals(List.class)) {
                            throw new IllegalStateException("The return type of method isn't supported. ");
                        }
                    }
                }
            }   
        }
        
        return Proxy.newProxyInstance(interfaces[0].getClassLoader(),
                interfaces, new NeverSleepingEye(targets));   
    
    }

    
}

