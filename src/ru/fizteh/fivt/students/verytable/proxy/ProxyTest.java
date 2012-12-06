package ru.fizteh.fivt.students.verytable.proxy;

import org.junit.Assert;
import org.junit.Test;

interface SomeInterface {

    String someMethod1(String s);
    int someMethod2(int i);
    Integer someMethod3(int i);
    double someMethod4(double d);
    void someMethod5(Enum e);
    boolean someMethod6(Object[] list);
    float someVeryBigOrEvenEnormousMethodReturningFloatOfSomeInterface(int i);
    void someMethod7(Object[] list);
}

class SomeInterfaceImpl implements SomeInterface {

    @Override
    public String someMethod1(String s) {
        return s;
    }

    @Override
    public int someMethod2(int i) {
        return i;
    }

    @Override
    public Integer someMethod3(int i) {
        return (Integer) null;
    }

    @Override
    public double someMethod4(double d) {
        return d;
    }

    @Override
    public void someMethod5(Enum e) {
        return;
    }

    @Override
    public boolean someMethod6(Object[] list) {
        return true;
    }

    @Override
    public float someVeryBigOrEvenEnormousMethodReturningFloatOfSomeInterface(int i) {
        return (float) i;
    }

    @Override
    public void someMethod7(Object[] list) {
        list[0] = list;
    }
}

interface SomeInterfaceWithoutMethods {
}

class SomeInterfaceWithoutMethodsImpl {
}

enum Season {WINTER};

public class ProxyTest {

    @Test(expected = IllegalArgumentException.class)
    public void nullTarget() {
        LoggingProxyFactory factory = new LoggingProxyFactory();
        factory.createProxy(null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullWriter() {
        LoggingProxyFactory factory = new LoggingProxyFactory();
        SomeInterfaceImpl sii = new SomeInterfaceImpl();
        factory.createProxy(sii, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullInterfaces() {
        LoggingProxyFactory factory = new LoggingProxyFactory();
        StringBuffer writer = new StringBuffer();
        SomeInterfaceWithoutMethodsImpl siwmi = new SomeInterfaceWithoutMethodsImpl();
        factory.createProxy(siwmi, writer, siwmi.getClass());
    }

    @Test(expected = RuntimeException.class)
    public void cyclicReference() {
        LoggingProxyFactory factory = new LoggingProxyFactory();
        StringBuffer writer = new StringBuffer();
        SomeInterfaceImpl sii = new SomeInterfaceImpl();
        SomeInterface logger = (SomeInterface) factory.createProxy(sii, writer, sii.getClass());
        Object[] list = new Object[1];
        logger.someMethod7(list);
    }

    @Test
    public void someTest() {
        LoggingProxyFactory factory = new LoggingProxyFactory();
        StringBuffer writer = new StringBuffer();
        SomeInterfaceImpl sii = new SomeInterfaceImpl();
        SomeInterface logger = (SomeInterface) factory.createProxy(sii, writer,
                SomeInterface.class);

        logger.someMethod1("hello");
        Assert.assertEquals("SomeInterface.someMethod1(\"hello\") returned \"hello\"\n",
                writer.toString());
        writer.setLength(0);

        String longString = "ababahalamahaaababahalamahaaababahalabahaaababahalabahaaabab";
        logger.someMethod1(longString);
        Assert.assertEquals("SomeInterface.someMethod1(\n" +
                "  \"ababahalamahaaababahalamahaaababahalabahaaababahalabahaaabab\"\n" +
                "  )\n" +
                "  returned \"ababahalamahaaababahalamahaaababahalabahaaababahalabahaaabab\"\n",
                writer.toString());
        writer.setLength(0);

        logger.someMethod2(0);
        Assert.assertEquals("SomeInterface.someMethod2(0) returned 0\n", writer.toString());
        writer.setLength(0);

        logger.someMethod3(0);
        Assert.assertEquals("SomeInterface.someMethod3(0) returned null\n", writer.toString());
        writer.setLength(0);

        logger.someMethod4(0.5);
        Assert.assertEquals("SomeInterface.someMethod4(0.5) returned 0.5\n", writer.toString());
        writer.setLength(0);

        logger.someMethod5(Season.WINTER);
        Assert.assertEquals("SomeInterface.someMethod5(WINTER) returned null\n", writer.toString());
        writer.setLength(0);

        Object[] list = {1, 2};
        logger.someMethod6(list);
        Assert.assertEquals("SomeInterface.someMethod6(2{\"1\", \"2\"}) returned true\n", writer.toString());
        writer.setLength(0);

        Object[] extended = {list, 3};
        logger.someMethod6(extended);
        Assert.assertEquals("SomeInterface.someMethod6(2{\"2{\\\\\"1\\\\\", \\\\\"2\\\\\"}\", \"3\"}) returned true\n", writer.toString());
        writer.setLength(0);

        logger.someVeryBigOrEvenEnormousMethodReturningFloatOfSomeInterface(0);
        Assert.assertEquals("SomeInterface.someVeryBigOrEvenEnormousMethodReturningFloatOfSomeInterface(0) returned 0.0\n", writer.toString());
        writer.setLength(0);
    }
}
