package ru.fizteh.fivt.students.dmitriyBelyakov.proxy.asmProxy;

import org.junit.Assert;
import org.junit.Test;
import ru.fizteh.fivt.students.dmitriyBelyakov.proxy.proxy.test.ClassForTests;
import ru.fizteh.fivt.students.dmitriyBelyakov.proxy.proxy.test.InterfaceForTests;

public class AsmProxyTest extends Assert {
    @Test
    public void main() {
        AsmShardingProxyFactory factory = new AsmShardingProxyFactory();
        Class[] interfaces = new Class[1];
        interfaces[0] = InterfaceForTests.class;
        Object[] targets = new Object[3];
        targets[0] = new ClassForTests(0);
        targets[1] = new ClassForTests(1);
        targets[2] = new ClassForTests(2);
        InterfaceForTests proxy = (InterfaceForTests) factory.createProxy(targets, interfaces);
        proxy.numCollectVoid();
    }
}