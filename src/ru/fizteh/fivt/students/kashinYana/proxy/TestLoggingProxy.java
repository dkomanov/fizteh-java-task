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
    static public void main(String[] args) throws Exception {
        InterfaceToProxyImplementation target = new InterfaceToProxyImplementation();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        StringWriter writer = new StringWriter();
        InterfaceToProxy log = (InterfaceToProxy)
                factory.createProxy(target, writer, InterfaceToProxy.class);

        String veryLongString = "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
        log.exception("somethink");
        log.exception(veryLongString);

        log.get("one", "two", 6);
        log.get(veryLongString, "two", 6);

        log.get2(5);
        log.getNull(77);
        log.getDouble(0.5, 0.6, 0.7);
        log.getEnum(Season.WINTER);
        Object num[] = {"dfg", 20, 45, 82, 25, 63};
        log.getArray(num);
        Object all[] = {num, 3, Season.SPRING, "hihihhi"};
        log.getArray(all);
        Object allWithLongString[] = {veryLongString, num, 3, Season.SPRING, "hihihhi"};
        log.getArray(allWithLongString);

        System.out.println(writer.toString());
    }
}
