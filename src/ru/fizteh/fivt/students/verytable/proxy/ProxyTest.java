package ru.fizteh.fivt.students.verytable.proxy;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

interface SomeInterface {

    String someMethod1(String s);

    int someMethod2(int i);

    Integer someMethod3(int i);

    double someMethod4(double d);

    void someMethod5(Enum e);

    boolean someMethod6(Object[] list);

    float someVeryBigOrEvenEnormousMethodReturningFloatOfSomeInterface(int i);

    void someMethod7(Object[] list);

    void someMethod8(int[] array);

    void someMethod9(List list);

    String someMethod10(String s, String t, String u);
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
        for (Object i : list) {
            System.out.print(i.toString() + " ");
        }
    }

    @Override
    public void someMethod8(int[] array) {
    }

    @Override
    public void someMethod9(List list) {
        list.clear();
    }

    @Override
    public String someMethod10(String s, String t, String u) {
        return s + t + u;
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
        factory.createProxy(siwmi, writer, SomeInterfaceWithoutMethods.class);
    }

    @Test(expected = RuntimeException.class)
    public void cyclicReference() {
        LoggingProxyFactory factory = new LoggingProxyFactory();
        StringBuffer writer = new StringBuffer();
        SomeInterfaceImpl sii = new SomeInterfaceImpl();
        SomeInterface logger = (SomeInterface) factory.createProxy(sii, writer, SomeInterface.class);
        Object[] list = new Object[1];
        list[0] = list;
        logger.someMethod7(list);
    }

    @Test
    public void primitiveArray() {
        LoggingProxyFactory factory = new LoggingProxyFactory();
        StringBuffer writer = new StringBuffer();
        SomeInterfaceImpl sii = new SomeInterfaceImpl();
        SomeInterface logger = (SomeInterface) factory.createProxy(sii, writer, SomeInterface.class);
        int[] array = new int[2];
        array[0] = 1;
        array[1] = 0;
        logger.someMethod8(array);
        Assert.assertEquals("SomeInterface.someMethod8(2{\"1\", \"0\"})\n", writer.toString());
    }

    @Test
    public void NPE() {
        LoggingProxyFactory factory = new LoggingProxyFactory();
        StringBuffer writer = new StringBuffer();
        SomeInterfaceImpl sii = new SomeInterfaceImpl();
        Object[] list = new Object[2];
        list[0] = 0;
        list[1] = null;
        SomeInterface logger = (SomeInterface) factory.createProxy(sii, writer, SomeInterface.class);
        try {
            logger.someMethod7(list);
        } catch (Throwable ex) {
            Assert.assertEquals("SomeInterface.someMethod7(2{\"0\", \"null\"})"
                    + " threw NullPointerException:  null\n"
                    + "  ru.fizteh.fivt.students.verytable.proxy.SomeInterfaceImpl"
                    + ".someMethod7(ProxyTest.java:74)",
                    writer.toString().substring(0, 166));
        }
    }

    @Test(expected = NullPointerException.class)
    public void nullList() {
        LoggingProxyFactory factory = new LoggingProxyFactory();
        StringBuffer writer = new StringBuffer();
        SomeInterfaceImpl sii = new SomeInterfaceImpl();
        SomeInterface logger = (SomeInterface) factory.createProxy(sii, writer, SomeInterface.class);
        List list = null;
        logger.someMethod9(list);
    }

    @Test
    public void screening() {
        LoggingProxyFactory factory = new LoggingProxyFactory();
        StringBuffer writer = new StringBuffer();
        List<String> list = new ArrayList<>();
        List<String> proxy = (List<String>) factory.createProxy(list, writer, list.getClass().getInterfaces());
        proxy.add("hello\\\\ \n world\\\"");
        Assert.assertEquals(writer.toString(), "List.add(\"hello\\\\\\\\ \\n world\\\\\\\"\") returned true\n");
    }

    @Test
    public void similarToCycle() {
        LoggingProxyFactory factory = new LoggingProxyFactory();
        StringBuffer writer = new StringBuffer();
        SomeInterfaceImpl sii = new SomeInterfaceImpl();
        SomeInterface logger = (SomeInterface) factory.createProxy(sii, writer, SomeInterface.class);
        String s = "1";
        logger.someMethod10(s, s, s);
        Assert.assertEquals("SomeInterface.someMethod10(\"1\", \"1\", \"1\") returned \"111\"\n", writer.toString());
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
        Assert.assertEquals("SomeInterface.someMethod5(WINTER)\n", writer.toString());
        writer.setLength(0);

        Object[] list = {1, 2};
        logger.someMethod6(list);
        Assert.assertEquals("SomeInterface.someMethod6(2{\"1\", \"2\"}) returned true\n", writer.toString());
        writer.setLength(0);

        Object[] extended = {list, 3};
        logger.someMethod6(extended);
        Assert.assertEquals("SomeInterface.someMethod6(2{\"2{\\\"1\\\", \\\"2\\\"}\", \"3\"}) returned true\n", writer.toString());
        writer.setLength(0);

        logger.someVeryBigOrEvenEnormousMethodReturningFloatOfSomeInterface(0);
        Assert.assertEquals("SomeInterface.someVeryBigOrEvenEnormousMethodReturningFloatOfSomeInterface(0) returned 0.0\n", writer.toString());
        writer.setLength(0);

        List newList = new ArrayList();
        newList.add(1);
        logger.someMethod9(newList);
        Assert.assertEquals("SomeInterface.someMethod9([[1]])\n", writer.toString());
        writer.setLength(0);
    }
}
