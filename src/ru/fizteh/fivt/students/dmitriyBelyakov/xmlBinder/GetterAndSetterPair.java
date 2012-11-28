package ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder;

import java.lang.reflect.Method;

public class GetterAndSetterPair {
    public String name;
    public Method getter;
    public Method setter;
    public Class type;
    public boolean asXmlCdata;
}