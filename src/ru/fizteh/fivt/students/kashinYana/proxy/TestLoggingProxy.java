package ru.fizteh.fivt.students.kashinYana.proxy;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: yana
 * Date: 01.12.12
 * Time: 17:39
 * To change this template use File | Settings | File Templates.
 */


interface InterfaceToProxy {
    void method(int a) throws Exception;

    String get(String a, String b, int y);
}

class InterfaceToProxyImplementation
        implements InterfaceToProxy {
    @Override
    public void method(int a) throws Exception {
    }

    @Override
    public String get(String a, String b, int j) {
        return a;
    }
}


public class TestLoggingProxy {
    static public void main(String[] args) throws Exception {
        InterfaceToProxyImplementation target = new InterfaceToProxyImplementation();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        StringWriter writer = new StringWriter();
        InterfaceToProxy log = (InterfaceToProxy)
                factory.createProxy(target, writer, InterfaceToProxy.class);

        log.method(6);
        log.get("rtttr", "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 7666);
        System.out.println(writer.toString());
    }
}
