package ru.fizteh.fivt.students.fedyuninV.proxy;

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

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
interface VoidInterface{}

class VoidInterfaceClass implements VoidInterface{
    public void func() {}
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

public class ProxyTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void noExceptionTest() {
        List<String> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        List<String> proxy = (List<String>) factory.createProxy(list, builder, list.getClass().getInterfaces());
        proxy.add("wtf1");
        proxy.add("wtf2");
        proxy.add(2, "too long too long too long too long too long too long too long too long too long too long");
        proxy.get(0);
        proxy.toString();
        Assert.assertEquals(builder.toString(),
                "List.add(\"wtf1\") returned true\n" +
                        "List.add(\"wtf2\") returned true\n" +
                        "List.add(\n" +
                        "  2,\n" +
                        "  \"too long too long too long too long too long too long too long too long too long too long\"\n" +
                        "  )\n" +
                        "  is void\n" +
                        "List.get(0) returned \"wtf1\"\n");
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
        proxy.add(new User(1, UserType.USER, new UserName("Valeriy", "Fedyunin"), new Permissions()));
        Assert.assertTrue(builder.toString().matches("List.add\\(\\[ru.fizteh.fivt.bind.test.User@[a-z0-9]+\\]\\) returned true\\n"));
    }



    @Test
    public void arrayTest() {
        ArrayTest arrayTest = new ArrayTest();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        SetArrays proxy = (SetArrays) factory.createProxy(arrayTest, builder, SetArrays.class);
        proxy.setArray(new Double[]{3.1415, 1.02, 23.04});
        proxy.voidMethod(new Double[]{});
        proxy.toString(); //don't proxy
        proxy.hashCode(); //don't proxy
        proxy.equals(null); //don't proxy
        Assert.assertEquals(builder.toString(),
                "SetArrays.setArray(3{\"3.1415\", \"1.02\", \"23.04\"}) returned 3{\"3.1415\", \"1.02\", \"23.04\"}\n" +
                "SetArrays.voidMethod(0{}) is void\n");
    }

    @Test
    public void noTabExceptionTest() {
        List<String> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        List<String> proxy = (List<String>) factory.createProxy(list, builder, list.getClass().getInterfaces());
        proxy.add(3, "wtf1");
        String[] strings = builder.toString().split("\\n");
        for (int i = 1; i < strings.length; i++) {  //starts from i=1 because first string doesn't have space prefix
            Assert.assertTrue(strings[i].startsWith("  "));
        }
    }

    @Test
    public void tabExceptionTest() {
        List<String> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        List<String> proxy = (List<String>) factory.createProxy(list, builder, list.getClass().getInterfaces());
        proxy.add(3, "too long too long too long too long too long too long too long too long too long too long");
        String[] strings = builder.toString().split("\\n");
        String prefix = "  ";
        for (int i = 1; i < strings.length; i++) {
            Assert.assertTrue(strings[i].startsWith(prefix));
            if (strings[i].startsWith("  threw")) { //after "  threw  *** exception
                prefix = "    ";                    //we have 4 spaces in front of string
            }
        }
    }

    @Test
    public void screeningTest() {
        List<String> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        List<String> proxy = (List<String>) factory.createProxy(list, builder, list.getClass().getInterfaces());
        proxy.add("wtf1 \n wtf2\\\"");
        Assert.assertEquals(builder.toString(), "List.add(\"wtf1 \\n wtf2\\\\\\\"\") returned true\n"); // wtf2\\\"
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
    public void nullTargetTes() {
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
        proxy.indexOf((Object) new int[]{1, 2});
        Assert.assertEquals(builder.toString(), "List.indexOf(2{\"1\", \"2\"}) returned -1\n");
    }
}
