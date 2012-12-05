package ru.fizteh.fivt.students.kashinYana.proxy;

import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: yana
 * Date: 01.12.12
 * Time: 17:39
 * To change this template use File | Settings | File Templates.
 */


interface InterfaceToProxy {
    void exception(String a) throws Exception;

    String get(String a, String b, int y);

    int get2(int a);

    Integer getNull(int h);

    double getDouble(double as, double j, double k);

    void getEnum(Enum a);

    int getArray(Object[] list);
}

class InterfaceToProxyImplementation
        implements InterfaceToProxy {
    @Override
    public void exception(String a) throws Exception {
        ArrayList temp = null;
        temp.size();
    }

    @Override
    public String get(String a, String b, int j) {
        return a;
    }

    @Override
    public int get2(int j) {
        return j;
    }

    @Override
    public Integer getNull(int j) {
        Integer y = null;
        return y;
    }

    @Override
    public double getDouble(double j, double h, double k) {
        return j;
    }

    @Override
    public void getEnum(Enum k) {
        return;
    }

    @Override
    public int getArray(Object[] list) {
        return 100;
    }
}

enum Season {WINTER, SPRING, SUMMER, AUTUMN};

public class TestLoggingProxy {

    static InterfaceToProxyImplementation target = new InterfaceToProxyImplementation();
    static LoggingProxyFactory factory = new LoggingProxyFactory();
    static String veryLongString = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";

    static public void main(String[] args) throws Exception {

        test1();
        test2();
        test3();
    }

    static void test1() {
        StringWriter writer = new StringWriter();
        InterfaceToProxy log = (InterfaceToProxy)
                factory.createProxy(target, writer, InterfaceToProxy.class);
        log.get("one", "two", 6);
        test(writer.toString(), "InterfaceToProxy.get(\"one\", \"two\", 6) returned \"one\"\n");
    }

    static void test2() {
        StringWriter writer = new StringWriter();
        InterfaceToProxy log = (InterfaceToProxy)
                factory.createProxy(target, writer, InterfaceToProxy.class);
        log.get(veryLongString, "two", 6);
        test(writer.toString(), "InterfaceToProxy.get(\n" +
                "  \"ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff\",\n" +
                "  \"two\",\n" +
                "  6\n" +
                "  )\n" +
                "  returned \"ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff\"\n");

    }

    static void test3() {
        StringWriter writer = new StringWriter();
        InterfaceToProxy log = (InterfaceToProxy)
                factory.createProxy(target, writer, InterfaceToProxy.class);

        log.get2(5);
        log.getNull(77);
        log.getDouble(0.5, 0.6, 0.7);
        log.getEnum(Season.WINTER);
        Object num[] = {"dfg", 20, 45, 82, 25, 63};
        log.getArray(num);
        Object all[] = {num, 3, Season.SPRING, "hihihhi"};
        log.getArray(all);
        Object allWithLongString[] = {veryLongString, num, 3, Season.SPRING, "\f\'\"\b\rhihihhi\n\t"};
        log.getArray(allWithLongString);
        test(writer.toString(), "InterfaceToProxy.get2(5) returned 5\n" +
                "InterfaceToProxy.getNull(77) returned null\n" +
                "InterfaceToProxy.getDouble(0.5, 0.6, 0.7) returned 0.5\n" +
                "InterfaceToProxy.getEnum(WINTER)\n" +
                "InterfaceToProxy.getArray(6{\"dfg\", 20, 45, 82, 25, 63}) returned 100\n" +
                "InterfaceToProxy.getArray(4{6{\"dfg\", 20, 45, 82, 25, 63}, 3, SPRING, \"hihihhi\"}) returned 100\n" +
                "InterfaceToProxy.getArray(\n" +
                "  5{\"ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff\"," +
                " 6{\"dfg\", 20, 45, 82, 25, 63}, 3, SPRING, \"\\f\\'\\\"\\b\\rhihihhi\\n\\t\"}\n" +
                "  )\n" +
                "  returned 100\n");
    }

    static void test(String answerProgram, String answerCorrent) {
        if (!answerCorrent.equals(answerProgram)) {
            System.err.println("Don't correct. Correct:\n" + answerCorrent + "\n Your:\n" + answerProgram);
            System.exit(1);
        }
    }
}
