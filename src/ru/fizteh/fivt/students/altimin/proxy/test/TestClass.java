package ru.fizteh.fivt.students.altimin.proxy.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * User: altimin
 * Date: 12/13/12
 * Time: 4:50 AM
 */

public class TestClass implements Interface {
    private int v;

    public TestClass(int v) {
        this.v = v;
    }

    @Override
    public void throwException() throws IOException {
        throw new IOException();
    }

    @Override
    public Void collectVoidObject() {
        return null;
    }

    @Override
    public Long collectLongObject() {
        return new Long(v);
    }

    @Override
    public Integer collectIntObject() {
        return v;
    }

    @Override
    public Integer takeIntObject(Integer V) {
        return v;
    }

    @Override
    public Long takeLongObject(Long V) {
        return new Long(v);
    }

    @Override
    public ArrayList collectArrayList() {
        ArrayList list = new ArrayList();
        list.add(v);
        return list;
    }

    @Override
    public List collectList() {
        List list = new LinkedList();
        list.add(v);
        return list;
    }

    @Override
    public LinkedList collectLinkedList() {
        LinkedList list = new LinkedList();
        list.add(v);
        return list;
    }

    @Override
    public int takeInt(int V) {
        return v;
    }

    @Override
    public long takeLong(long V) {
        return v;
    }

    @Override
    public void methodNotForProxy() {
    }

    @Override
    public int collectInt() {
        return v;
    }

    @Override
    public long collectLong() {
        return v;
    }

    @Override
    public void collectVoid() {
    }
}
