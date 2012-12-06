package ru.fizteh.fivt.students.dmitriyBelyakov.proxy.proxy.test;

import ru.fizteh.fivt.proxy.Collect;

import java.util.Map;

public interface InterfaceForTestsWithIncorrectAnnotations {
    @Collect
    Map returnMap();
}