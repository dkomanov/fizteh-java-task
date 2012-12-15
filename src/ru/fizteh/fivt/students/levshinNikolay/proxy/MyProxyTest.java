package ru.fizteh.fivt.students.levshinNikolay.proxy;

/**
 * Levshin Nikolay
 * MIPT FIVT 196
 */

import junit.framework.Assert;
import org.junit.*;
import org.junit.rules.ExpectedException;
import ru.fizteh.fivt.bind.test.*;
import java.util.*;

interface VoidInterface{}

class VoidInterfaceClass implements VoidInterface {
    public void func() {}
}

interface InnerInterface {
    public void go();
}

class ExternalInterface implements InnerInterface {
    @Override
    public void go() {

    }
}
interface SetArrays {
    public Double[] setArray(Double[] newArray);

    public void voidMethod(Double[] newArray);
}

class ArrayTest implements SetArrays{
    @Override
    public Double[] setArray(Double[] array) {
        return array;
    }

    @Override
    public void voidMethod(Double[] newArray) {}
}

interface SetObjectArrays {
    public Object[] setArray(Object[] newArray);
}

class ObjectArrayTest implements SetObjectArrays{
    @Override
    public Object[] setArray(Object[] array) {
        return array;
    }
}

public class MyProxyTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void noExceptionTest() {
        List<String> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        MyLoggingProxyFactory factory = new MyLoggingProxyFactory();
        List<String> proxy = (List<String>) factory.createProxy(list, builder, list.getClass().getInterfaces());
        proxy.add("wtf1");
        proxy.add("wtf2");
        proxy.get(0);
        proxy.add(2, "too long too long too long too long too long too long too long too long too long too long");
        proxy.add("0123456789012345678901234567890123456789012345678901234567");
        proxy.add("too long too long too long too long too long too long too long too long too long too long");
        proxy.toString();
        Assert.assertEquals(builder.toString(),
                "List.add(\"wtf1\") returned true\n" + "List.add(\"wtf2\") returned true\n" + "List.get(0) returned \"wtf1\"\n" +
                        "List.add(\n" + "  2,\n" + "  \"too long too long too long too long too long too long too long too long too long too long\"\n" +
                        "  )\n" + "List.add(\"0123456789012345678901234567890123456789012345678901234567\") returned true\n" + "List.add(\n" +
                        "  \"too long too long too long too long too long too long too long too long too long too long\"\n" +
                        "  )\n" +  "  returned true\n");
    }

    @Test
    public void testObject() {
        List<Object> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        MyLoggingProxyFactory factory = new MyLoggingProxyFactory();
        List<Object> proxy = (List<Object>) factory.createProxy(list, builder, list.getClass().getInterfaces());
        proxy.add(new Object());
        Assert.assertTrue(builder.toString().matches("List.add\\(\\[java.lang.Object@[a-z0-9]+\\]\\) returned true\\n"));
    }

    @Test
    public void testNonStandardObject() {
        List<User> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        MyLoggingProxyFactory factory = new MyLoggingProxyFactory();
        List<User> proxy = (List<User>) factory.createProxy(list, builder, list.getClass().getInterfaces());
        proxy.add(new User(1, UserType.USER, new UserName("Valeriy", "Fedyunin"), new Permissions()));
        Assert.assertTrue(builder.toString().matches("List.add\\(\\[ru.fizteh.fivt.bind.test.User@[a-z0-9]+\\]\\) returned true\\n"));
    }



    @Test
    public void arrayTest() {
        ArrayTest arrayTest = new ArrayTest();
        StringBuilder builder = new StringBuilder();
        MyLoggingProxyFactory factory = new MyLoggingProxyFactory();
        SetArrays proxy = (SetArrays) factory.createProxy(arrayTest, builder, SetArrays.class);
        proxy.setArray(new Double[]{3.1415, 1.02, 23.04});
        proxy.voidMethod(new Double[]{});
        proxy.toString();
        proxy.hashCode();
        proxy.equals(null);
        Assert.assertEquals(builder.toString(),
                "SetArrays.setArray(3{3.1415, 1.02, 23.04}) returned 3{3.1415, 1.02, 23.04}\n" +
                        "SetArrays.voidMethod(0{})\n");
    }

    @Test
    public void noTabExceptionTest() {
        List<String> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        MyLoggingProxyFactory factory = new MyLoggingProxyFactory();
        List<String> proxy = (List<String>) factory.createProxy(list, builder, list.getClass().getInterfaces());
        try {
            proxy.add(3, "wtf1");
        } catch (Throwable ex) {
        }
        String[] strings = builder.toString().split("\\n");
        for (int i = 1; i < strings.length; i++) {
            Assert.assertTrue(strings[i].startsWith("  "));
        }
    }

    @Test
    public void tabExceptionTest() {
        List<String> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        MyLoggingProxyFactory factory = new MyLoggingProxyFactory();
        List<String> proxy = (List<String>) factory.createProxy(list, builder, list.getClass().getInterfaces());
        try {
            proxy.add(3, "too long too long too long too long too long too long too long too long too long too long");
        } catch (Exception ex) {
            Assert.assertEquals(ex.getClass(), IndexOutOfBoundsException.class);
        }
        String[] strings = builder.toString().split("\\n");
        String prefix = "  ";
        for (int i = 1; i < strings.length; i++) {
            Assert.assertTrue(strings[i].startsWith(prefix));
            if (strings[i].startsWith("  threw")) {
                prefix = "    ";
            }
        }
        Assert.assertEquals(builder.toString().charAt(builder.toString().length() - 1), '\n');
    }

    @Test
    public void screeningTest() {
        List<String> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        MyLoggingProxyFactory factory = new MyLoggingProxyFactory();
        List<String> proxy = (List<String>) factory.createProxy(list, builder, list.getClass().getInterfaces());
        proxy.add("wtf1\\\\ \n wtf2\\\"");
        Assert.assertEquals(builder.toString(), "List.add(\"wtf1\\\\\\\\ \\n wtf2\\\\\\\"\") returned true\n");
    }


    @Test
    public void voidArrayTest() {
        ArrayTest arrayTest = new ArrayTest();
        StringBuilder builder = new StringBuilder();
        MyLoggingProxyFactory factory = new MyLoggingProxyFactory();
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
        MyLoggingProxyFactory factory = new MyLoggingProxyFactory();
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
        MyLoggingProxyFactory factory = new MyLoggingProxyFactory();
        VoidInterface proxy = (VoidInterface) factory.createProxy(obj, builder, VoidInterface.class);
    }

    @Test
    public void nullInterfaceTest() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Null parameter found");
        VoidInterfaceClass obj = new VoidInterfaceClass();
        StringBuilder builder = new StringBuilder();
        MyLoggingProxyFactory factory = new MyLoggingProxyFactory();
        VoidInterface proxy = (VoidInterface) factory.createProxy(obj, builder, VoidInterface.class, null);
    }

    @Test
    public void nullInterfacesTest() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Null parameter found");
        VoidInterfaceClass obj = new VoidInterfaceClass();
        StringBuilder builder = new StringBuilder();
        MyLoggingProxyFactory factory = new MyLoggingProxyFactory();
        VoidInterface proxy = (VoidInterface) factory.createProxy(obj, builder);
    }

    @Test
    public void nullBuilderTest() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Null parameter found");
        VoidInterfaceClass obj = new VoidInterfaceClass();
        MyLoggingProxyFactory factory = new MyLoggingProxyFactory();
        VoidInterface proxy = (VoidInterface) factory.createProxy(obj, null, VoidInterface.class);
    }

    @Test
    public void nullTargetTes() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Null parameter found");
        StringBuilder builder = new StringBuilder();
        MyLoggingProxyFactory factory = new MyLoggingProxyFactory();
        VoidInterface proxy = (VoidInterface) factory.createProxy(null, builder, VoidInterface.class);
    }

    @Test
    public void indexOfTest() {
        List<Object> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        MyLoggingProxyFactory factory = new MyLoggingProxyFactory();
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
        MyLoggingProxyFactory factory = new MyLoggingProxyFactory();
        ExternalInterface proxy = (ExternalInterface) factory.createProxy(arrayTest, builder, ExternalInterface.class);
    }

    @Test
    public void innerInterfaceTest() {
        ExternalInterface externalInterface = new ExternalInterface();
        StringBuilder builder = new StringBuilder();
        MyLoggingProxyFactory factory = new MyLoggingProxyFactory();
        InnerInterface proxy = (InnerInterface) factory.createProxy(externalInterface, builder, InnerInterface.class);
        proxy.go();
        Assert.assertEquals(builder.toString(), "InnerInterface.go()\n");
    }

    @Test
    public void exceptionTypeTest() {
        List<String> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        MyLoggingProxyFactory factory = new MyLoggingProxyFactory();
        List<String> proxy = (List<String>) factory.createProxy(list, builder, list.getClass().getInterfaces());
        try {
            proxy.add(3, "wtf");
        } catch (Exception ignored) {
        }
        Assert.assertTrue(builder.toString().startsWith("List.add(3, \"wtf\") threw java.lang.IndexOutOfBoundsException: Index: 3, Size: 0\n" +
                "  at java.util.ArrayList.rangeCheckForAdd(ArrayList.java:612)\n" + "  at java.util.ArrayList.add(ArrayList.java:426)\n"));
    }

}