package ru.fizteh.fivt.students.dmitriyBelyakov.proxy;

import ru.fizteh.fivt.PerformanceTest;
import ru.fizteh.fivt.students.dmitriyBelyakov.proxy.asmProxy.AsmShardingProxyFactory;
import ru.fizteh.fivt.students.dmitriyBelyakov.proxy.proxy.ShardingProxyFactory;
import ru.fizteh.fivt.students.dmitriyBelyakov.proxy.tests.ClassForTests;
import ru.fizteh.fivt.students.dmitriyBelyakov.proxy.tests.InterfaceForTests;

class RunTestProxy implements Runnable {
    ru.fizteh.fivt.proxy.ShardingProxyFactory factory;

    RunTestProxy(ru.fizteh.fivt.proxy.ShardingProxyFactory factory) {
        this.factory = factory;
    }

    @Override
    public void run() {
        Class[] interfaces = new Class[1];
        interfaces[0] = InterfaceForTests.class;
        Object[] targets = new Object[100];
        for(int i = 0; i < 100; ++i) {
            targets[i] = new ClassForTests(i % 50);
        }
        InterfaceForTests proxy = (InterfaceForTests) factory.createProxy(targets, interfaces);
        for(int i = 0; i < 1000000; ++i) {
            proxy.numCollectInt();
            proxy.numCollectVoid();
            proxy.numInt(i);
            proxy.numLong(i + 1);
        }
    }
}

public class PerformanceTestForProxy {
    public static void main(String[] args) {
        PerformanceTest testSimpleProxy = new PerformanceTest(new RunTestProxy(new ShardingProxyFactory()));
        long millisSimpleFactory = testSimpleProxy.runTest(2).getSingle().getMillis();

        PerformanceTest testAsmProxy = new PerformanceTest(new RunTestProxy(new AsmShardingProxyFactory()));
        long millisAsmFactory = testAsmProxy.runTest(2).getSingle().getMillis();

        System.out.println("Result for Proxy: " + millisSimpleFactory);
        System.out.println("Result for AsmProxy: " + millisAsmFactory);
        if(millisSimpleFactory > millisAsmFactory) {
            System.out.println("AsmProxy work " + (millisSimpleFactory / millisAsmFactory) + " times faster than Proxy.");
        } else {
            if(millisSimpleFactory > millisAsmFactory) {
                System.out.println("Proxy work " + (millisAsmFactory / millisSimpleFactory) + " times faster than AsmProxy.");
            }
        }
    }
}