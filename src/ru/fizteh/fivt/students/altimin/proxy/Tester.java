package ru.fizteh.fivt.students.altimin.proxy;

import ru.fizteh.fivt.PerformanceTest;
import ru.fizteh.fivt.proxy.*;
import ru.fizteh.fivt.proxy.ShardingProxyFactory;
import ru.fizteh.fivt.students.altimin.proxy.test.Interface;
import ru.fizteh.fivt.students.altimin.proxy.test.TestClass;

/**
 * User: altimin
 * Date: 12/13/12
 * Time: 4:51 AM
 */

class TestRunner implements Runnable {
    ru.fizteh.fivt.proxy.ShardingProxyFactory factory;
    Interface proxy;
    final int objectCount = (int) 1e3;
    final int testCount = (int) 1e5;

    TestRunner(ShardingProxyFactory factory) {
        this.factory = factory;
        Class[] ifaces = new Class[1];
        ifaces[0] = Interface.class;
        Object[] objects = new Object[objectCount];
        for (int i = 0; i < objectCount; i ++) {
            objects[i] = new TestClass(i);
        }
        proxy = (Interface) factory.createProxy(objects, ifaces);
    }

    @Override
    public void run() {
        for (int i = 0; i < testCount; i ++) {
            proxy.collectInt();
            proxy.collectIntObject();
            proxy.collectLong();
            proxy.collectLongObject();
            proxy.collectVoid();
        }
    }
}

public class Tester {
    public static void main(String[] args) {
        TestRunner reflectionProxy = new TestRunner(new ru.fizteh.fivt.students.altimin.proxy.ShardingProxyFactory());
        PerformanceTest reflectionProxyTest = new PerformanceTest(reflectionProxy);
        long reflectionProxyTime = reflectionProxyTest.runTest(2).getSingle().getMillis();

        TestRunner asmProxy = new TestRunner(new ru.fizteh.fivt.students.altimin.proxy.AsmShardingProxyFactory());
        PerformanceTest asmProxyTest = new PerformanceTest(asmProxy);
        long asmProxyTime = reflectionProxyTest.runTest(2).getSingle().getMillis();

        System.out.println("Proxy with reflection ok with " + reflectionProxyTime + " ms.");
        System.out.println("Proxy with asm ok with " + asmProxyTime + " ms.");
    }
}
