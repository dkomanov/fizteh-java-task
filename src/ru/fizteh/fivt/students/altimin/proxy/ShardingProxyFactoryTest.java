package ru.fizteh.fivt.students.altimin.proxy;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;
import ru.fizteh.fivt.students.altimin.proxy.test.Interface;
import ru.fizteh.fivt.students.altimin.proxy.test.TestClass;

import java.io.IOException;
import java.util.*;

import static junit.framework.Assert.assertEquals;


/**
 * User: altimin
 * Date: 12/7/12
 * Time: 2:53 AM
 */
public class ShardingProxyFactoryTest {

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



    private Interface getProxy() {
        Object[] objects = new Object[3];
        objects[0] = new TestClass(1);
        objects[1] = new TestClass(2);
        objects[2] = new TestClass(3);
        Class[] interfaces = new Class[1];
        interfaces[0] = Interface.class;
        return (Interface) new ShardingProxyFactory().createProxy(objects, interfaces);
    }

    private Interface getAsmProxy() {
        Object[] objects = new Object[3];
        objects[0] = new TestClass(1);
        objects[1] = new TestClass(2);
        objects[2] = new TestClass(3);
        Class[] interfaces = new Class[1];
        interfaces[0] = Interface.class;
        return (Interface) new AsmShardingProxyFactory().createProxy(objects, interfaces);
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
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("It's impossible to call method methodNotForProxy with @DoNotProxy annotation");
        Interface proxy = getProxy();
        proxy.methodNotForProxy();
    }

    @Test
    public void doNotProxyAsmTest() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("It's impossible to call method with @DoNotProxy annotation");
        Interface proxy = getAsmProxy();
        proxy.methodNotForProxy();
    }

    @Test
    public void testProxy() {
        Interface proxy = getProxy();
        proxy.collectVoid();
        proxy.collectVoidObject();
        for (int i = 0; i < 3; i ++) {
            assertEquals(i + 1, proxy.takeInt(i));
            assertEquals(i + 1, proxy.takeLong(i));
        }
        assertEquals(6, proxy.collectInt());
        assertEquals(6, proxy.collectLong());
        assertEquals(new Integer(6), proxy.collectIntObject());
        assertEquals(new Long(6), proxy.collectLongObject());
        List list = new LinkedList();
        list.add(1);
        list.add(2);
        list.add(3);
        assertEquals(list, proxy.collectList());
        assertEquals(list, proxy.collectArrayList());
        assertEquals(list, proxy.collectLinkedList());
    }

    @Test
    public void testAsmProxy() {
        Interface proxy = getAsmProxy();
        proxy.collectVoid();
        for (int i = 0; i < 3; i ++) {
            assertEquals(i + 1, proxy.takeInt(i));
            assertEquals(i + 1, proxy.takeLong(i));
            assertEquals(new Integer(i + 1), proxy.takeIntObject(i));
            assertEquals(new Long(i + 1), proxy.takeLongObject(new Long(i)));
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

    @Test(expected = IOException.class)
    public void checkException() throws IOException {
        Interface proxy = getProxy();
        proxy.throwException();
    }

    @Test(expected = IOException.class)
    public void checkAsmException() throws IOException {
        Interface proxy = getAsmProxy();
        proxy.throwException();
    }

    public interface Interface1 {
        void method(int i);
    }

    public interface Interface2 {
        void method(int i);
    }

    private static class Implementation implements Interface1, Interface2 {
        @Override
        public void method(int i) {
        }
    }

    @Test
    public void interfaceWithTheSameMethodShouldNotFail() {
        Object proxy = new AsmShardingProxyFactory().createProxy(new Object[] {new Implementation()}, new Class[] {Interface1.class, Interface2.class});
        ((Interface1) proxy).method(1);
        ((Interface2) proxy).method(1);
    }

}
