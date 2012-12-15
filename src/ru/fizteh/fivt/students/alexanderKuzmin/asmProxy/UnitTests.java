package ru.fizteh.fivt.students.alexanderKuzmin.asmProxy;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Alexander Kuzmin group 196 Class UnitTests
 * 
 */

public class UnitTests {

    @Test(expected = IllegalArgumentException.class)
    public void testProxyWithNullTargets() {
        new ShardingAsmProxyFactory().createProxy(null, new Class[1]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProxyWithEmptyTargets() {
        new ShardingAsmProxyFactory().createProxy(new Object[0], new Class[1]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProxyWithNullInterfaces() {
        new ShardingAsmProxyFactory().createProxy(new Object[1], null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProxyWithEmptyInterfaces() {
        new ShardingAsmProxyFactory().createProxy(new Object[1], new Class[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProxyWithOneEmptyInterface() {
        Class<?>[] clazz = new Class[3];
        clazz[1] = null;
        new ShardingAsmProxyFactory().createProxy(new Object[1], clazz);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProxyWithOneNullTarget() {
        Object[] clazz = new Object[2];
        Class<?>[] interf = new Class[1];
        clazz[0] = new MyTestClass();
        interf[0] = MyTestInterface.class;
        clazz[1] = null;
        new ShardingAsmProxyFactory().createProxy(clazz, interf);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProxyWithOneNullInterface() {
        Object[] clazz = new Object[1];
        Class<?>[] interf = new Class[2];
        clazz[0] = new MyTestClass();
        interf[0] = MyTestInterface.class;
        interf[1] = null;
        new ShardingAsmProxyFactory().createProxy(clazz, interf);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProxyWithNoSuchClasses() {
        Object[] clazz = new Object[1];
        Class<?>[] interf = new Class[1];
        clazz[0] = new MyTestOtherClass();
        interf[0] = MyTestInterface.class;
        new ShardingAsmProxyFactory().createProxy(clazz, interf);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProxyWithNoMethodInInterface() {
        Object[] clazz = new Object[1];
        Class<?>[] interf = new Class[2];
        clazz[0] = new MyTestClass();
        interf[0] = MyTestOtherInterface.class;
        interf[1] = MyTestInterface.class;
        new ShardingAsmProxyFactory().createProxy(clazz, interf);
    }

    @Test(expected = IllegalStateException.class)
    public void testProxyWithDoNotProxyAnnotation() {
        Object[] clazz = new Object[2];
        Class<?>[] interf = new Class[1];
        clazz[0] = new MyTestClass();
        clazz[1] = new MyTestClass();
        interf[0] = MyTestInterface.class;
        ShardingAsmProxyFactory factory = new ShardingAsmProxyFactory();
        MyTestInterface myInter = (MyTestInterface) factory.createProxy(clazz,
                interf);
        Assert.assertTrue(myInter.getI(412) == 124);
    }

    @Test
    public void testProxyNormal() {
        Object[] clazz = new Object[2];
        Class<?>[] interf = new Class[1];
        clazz[0] = new MyTestClass();
        clazz[1] = new MyTestClass();
        interf[0] = MyTestInterface.class;
        ShardingAsmProxyFactory factorys = new ShardingAsmProxyFactory();
        MyTestInterface myInter = (MyTestInterface) factorys.createProxy(clazz,
                interf);
        Assert.assertTrue(myInter.smth(2, 3) == 8);
        Assert.assertTrue(myInter.getA(3L) == -3L);
        Assert.assertEquals(myInter.getL(412L).longValue(), 826);
        Assert.assertEquals(myInter.getSomething(), 30);
        Assert.assertEquals(myInter.getLength("123456"), 12);
    }
}