package ru.fizteh.fivt.students.dmitriyBelyakov.proxy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ShardingProxyFactoryTest extends Assert {
    private InterfaceForTests proxy;

    @Before
    public void createProxy() {
        ShardingProxyFactory factory = new ShardingProxyFactory();
        Class[] interfaces = new Class[1];
        interfaces[0] = InterfaceForTests.class;
        Object[] targets = new Object[3];
        targets[0] = new ClassForTests(0);
        targets[1] = new ClassForTests(1);
        targets[2] = new ClassForTests(2);
        proxy  = (InterfaceForTests) factory.createProxy(targets, interfaces);
    }

    @Test(expected = RuntimeException.class)
    public void testDoNotProxy() {
        proxy.numNotForProxy();
    }

    @Test
    public void testProxy() {
        for(int i = 0; i < 3; ++i) {
            assertEquals(i, proxy.numInt(i));
            assertEquals(i, proxy.numLong(i));
        }
        proxy.numCollectVoid();
        assertEquals(3, proxy.numCollectInt());
        assertEquals(3, proxy.numCollectLong());
        List list = new ArrayList();
        for(int i = 0; i < 3; ++i) {
            list.add(i);
            list.add(i + 1);
            list.add(i + 2);
        }
        assertEquals(list, proxy.numCollectList());
    }
}