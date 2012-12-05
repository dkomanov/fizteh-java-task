package ru.fizteh.fivt.students.tolyapro.proxy;

import java.lang.reflect.Array;

import org.junit.*;

public class TestProxy {

    interface InterfaceTestEscape {
        public String testEscape(String string);

    }

    class ClassTestEscape implements InterfaceTestEscape {

        @Override
        public String testEscape(String string) {
            String tmp = new String();
            tmp += "\n\t String: \n" + string + "\n";
            return tmp;
        }

    }

    interface InterfaceWithoutMethods {

    }

    class ClassWithoutMethods implements InterfaceWithoutMethods {

    }

    interface SimpleInterface {
        int getOne();

        void getException();

        String getHello();

        Object testBadRef();
    }

    class SimpleClass implements SimpleInterface {

        @Override
        public int getOne() {
            return 1;
        }

        @Override
        public String getHello() {
            return "Hello world!";
        }

        @Override
        public void getException() {
            throw new RuntimeException("I am an exception");
        }

        @Override
        public Object testBadRef() {
            Object[] array = new Object[1];
            array[0] = array;
            return array;
        }

    }

    interface ExtendedInterface {
        public int[] fillTheArray(int n);

        public void iAmJustVeryVeryBigMethodNameAndICanBeEvenBiggerExclamationMark(
                int small);

        public void getException(String string);

    }

    class ExtendedClass implements ExtendedInterface {

        @Override
        public int[] fillTheArray(int n) {
            int[] result = new int[n];
            for (int i = 0; i < n; ++i) {
                result[i] = n;
            }
            return result;
        }

        @Override
        public void iAmJustVeryVeryBigMethodNameAndICanBeEvenBiggerExclamationMark(
                int small) {
            small++;
        }

        @Override
        public void getException(String string) {
            throw new RuntimeException(string);

        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void testNull1() {
        LoggingProxyFactory factory = new LoggingProxyFactory();
        SimpleClass simpleClass = new SimpleClass();
        StringBuffer writer = new StringBuffer();
        factory.createProxy(simpleClass, null, SimpleInterface.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNull2() {
        LoggingProxyFactory factory = new LoggingProxyFactory();
        SimpleClass simpleClass = new SimpleClass();
        StringBuffer writer = new StringBuffer();
        factory.createProxy(null, writer, SimpleInterface.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadInterface() {
        LoggingProxyFactory factory = new LoggingProxyFactory();
        ClassWithoutMethods classWithoutMethods = new ClassWithoutMethods();
        StringBuffer writer = new StringBuffer();
        factory.createProxy(classWithoutMethods, null,
                InterfaceWithoutMethods.class);
    }

    @Test
    public void testSimple() {

        LoggingProxyFactory factory = new LoggingProxyFactory();
        SimpleClass simpleClass = new SimpleClass();
        StringBuffer writer = new StringBuffer();
        SimpleInterface proxy = (SimpleInterface) factory.createProxy(
                simpleClass, writer, SimpleInterface.class);
        proxy.getOne();
        Assert.assertEquals("SimpleInterface.getOne() returned 1\n",
                writer.toString());
        proxy.getHello();
        Assert.assertTrue(writer.toString().contains(
                "SimpleInterface.getHello() returned \"Hello world!\""));
        proxy.getException();
    }

    @Test
    public void testExtended() {

        LoggingProxyFactory factory = new LoggingProxyFactory();
        ExtendedClass extendedClass = new ExtendedClass();
        StringBuffer writer = new StringBuffer();
        ExtendedInterface proxy = (ExtendedInterface) factory.createProxy(
                extendedClass, writer, ExtendedInterface.class);
        proxy.fillTheArray(10);
        Assert.assertEquals(
                "ExtendedInterface.fillTheArray(10) returned 10{10, 10, 10, 10, 10, 10, 10, 10, 10, 10}\n",
                writer.toString());
        proxy.iAmJustVeryVeryBigMethodNameAndICanBeEvenBiggerExclamationMark(100500);
        Assert.assertTrue(writer
                .toString()
                .contains(
                        "ExtendedInterface.iAmJustVeryVeryBigMethodNameAndICanBeEvenBiggerExclamationMark(100500)"));
        proxy.getException("This string is so big that nobody will read it till the end This string is so big that nobody will read it till the end This string is so big that nobody will read it till the end");
        Assert.assertTrue(writer
                .toString()
                .contains(
                        "ExtendedInterface.getException(\"This string is so big that nobody will read it till the end This string is so big that nobody will read it till the end This string is so big that nobody will read it till the end\"\n) \nthrew"));
        // System.out.println(writer);

    }

    @Test
    public void testEscape() {
        LoggingProxyFactory factory = new LoggingProxyFactory();
        ClassTestEscape classTestEscape = new ClassTestEscape();
        StringBuffer writer = new StringBuffer();
        InterfaceTestEscape proxy = (InterfaceTestEscape) factory.createProxy(
                classTestEscape, writer, InterfaceTestEscape.class);
        proxy.testEscape("yet another string");
        String returned = writer.toString();
        // System.out.println(returned);
        Assert.assertTrue(returned
                .contains("\"\\n\\t String: \\nyet another string\\n\""));
    }

}
