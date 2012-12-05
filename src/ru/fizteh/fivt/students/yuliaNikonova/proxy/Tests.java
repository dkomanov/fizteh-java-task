package ru.fizteh.fivt.students.yuliaNikonova.proxy;

import static org.junit.Assert.*;

import java.io.Closeable;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;

public class Tests extends Assert {

    @Test(expected = IllegalArgumentException.class)
    public void nullTarget() {
        new ShardingProxyFactory().createProxy(null, new Class[2]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullInterface() {
        new ShardingProxyFactory().createProxy(new Object[2], null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyTarget() {
        new ShardingProxyFactory().createProxy(new Object[0], new Class[10]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyInterface() {
        new ShardingProxyFactory().createProxy(new Object[10], new Class[0]);
    }

    class ClassWithoutInterface {
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

    public interface MInterface {
        @DoNotProxy
        public int getInt(int n);

        public Long getLong(Long l);

        int sum(int num1, int num2);

        Long div(Long num1, Long num2);

        @Collect
        public int get1();

        @Collect
        public void throwRuntimeException();

        @Collect
        public void throwIllegalStateException();

        @Collect
        public void addList(List<Integer> list);
    }

    public class MClass implements MInterface {
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

        public void throwRuntimeException() {
            throw new RuntimeException("RuntimeException");

        }

        public void throwIllegalStateException() {
            throw new IllegalStateException("IllegalStateException");

        }

        public void addList(List<Integer> list) {
            list.add(1);

        }
    }

    @Test(expected = RuntimeException.class)
    public void doNotProxy() {
        Object[] targets = new Object[1];
        targets[0] = new MClass();

        Class[] interfaces = new Class[1];
        interfaces[0] = MInterface.class;

        MInterface inter = (MInterface) new ShardingProxyFactory().createProxy(targets, interfaces);
        inter.getInt(3);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void runtimeExceptionTest() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("RuntimeException");
        Object[] targets = new Object[1];
        targets[0] = new MClass();

        Class[] interfaces = new Class[1];
        interfaces[0] = MInterface.class;

        MInterface inter = (MInterface) new ShardingProxyFactory().createProxy(targets, interfaces);
        inter.throwRuntimeException();
    }

    @Test
    public void illegalStateExceptionTest() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("IllegalStateException");
        Object[] targets = new Object[1];
        targets[0] = new MClass();

        Class[] interfaces = new Class[1];
        interfaces[0] = MInterface.class;

        MInterface inter = (MInterface) new ShardingProxyFactory().createProxy(targets, interfaces);
        inter.throwIllegalStateException();
    }

    @Test
    public void tests() {
        Object[] targets = new Object[2];
        targets[0] = new MClass();
        targets[1] = new MClass();

        Class[] interfaces = new Class[1];
        interfaces[0] = MInterface.class;

        MInterface inter = (MInterface) new ShardingProxyFactory().createProxy(targets, interfaces);

        try {
            assertTrue(inter.getLong(2L) == 2L);
            assertTrue(inter.sum(1, 2) == 3);
            assertTrue(inter.div(10L, 2L) == 5L);
            assertTrue(inter.get1() == 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
