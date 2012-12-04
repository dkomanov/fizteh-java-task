package ru.fizteh.fivt.students.fedyuninV.proxy;

import junit.framework.Assert;
import org.junit.Test;
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

interface SetArrays {
    public Double[] setArray(Double[] newArray);
}

class ArrayTest implements SetArrays{
    @Override
    public Double[] setArray(Double[] array) {
        return array;
    }
}


public class ProxyTest {

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
        Assert.assertEquals(builder.toString(),
                "add(\"wtf1\") returned true\n" +
                        "add(\"wtf2\") returned true\n" +
                        "add(\n" +
                        "  2,\n" +
                        "  \"too long too long too long too long too long too long too long too long too long too long\"\n" +
                        "  )\n" +
                        "  returned null\n" +
                        "get(0) returned \"wtf1\"\n");
    }

    @Test
    public void noTabExceptionTest() {
        List<String> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        List<String> proxy = (List<String>) factory.createProxy(list, builder, list.getClass().getInterfaces());
        proxy.add(3, "wow");
        Assert.assertEquals(builder.toString(),
                "add(3, \"wow\") threw java.lang.reflect.InvocationTargetException: null\n" +
                        "  sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
                        "  sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)\n" +
                        "  sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n" +
                        "  java.lang.reflect.Method.invoke(Method.java:601)\n" +
                        "  ru.fizteh.fivt.students.fedyuninV.proxy.InvocationHandler.invoke(InvocationHandler.java:126)\n" +
                        "  $Proxy4.add(Unknown Source)\n" +
                        "  ru.fizteh.fivt.students.fedyuninV.proxy.ProxyTest.noTabExceptionTest(ProxyTest.java:59)\n" +
                        "  sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
                        "  sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)\n" +
                        "  sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n" +
                        "  java.lang.reflect.Method.invoke(Method.java:601)\n" +
                        "  org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:45)\n" +
                        "  org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)\n" +
                        "  org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:42)\n" +
                        "  org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)\n" +
                        "  org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:263)\n" +
                        "  org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:68)\n" +
                        "  org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:47)\n" +
                        "  org.junit.runners.ParentRunner$3.run(ParentRunner.java:231)\n" +
                        "  org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:60)\n" +
                        "  org.junit.runners.ParentRunner.runChildren(ParentRunner.java:229)\n" +
                        "  org.junit.runners.ParentRunner.access$000(ParentRunner.java:50)\n" +
                        "  org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:222)\n" +
                        "  org.junit.runners.ParentRunner.run(ParentRunner.java:300)\n" +
                        "  org.junit.runner.JUnitCore.run(JUnitCore.java:157)\n" +
                        "  com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:76)\n" +
                        "  com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:195)\n" +
                        "  com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:63)\n" +
                        "  sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
                        "  sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)\n" +
                        "  sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n" +
                        "  java.lang.reflect.Method.invoke(Method.java:601)\n" +
                        "  com.intellij.rt.execution.application.AppMain.main(AppMain.java:120)\n");
    }

    @Test
    public void tabException() {
        List<String> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        List<String> proxy = (List<String>) factory.createProxy(list, builder, list.getClass().getInterfaces());
        proxy.add(2, "too long too long too long too long too long too long too long too long too long too long");
        Assert.assertEquals(builder.toString(),
                "add(\n" +
                        "  2,\n" +
                        "  \"too long too long too long too long too long too long too long too long too long too long\"\n" +
                        "  )\n" +
                        "  threw java.lang.reflect.InvocationTargetException: null\n" +
                        "    sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
                        "    sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)\n" +
                        "    sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n" +
                        "    java.lang.reflect.Method.invoke(Method.java:601)\n" +
                        "    ru.fizteh.fivt.students.fedyuninV.proxy.InvocationHandler.invoke(InvocationHandler.java:126)\n" +
                        "    $Proxy4.add(Unknown Source)\n" +
                        "    ru.fizteh.fivt.students.fedyuninV.proxy.ProxyTest.tabException(ProxyTest.java:103)\n" +
                        "    sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
                        "    sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)\n" +
                        "    sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n" +
                        "    java.lang.reflect.Method.invoke(Method.java:601)\n" +
                        "    org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:45)\n" +
                        "    org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)\n" +
                        "    org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:42)\n" +
                        "    org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)\n" +
                        "    org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:263)\n" +
                        "    org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:68)\n" +
                        "    org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:47)\n" +
                        "    org.junit.runners.ParentRunner$3.run(ParentRunner.java:231)\n" +
                        "    org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:60)\n" +
                        "    org.junit.runners.ParentRunner.runChildren(ParentRunner.java:229)\n" +
                        "    org.junit.runners.ParentRunner.access$000(ParentRunner.java:50)\n" +
                        "    org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:222)\n" +
                        "    org.junit.runners.ParentRunner.run(ParentRunner.java:300)\n" +
                        "    org.junit.runner.JUnitCore.run(JUnitCore.java:157)\n" +
                        "    com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:76)\n" +
                        "    com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:195)\n" +
                        "    com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:63)\n" +
                        "    sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n" +
                        "    sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)\n" +
                        "    sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n" +
                        "    java.lang.reflect.Method.invoke(Method.java:601)\n" +
                        "    com.intellij.rt.execution.application.AppMain.main(AppMain.java:120)\n");
    }

    @Test
    public void testObject() {
        List<Object> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        List<Object> proxy = (List<Object>) factory.createProxy(list, builder, list.getClass().getInterfaces());
        proxy.add(new Object());
        Assert.assertTrue(builder.toString().matches("add\\(\\[java.lang.Object@[a-z0-9]+\\]\\) returned true\\n"));
    }

    @Test
    public void testNonStandardObject() {
        List<User> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        List<User> proxy = (List<User>) factory.createProxy(list, builder, list.getClass().getInterfaces());
        proxy.add(new User(1, UserType.USER, new UserName("Valeriy", "Fedyunin"), new Permissions()));
        Assert.assertTrue(builder.toString().matches("add\\(\\[ru.fizteh.fivt.bind.test.User@[a-z0-9]+\\]\\) returned true\\n"));
    }



    @Test
    public void arrayTest() {
        ArrayTest arrayTest = new ArrayTest();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        SetArrays proxy = (SetArrays) factory.createProxy(arrayTest, builder, SetArrays.class);
        proxy.setArray(new Double[]{3.1415, 1.02, 23.04});
        Assert.assertEquals(builder.toString(),
                "setArray(3{\"3.1415\", \"1.02\", \"23.04\"}) returned 3{\"3.1415\", \"1.02\", \"23.04\"}\n");
    }
}
