/*
 * ShardingProxyTest.java
 * Dec 7, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.proxy.test;

import java.util.*;
import ru.fizteh.fivt.students.harius.proxy.*;

/* A bunch of tests for ShardingProxy */
public class ShardingProxyTest {

    private static int iter = 31;

    private static int count = 17;

    private static Object[] targets = new Object[count];

    private static List<String> answer = new ArrayList<>();

    static {
        for (int i = 0; i < count; ++i) {
            String next = i + "!";
            targets[i] = new TestClass(next);
            for(int j = 0; j < iter; ++j) {
                answer.add(next);
            }
        }
    }

    private static Class[] interfaces
        = {TestInterface.class};

    private static TestInterface checker
        = new TestClass("?");

    /* Do all tests */
    public static void main(String[] args) {
        factoryIsCorrect();
        factoryFailsProperly();
        proxyIsCorrect();
        proxyFailsProperly();

        System.out.println("All tests OK");
    }

    public static void factoryIsCorrect() {
        System.out.println("Checking factory correctness");

        ShardingProxyFactory factory = new ShardingProxyFactory();
        factory.createProxy(targets, interfaces);

        System.out.println("Factory is correct");
    }

    public static void factoryFailsProperly() {
        System.out.println("Checking factory failures");

        Object[] badObj = {null};
        Class[] badClass = {null};
        Class[] empty = {EmptyInterface.class};
        Object[] none = {};
        Class[] none2 = {};

        ShardingProxyFactory factory = new ShardingProxyFactory();

        factoryCrash(factory, targets, null);
        factoryCrash(factory, null, interfaces);
        factoryCrash(factory, badObj, interfaces);
        factoryCrash(factory, targets, badClass);

        factoryCrash(factory, targets, empty);
        factoryCrash(factory, none, empty);
        factoryCrash(factory, targets, none2);

        System.out.println("Factory fails correctly");
    }

    public static void proxyIsCorrect() {
        System.out.println("Checking proxy correctness");

        ShardingProxyFactory factory = new ShardingProxyFactory();
        TestInterface proxy = (TestInterface)factory.createProxy(
            targets, interfaces);

        check(proxy.longFromInt(0), checker.longFromInt(0));
        check(proxy.intFromLong(0), checker.intFromLong(0));
        check(proxy.longFromStringCollect("proverko"),
            checker.longFromStringCollect("proverko") * count);

        check(proxy.intFromStringCollect("proverko"),
            checker.intFromStringCollect("proverko") * count);

        check(proxy.llongFromStringCollect("proverko"),
            checker.llongFromStringCollect("proverko") * count);

        check(proxy.iintFromStringCollect("proverko"),
            checker.iintFromStringCollect("proverko") * count);

        List<String> result
            = proxy.listFromIntegerCollect(iter);

        if (!result.equals(answer)) {
            String log = result + "\n" + answer;
            throw new RuntimeException("Wrong list collection:\n" + log);
        }

        System.out.println("Proxy is correct");
    }

    public static void proxyFailsProperly() {
        System.out.println("Checking proxy failures");

        ShardingProxyFactory factory = new ShardingProxyFactory();
        TestInterface proxy = (TestInterface)factory.createProxy(
            targets, interfaces);

        boolean crashed = false;
        try {
            proxy.voidFromNoneIgnore();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            crashed = true;
        } finally {
            if (!crashed) {
                throw new RuntimeException(
                    "Not crashed while calling do-not-proxy");
            }
        }

        System.out.println("Proxy fails correctly");
    }

    /* Assert equality */
    private static void check(long a, long b) {
        if (a != b) {
            throw new RuntimeException(
                "Assertion failed: " + a + "/=" + b);
        }
    }

    /* Ensure the factory crashes */
    private static void factoryCrash(
        ShardingProxyFactory factory, Object[] targets,
        Class[] interfaces) {

        boolean thrown = false;
        try {
            factory.createProxy(targets, interfaces);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            thrown = true;
        } finally {
            if (!thrown) {
                String log = Arrays.toString(targets) + "\n"
                    + Arrays.toString(interfaces);
                throw new RuntimeException("Not thrown when expected\n" + log);
            }
        }
    }
}