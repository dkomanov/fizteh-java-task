package ru.fizteh.fivt.students.dmitriyBelyakov.proxy;

import ru.fizteh.fivt.proxy.Collect;

import java.util.Map;

public interface InterfaceForTestsWithIncorrectAnnotations {
    @Collect
    Map returnMap();
}