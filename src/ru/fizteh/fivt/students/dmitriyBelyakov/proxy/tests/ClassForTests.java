package ru.fizteh.fivt.students.dmitriyBelyakov.proxy.tests;

import java.util.ArrayList;
import java.util.List;

public class ClassForTests implements InterfaceForTests {
    public int number;

    public ClassForTests(int num) {
        number = num;
    }

    @Override
    public int numInt(int num) {
        return number;
    }

    @Override
    public int numLong(long num) {
        return number;
    }

    @Override
    public void numNotForProxy() {
        return;
    }

    @Override
    public int numCollectInt() {
        return number;
    }

    @Override
    public void numCollectVoid() {
        return;
    }

    @Override
    public long numCollectLong() {
        return (long) number;
    }

    @Override
    public List numCollectList() {
        List returnList = new ArrayList<Integer>();
        returnList.add(number);
        returnList.add(number + 1);
        returnList.add(number + 2);
        return returnList;
    }
}