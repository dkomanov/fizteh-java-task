package ru.fizteh.fivt.students.nikitaAntonov.proxy;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class UnitTests {

    interface EmptyInterface {

    };

    interface HasVoidResultMethod {
        void proc(int i);
    }

    interface CanWorkWithArrays {
        public Double[] setArray(Double[] newArray);

        public void voidMethod(Double[] newArray);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    LoggingProxyFactory factory = new LoggingProxyFactory();

    @Before
    public void init() {
        factory = new LoggingProxyFactory();
    }

    @Test
    public void simpleTest() {
        List<String> list = new ArrayList<>();
        StringBuilder logger = new StringBuilder();

        List<String> proxy = (List<String>) factory.createProxy(list, logger,
                list.getClass().getInterfaces());

        proxy.add("Simple string");
        proxy.isEmpty();
        proxy.get(0);
        assertEquals(logger.toString(),
                "List.add(\"Simple string\") returned true\n"
                        + "List.isEmpty() returned false\n"
                        + "List.get(0) returned \"Simple string\"\n");
    }

    @Test
    public void invisibleMethods() {
        List<String> list = new ArrayList<>();
        StringBuilder logger = new StringBuilder();

        List<String> proxy = (List<String>) factory.createProxy(list, logger,
                list.getClass().getInterfaces());

        proxy.add("Simple string");
        proxy.isEmpty();
        proxy.get(0);
        proxy.toString(); // Must be invisible
        assertEquals(logger.toString(),
                "List.add(\"Simple string\") returned true\n"
                        + "List.isEmpty() returned false\n"
                        + "List.get(0) returned \"Simple string\"\n");
    }

    @Test
    public void withoutInterfaces() {

        class TestClass implements EmptyInterface {
        }
        ;

        TestClass obj = new TestClass();
        StringBuilder logger = new StringBuilder();
        thrown.expectMessage("There is no methods for proxing");
        TestClass proxy = (TestClass) factory.createProxy(obj, logger,
                EmptyInterface.class);
    }

    @Test
    public void nullInterface1() {
        class TestClass implements EmptyInterface {
        }
        ;

        TestClass obj = new TestClass();
        StringBuilder logger = new StringBuilder();
        thrown.expectMessage("All params musn't be null");
        TestClass proxy = (TestClass) factory.createProxy(obj, logger, null);
    }

    @Test
    public void nullInterface2() {
        class TestClass implements EmptyInterface {
        }
        ;

        TestClass obj = new TestClass();
        StringBuilder logger = new StringBuilder();
        thrown.expectMessage("All params musn't be null");
        TestClass proxy = (TestClass) factory.createProxy(obj, logger,
                EmptyInterface.class, null);
    }

    @Test
    public void nullInterface3() {
        class TestClass implements EmptyInterface {
        }
        ;

        TestClass obj = new TestClass();
        StringBuilder logger = new StringBuilder();
        thrown.expectMessage("All params musn't be null");
        TestClass proxy = (TestClass) factory.createProxy(obj, null,
                EmptyInterface.class);
    }

    @Test
    public void nullInterface4() {
        class TestClass implements EmptyInterface {
        }
        ;

        TestClass obj = new TestClass();
        StringBuilder logger = new StringBuilder();
        thrown.expectMessage("All params musn't be null");
        TestClass proxy = (TestClass) factory.createProxy(null, logger,
                EmptyInterface.class);
    }

    @Test
    public void noImplements() {
        class TestClass {
        }
        ;

        TestClass obj = new TestClass();
        StringBuilder logger = new StringBuilder();
        thrown.expectMessage("object don't implement interface EmptyInterface");
        TestClass proxy = (TestClass) factory.createProxy(obj, logger,
                EmptyInterface.class);

    }

    @Test
    public void voidResult() {
        class TestClass implements HasVoidResultMethod {
            public void proc(int i) {
                // blah
            }
        }
        ;

        HasVoidResultMethod obj = new TestClass();
        StringBuilder logger = new StringBuilder();
        HasVoidResultMethod proxy = (HasVoidResultMethod) factory.createProxy(
                obj, logger, HasVoidResultMethod.class);

        proxy.proc(42);
        assertEquals(logger.toString(), "HasVoidResultMethod.proc(42)\n");

    }

    @Test
    public void arrayTest() {

        class ArrayTest implements CanWorkWithArrays {
            @Override
            public Double[] setArray(Double[] array) {
                return array;
            }

            @Override
            public void voidMethod(Double[] newArray) {
            }
        }

        ArrayTest arrayTest = new ArrayTest();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        CanWorkWithArrays proxy = (CanWorkWithArrays) factory.createProxy(
                arrayTest, builder, CanWorkWithArrays.class);
        proxy.setArray(new Double[] { 3.1415, 1.02, 23.04 });
        proxy.voidMethod(new Double[] {});
        Assert.assertEquals(
                builder.toString(),
                "CanWorkWithArrays.setArray(3{3.1415, 1.02, 23.04}) returned 3{3.1415, 1.02, 23.04}\n"
                        + "CanWorkWithArrays.voidMethod(0{})\n");
    }

    @Test
    public void longParamsTest() {
        List<String> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        List<String> proxy = (List<String>) factory.createProxy(list, builder,
                list.getClass().getInterfaces());
        proxy.add("wtf1");
        proxy.add("wtf2");
        proxy.get(0);
        proxy.add(
                2,
                "too long too long too long too long too long too long too long too long too long too long");
        proxy.add("0123456789012345678901234567890123456789012345678901234567");
        proxy.add("too long too long too long too long too long too long too long too long too long too long");
        proxy.toString();
        Assert.assertEquals(
                builder.toString(),
                "List.add(\"wtf1\") returned true\n"
                        + "List.add(\"wtf2\") returned true\n"
                        + "List.get(0) returned \"wtf1\"\n"
                        + "List.add(\n"
                        + "  2,\n"
                        + "  \"too long too long too long too long too long too long too long too long too long too long\"\n"
                        + "  )\n"
                        + "List.add(\"0123456789012345678901234567890123456789012345678901234567\") returned true\n"
                        + "List.add(\n"
                        + "  \"too long too long too long too long too long too long too long too long too long too long\"\n"
                        + "  )\n" + "  returned true\n");
    }

}
