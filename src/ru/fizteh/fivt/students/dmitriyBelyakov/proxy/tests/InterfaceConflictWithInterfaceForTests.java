package ru.fizteh.fivt.students.dmitriyBelyakov.proxy.tests;

import ru.fizteh.fivt.proxy.Collect;
import ru.fizteh.fivt.proxy.DoNotProxy;

import java.util.List;

public interface InterfaceConflictWithInterfaceForTests {
    int numInt(int num);

    int numLongNotConflict(long num);

    @Collect
    int numCollectIntNotConflict();
}