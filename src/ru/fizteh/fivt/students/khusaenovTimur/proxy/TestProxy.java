package ru.fizteh.fivt.students.khusaenovTimur.proxy;

import junit.framework.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import ru.fizteh.fivt.bind.test.Permissions;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;

import java.util.ArrayList;
import java.util.List;

public class TestProxy {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void noExceptionTest() {
        List<String> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        List<String> proxy = (List<String>) factory.createProxy(list, builder, list.getClass().getInterfaces());
        proxy.add("Object1");
        proxy.add("Object2");
        proxy.get(1);
        proxy.add(2, "a very long result of toString() method of some object with huge amount of data");
        proxy.add("5 march 1993 Khusaenov Timur Ramilevich. 1234567890");
        proxy.add("and another a very long result of toString() method of some object with huge amount of data");
        proxy.toString();
        Assert.assertEquals(builder.toString(),
                "List.add(\"Object1\") returned true\n" +
                        "List.add(\"Object2\") returned true\n" +
                        "List.get(1) returned \"Object2\"\n" +
                        "List.add(\n" +
                        "  2,\n" +
                        "  \"a very long result of toString() method of some object with huge amount of data\"\n" +
                        "  )\n" +
                        "List.add(\"5 march 1993 Khusaenov Timur Ramilevich. 1234567890\") returned true\n" +
                        "List.add(\n" +
                        "  \"and another a very long result of toString() method of some object with huge amount of data\"\n" +
                        "  )\n" +
                        "  returned true\n");
    }

    @Test
    public void testObject() {
        List<Object> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        List<Object> proxy = (List<Object>) factory.createProxy(list, builder, list.getClass().getInterfaces());
        proxy.add(new Object());
        Assert.assertTrue(builder.toString().matches("List.add\\(\\[java.lang.Object@[a-z0-9]+\\]\\) returned true\\n"));
    }

    @Test
    public void testNonStandardObject() {
        List<User> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        List<User> proxy = (List<User>) factory.createProxy(list, builder, list.getClass().getInterfaces());
        proxy.add(new User(1, UserType.USER, new UserName("Timur", "Khusaenov"), new Permissions()));
        Assert.assertTrue(builder.toString().matches("List.add\\(\\[ru.fizteh.fivt.bind.test.User@[a-z0-9]+\\]\\) returned true\\n"));
    }



    @Test
    public void arrayTest() {
        ArrayTest arrayTest = new ArrayTest();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        SetArrays proxy = (SetArrays) factory.createProxy(arrayTest, builder, SetArrays.class);
        proxy.setArray(new Double[]{7.64546, 13.5, 56.85, 9.36});
        proxy.voidMethod(new Double[]{});
        Assert.assertEquals(builder.toString(),
                "SetArrays.setArray(4{7.64546, 13.5, 56.85, 9.36}) returned 4{7.64546, 13.5, 56.85, 9.36}\n" +
                "SetArrays.voidMethod(0{})\n");
    }


    @Test
    public void screeningTest() {
        List<String> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        List<String> proxy = (List<String>) factory.createProxy(list, builder, list.getClass().getInterfaces());
        proxy.add("tim\\\\ \n \\\\ \\\\\"");
        Assert.assertEquals(builder.toString(), "List.add(\"tim\\\\\\\\ \\n \\\\\\\\ \\\\\\\\\\\"\") returned true\n");
    }


    @Test
    public void voidArrayTest() {
        ArrayTest arrayTest = new ArrayTest();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        SetArrays proxy = (SetArrays) factory.createProxy(arrayTest, builder, SetArrays.class);
        proxy.setArray(new Double[]{});
        Assert.assertEquals(builder.toString(),
                "SetArrays.setArray(0{}) returned 0{}\n");
    }

    @Test
    public void infiniteArrayTest() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("Object contains link to itself");
        ObjectArrayTest arrayTest = new ObjectArrayTest();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        SetObjectArrays proxy = (SetObjectArrays) factory.createProxy(arrayTest, builder, SetObjectArrays.class);
        Object[] objects = new Object[1];
        objects[0] = objects;
        proxy.setArray(objects);
    }

    @Test
    public void voidInterfaceTest() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("No methods in interfaces");
        VoidInterfaceClass obj = new VoidInterfaceClass();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        VoidInterface proxy = (VoidInterface) factory.createProxy(obj, builder, VoidInterface.class);
    }

    @Test
    public void nullInterfaceTest() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Null parameter found");
        VoidInterfaceClass obj = new VoidInterfaceClass();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        VoidInterface proxy = (VoidInterface) factory.createProxy(obj, builder, VoidInterface.class, null);
    }

    @Test
    public void nullInterfacesTest() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Null parameter found");
        VoidInterfaceClass obj = new VoidInterfaceClass();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        VoidInterface proxy = (VoidInterface) factory.createProxy(obj, builder);
    }

    @Test
    public void nullBuilderTest() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Null parameter found");
        VoidInterfaceClass obj = new VoidInterfaceClass();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        VoidInterface proxy = (VoidInterface) factory.createProxy(obj, null, VoidInterface.class);
    }

    @Test
    public void nullTargetTest() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Null parameter found");
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        VoidInterface proxy = (VoidInterface) factory.createProxy(null, builder, VoidInterface.class);
    }

    @Test
    public void indexOfTest() {
        List<Object> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        List<Object> proxy = (List<Object>) factory.createProxy(list, builder, list.getClass().getInterfaces());
        Integer x = 3;
        proxy.indexOf(new Integer[]{x, x});
        proxy.indexOf(new int[]{1, 2});
        Assert.assertEquals(builder.toString(), "List.indexOf(2{3, 3}) returned -1\n" +
                "List.indexOf(2{1, 2}) returned -1\n");
    }

    @Test
    public void badInterfaceTest() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("target doesn't support interface");
        ArrayTest arrayTest = new ArrayTest();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        ExternalInterface proxy = (ExternalInterface) factory.createProxy(arrayTest, builder, ExternalInterface.class);
    }

    @Test
    public void innerInterfaceTest() {
        ExternalInterface externalInterface = new ExternalInterface();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        InnerInterface proxy = (InnerInterface) factory.createProxy(externalInterface, builder, InnerInterface.class);
        proxy.go();
        Assert.assertEquals(builder.toString(), "InnerInterface.go()\n");
    }

    @Test
    public void exceptionTypeTest() {
        List<String> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        List<String> proxy = (List<String>) factory.createProxy(list, builder, list.getClass().getInterfaces());
        try {
            proxy.add(3, "wtf");
        } catch (Exception ignored) {
        }
        Assert.assertTrue(builder.toString().startsWith("List.add(3, \"wtf\") threw java.lang.IndexOutOfBoundsException: Index: 3, Size: 0\n" +
                "  java.util.ArrayList.rangeCheckForAdd(ArrayList.java:612)\n" +
                "  java.util.ArrayList.add(ArrayList.java:426)\n"));
    }

}