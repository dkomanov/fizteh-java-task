package ru.fizteh.fivt.students.dmitriyBelyakov.proxy.tests;

import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;

import java.util.List;

public interface InterfaceForTests {
    int numInt(int num);

    int numLong(long num);

    @DoNotProxy
    void numNotForProxy();

    @Collect
    int numCollectInt();

    @Collect
    void numCollectVoid();

    @Collect
    long numCollectLong();

    @Collect
    List numCollectList();

    Integer numInteger(int num);

    @Collect
    Integer numCollectInteger();

    @Collect
    Long numCollectLongObject();
}