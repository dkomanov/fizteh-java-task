package ru.fizteh.fivt.students.mysinYurii.proxy;

import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;
import ru.fizteh.fivt.students.mysinYurii.testClass.TestClass;

public class UnitTest {
    static void test1() {
        Class<?>[] interfaces = new Class[1];
        Object[] target = new Object[3];
        for (int i = 0; i < 3; ++i) {
            target[i] = new ExampleClass();
        }
        interfaces[0] = ExampleInterface.class;
        try {
            ExampleInterface pr = (ExampleInterface) new ShardingProxyFactory().createProxy(target, interfaces);
            System.out.println("Expected 18. Answer is : " + pr.mul(2, 3));
        } catch (IllegalArgumentException e) {
            System.out.println("Fail" + e.getMessage());
        }
    }
    
    static void test2() {
        Class<?>[] interfaces = new Class[1];
        Object[] target = new Object[1];
        target[0] = new ExampleClass();
        interfaces[0] = ExampleInterface.class;
        try {
            new ShardingProxyFactory().createProxy(target, null);
            System.out.println("Fail");
        } catch (IllegalArgumentException e) {
            System.out.println("Success");
        }
    }
    
    static void test3() {
        Class<?>[] interfaces = new Class[1];
        Object[] target = new Object[1];
        target[0] = new ExampleClass();
        interfaces[0] = ExampleInterface.class;
        try {
            new ShardingProxyFactory().createProxy(null, interfaces);
            System.out.println("Fail");
        } catch (IllegalArgumentException e) {
            System.out.println("Success");
        }
    }
    
    static void test4() {
        Class<?>[] interfaces = new Class[0];
        Object[] target = new Object[1];
        target[0] = new ExampleClass();
        try {
            new ShardingProxyFactory().createProxy(target, interfaces);
            System.out.println("Fail");
        } catch (IllegalArgumentException e) {
            System.out.println("Success");
        }
    }
    
    static void test5() {
        Class<?>[] interfaces = new Class[1];
        Object[] target = new Object[1];
        target[0] = new ExampleClass();
        interfaces[0] = ExampleInterface.class;
        try {
            ExampleInterface temp = (ExampleInterface) new ShardingProxyFactory().createProxy(target, interfaces);
            temp.throwException(0);
        } catch (IllegalArgumentException e) {
            System.out.println("Expected: Catch me, if you can. Got: " + e.getMessage());
        } catch (Throwable e) {
            System.out.println("Fail");
        }
    }
    
    static void test6() {
        Class<?>[] interfaces = new Class[1];
        Object[] target = new Object[1];
        target[0] = new ExampleClass();
        try {
            ExampleClass temp = (ExampleClass) new ShardingProxyFactory().createProxy(target, interfaces);
            System.out.println(temp.sum(0, 5));
        } catch (IllegalArgumentException e) {
            System.out.println("Success");
        } 
    }
    
    static void test7() {
        Class<?>[] interfaces = new Class[1];
        Object[] target = new Object[3];
        for (int i = 0; i < target.length; ++i) {
            target[i] = new ExampleClass();
        }
        interfaces[0] = ExampleInterface.class;
        try {
            ExampleInterface temp = (ExampleInterface) new ShardingProxyFactory().createProxy(target, interfaces);
            System.out.println(temp.assign(5, 5));
        } catch (IllegalArgumentException e) {
            System.out.println("Fail " + e.getMessage());
        }
    }
    
    static void test8() {
        Class<?>[] inter = new Class[1];
        Object[] targ = new Object[2];
        inter[0] = TestClass.getInter();
        targ[0] = TestClass.getNested();
        targ[1] = TestClass.getAnNested();
        try {
            Object temp1 = (Object) new ShardingProxyFactory().createProxy(targ, inter);
            temp1.getClass().getMethod("hello", int.class).invoke(temp1, 0);
            temp1.getClass().getMethod("hello", int.class).invoke(temp1, 1);
        } catch (IllegalArgumentException e) {
            System.out.println("Fail " + e.toString());
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
    private static interface ExampleInterface {
        @DoNotProxy
        public int sum(int i, int j);
        @Collect
        public long mul(long i, long j);
        @Collect
        public List<Integer> assign(int i, int j);
        public void throwException(int j);
    }
    
    public static class ExampleClass implements ExampleInterface {
        @DoNotProxy
        public int sum(int i, int j) {
            return i * j;
        }
        
        public void throwException(int j) throws IllegalArgumentException {
            throw new IllegalArgumentException("Catch me, if you can");
        }

        @Collect
        public long mul(long i, long j) {
            return i * j;
        }
        
        @Collect
        public List<Integer> assign(int i, int j) {
            List<Integer> returnVal = new ArrayList<Integer>();
            for (int i1 = 0; i1 < i; ++i1) {
                returnVal.add(j);
            }
            return returnVal;
        }
    }
}
