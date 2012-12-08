package ru.fizteh.fivt.students.altimin.proxy;

import org.junit.Rule;
import org.junit.Test;
import org.junit.Assert;
import org.junit.rules.ExpectedException;
import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;

import java.util.*;

import static junit.framework.Assert.assertEquals;


/**
 * User: altimin
 * Date: 12/7/12
 * Time: 2:53 AM
 */
public class ShardingProxyFactoryTest {
    private static interface Interface {
        int takeInt(int v);

        long takeLong(long v);

        @DoNotProxy
        void methodNotForProxy();

        @Collect
        int collectInt();

        @Collect
        long collectLong();

        @Collect
        void collectVoid();

        @Collect
        List collectList();

        @Collect
        ArrayList collectArrayList();

        @Collect
        LinkedList collectLinkedList();
    }

    private static interface EmptyInterface {
    }

    private static class EmptyClass implements EmptyInterface {
    }

    private static interface IncorrectInterface {
        @Collect
        Map collectMap();
    }

    private static class IncorrectClass implements IncorrectInterface {
        @Override
        public Map collectMap() {
            Map map = new HashMap();
            map.put(1, 1);
            return map;
        }
    }

    private static class TestClass implements Interface {
        private int v;

        private TestClass(int v) {
            this.v = v;
        }

        @Override
        public ArrayList collectArrayList() {
            ArrayList list = new ArrayList();
            list.add(v);
            return list;
        }

        @Override
        public List collectList() {
            List list = new LinkedList();
            list.add(v);
            return list;
        }

        @Override
        public LinkedList collectLinkedList() {
            LinkedList list = new LinkedList();
            list.add(v);
            return list;
        }

        @Override
        public int takeInt(int V) {
            return v;
        }

        @Override
        public long takeLong(long V) {
            return v;
        }

        @Override
        public void methodNotForProxy() {
        }

        @Override
        public int collectInt() {
            return v;
        }

        @Override
        public long collectLong() {
            return v;
        }

        @Override
        public void collectVoid() {
        }
    }

    private Interface getProxy() {
        Object[] objects = new Object[3];
        objects[0] = new TestClass(1);
        objects[1] = new TestClass(2);
        objects[2] = new TestClass(3);
        Class[] interfaces = new Class[1];
        interfaces[0] = Interface.class;
        return (Interface) new ShardingProxyFactory().createProxy(objects, interfaces);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyInterface() {
        Object[] objects = new Object[3];
        objects[0] = new TestClass(1);
        objects[1] = new TestClass(2);
        objects[2] = new TestClass(3);
        Class[] interfaces = new Class[2];
        interfaces[0] = Interface.class;
        interfaces[1] = EmptyInterface.class;
        Interface proxy = (Interface) new ShardingProxyFactory().createProxy(objects, interfaces);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClassWithNoInterface() {
        Object[] objects = new Object[3];
        objects[0] = new EmptyClass();
        objects[1] = new EmptyClass();
        objects[2] = new EmptyClass();
        Class[] interfaces = new Class[1];
        interfaces[0] = Interface.class;
        Interface proxy = (Interface) new ShardingProxyFactory().createProxy(objects, interfaces);
    }

    @Test(expected = IllegalStateException.class)
    public void testIncorrectCollection() {
        Object[] objects = new Object[2];
        objects[0] = new IncorrectClass();
        objects[1] = new IncorrectClass();
        Class[] interfaces = new Class[1];
        interfaces[0] = IncorrectInterface.class;
        Interface proxy = (Interface) new ShardingProxyFactory().createProxy(objects, interfaces);
    }


    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void doNotProxyTest() {
        expected.expect(RuntimeException.class);
        expected.expectMessage("It's impossible to call method methodNotForProxy with @DoNotProxy annotation");
        Interface proxy = getProxy();
        proxy.methodNotForProxy();
    }

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void test() {
        //expected.expect(RuntimeException.class);
        //expected.expectMessage();
    }


    @Test
    public void testProxy() {
        Interface proxy = getProxy();
        proxy.collectVoid();
        for (int i = 0; i < 3; i ++) {
            assertEquals(i + 1, proxy.takeInt(i));
            assertEquals(i + 1, proxy.takeLong(i));
        }
        assertEquals(6, proxy.collectInt());
        assertEquals(6, proxy.collectLong());
        List list = new LinkedList();
        list.add(1);
        list.add(2);
        list.add(3);
        assertEquals(list, proxy.collectList());
        assertEquals(list, proxy.collectArrayList());
        assertEquals(list, proxy.collectLinkedList());
    }




}
