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
    
}
