package ru.fizteh.fivt.students.yuliaNikonova.common;

import java.util.List;

import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;

public interface InterfaceTest {

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
    List<?> numCollectList();

}
