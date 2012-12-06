package ru.fizteh.fivt.students.mysinYurii.proxy;

import java.util.ArrayList;
import java.util.List;

import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;

public class UnitTest {
    static void test1() {
        Class[] interfaces = new Class[1];
        Object[] target = new Object[3];
        for (int i = 0; i < 3; ++i) {
            target[i] = new ExampleClass();
        }
        interfaces[0] = ExampleInterface.class;
        try {
            ExampleInterface pr = (ExampleInterface) new ShardingProxyFactory().createProxy(target, interfaces);
            System.out.print(pr.mul(2, 3));
            System.out.print(".Fail, exception is expected");
        } catch (IllegalArgumentException e) {
            System.out.println("Success");
        }
    }
    
    static void test2() {
        Class[] interfaces = new Class[1];
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
        Class[] interfaces = new Class[1];
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
        Class[] interfaces = new Class[0];
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
        Class[] interfaces = new Class[1];
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
        Class[] interfaces = new Class[1];
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
        Class[] interfaces = new Class[1];
        Object[] target = new Object[3];
        for (int i = 0; i < target.length; ++i) {
            target[i] = new ExampleClass();
        }
        interfaces[0] = ExampleInterface.class;
        try {
            ExampleInterface temp = (ExampleInterface) new ShardingProxyFactory().createProxy(target, interfaces);
            System.out.println(temp.assign(Long.valueOf(5), Long.valueOf(5)));
        } catch (IllegalArgumentException e) {
            System.out.println("Fail " + e.getMessage());
        }
    }
    
    public static interface ExampleInterface {
        @DoNotProxy
        public int sum(int i, int j);
        @Collect
        public long mul(long i, long j);
        @Collect
        public List<Long> assign(Long i, Long j);
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
        public List<Long> assign(Long i, Long j) {
            List<Long> returnVal = new ArrayList<Long>();
            for (int i1 = 0; i1 < i; ++i1) {
                returnVal.add(j);
            }
            return returnVal;
        }
    }
}
