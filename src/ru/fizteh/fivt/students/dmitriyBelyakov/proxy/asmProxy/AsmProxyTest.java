package ru.fizteh.fivt.students.dmitriyBelyakov.proxy.asmProxy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.fizteh.fivt.students.dmitriyBelyakov.proxy.tests.*;

import java.util.ArrayList;
import java.util.List;

public class AsmProxyTest extends Assert {
    private InterfaceForTests proxy;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void createProxy() {
        AsmShardingProxyFactory factory = new AsmShardingProxyFactory();
        Class[] interfaces = new Class[1];
        interfaces[0] = InterfaceForTests.class;
        Object[] targets = new Object[3];
        targets[0] = new ClassForTests(0);
        targets[1] = new ClassForTests(1);
        targets[2] = new ClassForTests(2);
        proxy = (InterfaceForTests) factory.createProxy(targets, interfaces);
    }

    @Test
    public void testDoNotProxy() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("This method not for proxy.");
        proxy.numNotForProxy();
    }

    @Test
    public void testNoTargets() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("No one target found.");
        AsmShardingProxyFactory factory = new AsmShardingProxyFactory();
        Class[] interfaces = new Class[1];
        interfaces[0] = InterfaceForTests.class;
        Object[] targets = new Object[0];
        proxy = (InterfaceForTests) factory.createProxy(targets, interfaces);
    }

    @Test
    public void testIncorrectInterface() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("One of interfaces hasn't methods.");
        AsmShardingProxyFactory factory = new AsmShardingProxyFactory();
        Class[] interfaces = new Class[2];
        interfaces[0] = InterfaceForTests.class;
        interfaces[1] = InterfaceForTestsWithoutMethods.class;
        Object[] targets = new Object[3];
        targets[0] = new ClassForTests(0);
        targets[1] = new ClassForTests(1);
        targets[2] = new ClassForTests(2);
        proxy = (InterfaceForTests) factory.createProxy(targets, interfaces);
    }

    @Test
    public void testIsNotInterface() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("java.util.ArrayList is not interface.");
        AsmShardingProxyFactory factory = new AsmShardingProxyFactory();
        Class[] interfaces = new Class[1];
        interfaces[0] = ArrayList.class;
        Object[] targets = new Object[3];
        targets[0] = new ClassForTests(0);
        targets[1] = new ClassForTests(1);
        targets[2] = new ClassForTests(2);
        proxy = (InterfaceForTests) factory.createProxy(targets, interfaces);
    }

    @Test
    public void testIncorrectAnnotations() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Incorrect annotation.");
        AsmShardingProxyFactory factory = new AsmShardingProxyFactory();
        Class[] interfaces = new Class[2];
        interfaces[0] = InterfaceForTests.class;
        interfaces[1] = InterfaceForTestsWithIncorrectAnnotations.class;
        Object[] targets = new Object[3];
        targets[0] = new ClassForTests(0);
        targets[1] = new ClassForTests(1);
        targets[2] = new ClassForTests(2);
        proxy = (InterfaceForTests) factory.createProxy(targets, interfaces);
    }

    @Test
    public void testIncorrectArguments() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("All methods must have int or long argument.");
        AsmShardingProxyFactory factory = new AsmShardingProxyFactory();
        Class[] interfaces = new Class[2];
        interfaces[0] = InterfaceForTests.class;
        interfaces[1] = InterfaceForTestsWithoutNumArgument.class;
        Object[] targets = new Object[3];
        targets[0] = new ClassForTests(0);
        targets[1] = new ClassForTests(1);
        targets[2] = new ClassForTests(2);
        proxy = (InterfaceForTests) factory.createProxy(targets, interfaces);
    }

    @Test
    public void testNoInterfaces() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("No one interface found.");
        AsmShardingProxyFactory factory = new AsmShardingProxyFactory();
        Class[] interfaces = new Class[0];
        Object[] targets = new Object[3];
        targets[0] = new ClassForTests(0);
        targets[1] = new ClassForTests(1);
        targets[2] = new ClassForTests(2);
        proxy = (InterfaceForTests) factory.createProxy(targets, interfaces);
    }

    @Test
    public void testNullTarget() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("NULL target.");
        AsmShardingProxyFactory factory = new AsmShardingProxyFactory();
        Class[] interfaces = new Class[1];
        interfaces[0] = InterfaceForTests.class;
        Object[] targets = new Object[3];
        targets[0] = new ClassForTests(0);
        targets[1] = new ClassForTests(1);
        targets[2] = null;
        proxy = (InterfaceForTests) factory.createProxy(targets, interfaces);
    }

    @Test
    public void testTargetWithoutInterfaces() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("One of targets doesn't implement interface from interfaces.");
        AsmShardingProxyFactory factory = new AsmShardingProxyFactory();
        Class[] interfaces = new Class[1];
        interfaces[0] = InterfaceForTests.class;
        Object[] targets = new Object[3];
        targets[0] = new ClassForTests(0);
        targets[1] = new ClassForTests(1);
        targets[2] = new ClassWithoutInterfaces();
        proxy = (InterfaceForTests) factory.createProxy(targets, interfaces);
    }

    @Test
    public void testConflictInMethods() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Conflict methods in interfaces.");
        AsmShardingProxyFactory factory = new AsmShardingProxyFactory();
        Class[] interfaces = new Class[2];
        interfaces[0] = InterfaceForTests.class;
        interfaces[1] = InterfaceConflictWithInterfaceForTests.class;
        Object[] targets = new Object[1];
        targets[0] = new ClassForTests(1);
        proxy = (InterfaceForTests) factory.createProxy(targets, interfaces);
    }

    @Test
    public void testProxy() {
        for (int i = 0; i < 3; ++i) {
            assertEquals(i, proxy.numInt(i));
            assertEquals(i, proxy.numLong(i));
        }
        proxy.numCollectVoid();
        assertEquals(3, proxy.numCollectInt());
        assertEquals(3, proxy.numCollectLong());
        List list = new ArrayList();
        for (int i = 0; i < 3; ++i) {
            list.add(i);
            list.add(i + 1);
            list.add(i + 2);
        }
        assertEquals(list, proxy.numCollectList());
    }
}