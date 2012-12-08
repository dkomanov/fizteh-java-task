package ru.fizteh.fivt.students.khusaenovTimur.proxy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Timur
 * Date: 12/8/12
 * Time: 2:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestPrint {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        List<String> proxy = (List<String>) factory.createProxy(list, builder, list.getClass().getInterfaces());
        proxy.add("wtf1\\\\ \n wtf2\\\"");
        System.out.println(builder.toString());
    }
}
