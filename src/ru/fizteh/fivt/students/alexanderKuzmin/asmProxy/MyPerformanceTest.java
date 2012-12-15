package ru.fizteh.fivt.students.alexanderKuzmin.asmProxy;

import org.junit.Assert;
import ru.fizteh.fivt.PerformanceTest;
import proxy.ShardingProxyFactory;

/**
 * @author Alexander Kuzmin group 196 Class MyPerformanceTest
 * 
 */

class Runner implements Runnable {
    ru.fizteh.fivt.proxy.ShardingProxyFactory factory;
    MyTestInterface myInter;

    Runner(ru.fizteh.fivt.proxy.ShardingProxyFactory factory) {
        Object[] clazz = new Object[2];
        Class<?>[] interf = new Class[1];
        clazz[0] = new MyTestClass();
        clazz[1] = new MyTestClass();
        interf[0] = MyTestInterface.class;
        this.factory = factory;
        myInter = (MyTestInterface) this.factory.createProxy(clazz, interf);
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; ++i) {
            Assert.assertTrue(myInter.smth(2, 3) == 8);
            Assert.assertTrue(myInter.getA(3L) == -3L);
            Assert.assertEquals(myInter.getL(412L).longValue(), 826);
            Assert.assertEquals(myInter.getSomething(), 30);
            Assert.assertEquals(myInter.getLength("123456"), 12);
        }
    }
}

public class MyPerformanceTest {

    public static void main(String[] args) {
        Runner ShardingProxyFactoryRunner = new Runner(
                new ShardingProxyFactory());
        PerformanceTest testSimpleProxy = new PerformanceTest(
                ShardingProxyFactoryRunner);
        System.out.println("ShardingProxyFactoryRunner result: "
                + testSimpleProxy.runTest(100000).getSingle().getNanos());

        Runner ShardingAsmProxyFactoryRunner = new Runner(
                new ShardingAsmProxyFactory());
        PerformanceTest testAsmProxy = new PerformanceTest(
                ShardingAsmProxyFactoryRunner);
        System.out.println("ShardingAsmProxyFactoryRunner result: "
                + testAsmProxy.runTest(100000).getSingle().getNanos());
    }
}