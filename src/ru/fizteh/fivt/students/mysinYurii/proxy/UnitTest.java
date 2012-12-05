package ru.fizteh.fivt.students.mysinYurii.proxy;

import java.lang.reflect.Proxy;
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
            System.out.print("Expected 18. Answer is : ");
            System.out.println(pr.mul(2, 3));
        } catch (IllegalArgumentException e) {
            System.out.println("Fail");
            System.out.println(e.getMessage());
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
            new ShardingProxyFactory().createProxy(null, interfaces);
            System.out.println("Fail");
        } catch (IllegalArgumentException e) {
            System.out.println("Success");
        }
    }
    
    public static interface ExampleInterface {
        @DoNotProxy
        public int sum(int i, int j);
        @Collect
        public long mul(long i, long j);
        public List<Integer> assign(int i, int j);
    }
    
    public static class ExampleClass implements ExampleInterface {
        @DoNotProxy
        public int sum(int i, int j) {
            return i * j;
        }
        
        @Collect
        public long mul(long i, long j) {
            return i * j;
        }
        
        public List<Integer> assign(int i, int j) {
            List<Integer> returnVal = new ArrayList<Integer>();
            for (int i1 = 0; i1 < i; ++i1) {
                returnVal.add(j);
            }
            return returnVal;
        }
    }
}
