package ru.fizteh.fivt.students.myhinMihail.proxy;

import java.io.*;
import org.junit.*;

import ru.fizteh.fivt.proxy.*;

public class UnitTests extends Assert {
    
    @Test(expected = IllegalArgumentException.class)
    public void nullTarget() {
        new ShardingProxyFactory().createProxy(null, new Class[10]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullInterface() {
        new ShardingProxyFactory().createProxy(new Object[10], null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyTarget() {
        new ShardingProxyFactory().createProxy(new Object[0], new Class[10]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyInterface() {
        new ShardingProxyFactory().createProxy(new Object[10], new Class[0]);
    }

    class ClassWithoutInterface  {
        @Collect
        public void function() {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void noInterface() {
        Object[] targets = new Object[1];
        targets[0] = new ClassWithoutInterface();
        
        Class[] interfaces = new Class[1];
        interfaces[0] = Closeable.class;
        
        new ShardingProxyFactory().createProxy(targets, interfaces);
    }

    interface SimpleInterface {
        @DoNotProxy
        public int getInt(int n);

        public Long getLong(Long l);

        int sum(int num1, int num2);

        Long div(Long num1, Long num2);

        @Collect
        public int get1();
    }

    class TestClass implements SimpleInterface {
        public int getInt(int n) {
            return n;
        }

        public Long getLong(Long l) {
            return l;
        }

        public int sum(int n1, int n2) {
            return n1 + n2;
        }

        public Long div(Long l1, Long l2) {
            return l1 / l2;
        }

        public int get1() {
            return 1;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void badInterface() {
        Object[] targets = new Object[1];
        targets[0] = new TestClass();
        
        Class[] interfaces = new Class[2];
        interfaces[0] = TestClass.class;
        
        new ShardingProxyFactory().createProxy(targets, interfaces);
    }

    @Test(expected = RuntimeException.class)
    public void  doNotProxy() {
        Object[] targets = new Object[1];
        targets[0] = new TestClass();
        
        Class[] interfaces = new Class[1];
        interfaces[0] = TestClass.class;
        
        SimpleInterface inter = (SimpleInterface) new ShardingProxyFactory().createProxy(targets, interfaces);
        inter.getInt(3);
    }

    @Test
    public void goodTests() {
        Object[] targets = new Object[2];
        targets[0] = new TestClass();
        targets[1] = new TestClass();
        
        Class[] interfaces = new Class[1];
        interfaces[0] = SimpleInterface.class;
        
        SimpleInterface inter = (SimpleInterface) new ShardingProxyFactory().createProxy(targets, interfaces);
        
        assertTrue(inter.getLong(2L) == 2L);
        assertTrue(inter.sum(1, 2) == 3);
        assertTrue(inter.div(10L, 2L) == 5L);
        assertTrue(inter.get1() == 2);
    }
    
}