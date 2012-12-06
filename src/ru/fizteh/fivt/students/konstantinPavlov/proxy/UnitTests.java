package ru.fizteh.fivt.students.konstantinPavlov.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;

public class UnitTests extends Assert {

    interface InterfaceWithoutArguments {
        void incorrectMethod();
    }

    interface InterfaceWithInvalidAnnotation {
        @Collect
        Map returnMap();
    }

    interface InterfaceForTests {
        int numInt(int num);

        int numLong(long num);

        @DoNotProxy
        void numNotForProxy();

        @Collect
        int numCollectInt();

        @Collect
        void numCollectVoid();

        @Collect
        long numCollectLong();

        @Collect
        List<?> numCollectList();
    }

    interface InterfaceWithoutMethods {
    }

    class ClassWithoutInterfaces {
        public void nothing() {
            throw new RuntimeException();
        }
    }

    class ClassForTests implements InterfaceForTests {
        public int number;

        public SubClassForTests newInstanceOfSubClass() {
            return new SubClassForTests(0);
        }

        private class SubClassForTests implements InterfaceForTests {
            SubClassForTests(int num) {
                number = num;
            }

            @Override
            public int numInt(int num) {
                return 1;
            }

            @Override
            public int numLong(long num) {
                return 2;
            }

            @Override
            public void numNotForProxy() {
                return;
            }

            @Override
            public int numCollectInt() {
                return number;
            }

            @Override
            public void numCollectVoid() {
                return;
            }

            @Override
            public long numCollectLong() {
                return number;
            }

            @Override
            public List numCollectList() {
                List returnList = new ArrayList<Integer>();
                returnList.add(number);
                returnList.add(number + 1);
                returnList.add(number + 2);
                return returnList;
            }
        }

        ClassForTests(int num) {
            number = num;
        }

        @Override
        public int numInt(int num) {
            return number;
        }

        @Override
        public int numLong(long num) {
            return number;
        }

        @Override
        public void numNotForProxy() {
            return;
        }

        @Override
        public int numCollectInt() {
            return number;
        }

        @Override
        public void numCollectVoid() {
            return;
        }

        @Override
        public long numCollectLong() {
            return number;
        }

        @Override
        public List numCollectList() {
            List returnList = new ArrayList<Integer>();
            returnList.add(number);
            returnList.add(number + 1);
            returnList.add(number + 2);
            return returnList;
        }
    }

    InterfaceForTests proxy;

    @Before
    public void createProxy() {
        ShardingProxyFactory factory = new ShardingProxyFactory();
        Class[] interfaces = new Class[1];
        interfaces[0] = InterfaceForTests.class;
        Object[] targets = new Object[3];
        targets[0] = new ClassForTests(0);
        targets[1] = new ClassForTests(1);
        targets[2] = new ClassForTests(2);
        proxy = (InterfaceForTests) factory.createProxy(targets, interfaces);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test(expected = IllegalArgumentException.class)
    public void testNullTarget() {
        ShardingProxyFactory factory = new ShardingProxyFactory();
        Class[] interfacesArray = new Class[1];
        Object[] targetsArray = new Object[2];
        targetsArray[0] = new ClassForTests(0);
        targetsArray[1] = null;
        new ShardingProxyFactory().createProxy(targetsArray, interfacesArray);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullInterface() {
        ShardingProxyFactory factory = new ShardingProxyFactory();
        Object[] targetsArray = new Object[1];
        Class[] interfacesArray = new Class[1];
        interfacesArray[0] = null;
        new ShardingProxyFactory().createProxy(targetsArray, interfacesArray);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoInterfaces() {
        Class[] interfaces = new Class[0];
        Object[] targets = new Object[2];
        targets[0] = new ClassForTests(0);
        targets[1] = new ClassForTests(1);
        new ShardingProxyFactory().createProxy(targets, interfaces);
    }

    @Test(expected = RuntimeException.class)
    public void testDoNotProxy() {
        proxy.numNotForProxy();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoTargets() {
        Class[] interfaces = new Class[1];
        interfaces[0] = InterfaceForTests.class;
        Object[] targets = new Object[0];
        new ShardingProxyFactory().createProxy(targets, interfaces);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectInterface() {
        Class[] interfaces = new Class[1];
        interfaces[0] = InterfaceWithoutMethods.class;
        Object[] targets = new Object[1];
        targets[0] = new ClassForTests(0);
        new ShardingProxyFactory().createProxy(targets, interfaces);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsNotInterface() {
        Class[] interfaces = new Class[1];
        interfaces[0] = ArrayList.class;
        Object[] targets = new Object[1];
        targets[0] = new ClassForTests(0);
        new ShardingProxyFactory().createProxy(targets, interfaces);
    }

    @Test(expected = IllegalStateException.class)
    public void testIncorrectAnnotations() {
        Class[] interfaces = new Class[1];
        interfaces[0] = InterfaceWithInvalidAnnotation.class;
        Object[] targets = new Object[1];
        targets[0] = new ClassForTests(0);
        new ShardingProxyFactory().createProxy(targets, interfaces);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectArguments() {
        Class[] interfaces = new Class[1];
        interfaces[0] = InterfaceWithoutArguments.class;
        Object[] targets = new Object[1];
        targets[0] = new ClassForTests(0);
        new ShardingProxyFactory().createProxy(targets, interfaces);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTargetWithoutInterfaces() {
        Class[] interfaces = new Class[1];
        interfaces[0] = InterfaceForTests.class;
        Object[] targets = new Object[1];
        targets[0] = new ClassWithoutInterfaces();
        new ShardingProxyFactory().createProxy(targets, interfaces);
    }

    @Test
    public void testGood() {
        Object[] targets = new Object[2];
        targets[0] = new ClassForTests(0);
        targets[1] = new ClassForTests(1);

        Class[] interfaces = new Class[1];
        interfaces[0] = InterfaceForTests.class;

        InterfaceForTests testInterface = (InterfaceForTests) new ShardingProxyFactory()
                .createProxy(targets, interfaces);

    }

    @Test
    public void testForPrivateNestedClass() {
        Object[] targets = new Object[2];
        targets[0] = new ClassForTests(0).newInstanceOfSubClass();
        targets[1] = new ClassForTests(1).newInstanceOfSubClass();

        Class[] interfaces = new Class[1];
        interfaces[0] = InterfaceForTests.class;

        InterfaceForTests testInterface = (InterfaceForTests) new ShardingProxyFactory()
                .createProxy(targets, interfaces);

    }
}