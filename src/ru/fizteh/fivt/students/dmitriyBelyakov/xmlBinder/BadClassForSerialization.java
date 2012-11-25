package ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder;

public class BadClassForSerialization {
    private BadClassForSerialization link;

    BadClassForSerialization() {
        link = this;
    }
}