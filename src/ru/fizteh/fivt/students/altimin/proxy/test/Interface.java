package ru.fizteh.fivt.students.altimin.proxy.test;

import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;

import java.io.IOException;
import java.util.*;

/**
 * User: altimin
 * Date: 12/13/12
 * Time: 4:49 AM
 */

public interface Interface {
    int takeInt(int v);


    long takeLong(long v);

    Integer takeIntObject(Integer v);

    Long takeLongObject(Long v);

    @DoNotProxy
    void methodNotForProxy();

    @Collect
    int collectInt();

    @Collect
    long collectLong();

    @Collect
    void collectVoid();

    @Collect
    Integer collectIntObject();

    @Collect
    Long collectLongObject();

    @Collect
    Void collectVoidObject();

    @Collect
    List collectList();

    @Collect
    List collectArrayList();

    @Collect
    List collectLinkedList();

    @Collect
    void throwException() throws IOException;
}