package ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder.test;

public class BadClassForSerialization {
    private BadClassForSerialization link;

    public BadClassForSerialization() {
        link = this;
    }
}