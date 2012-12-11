package ru.fizteh.fivt.students.dmitriyBelyakov.proxy.tests;

import ru.fizteh.fivt.proxy.Collect;

import java.util.Map;

public interface InterfaceForTestsWithIncorrectAnnotations {
    @Collect
    Map returnMap();
}