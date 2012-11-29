package ru.fizteh.fivt.students.nikitaAntonov.stringformatter;

import ru.fizteh.fivt.students.nikitaAntonov.stringformatter.StringFormatterFactory;

public class Testing {
    public static void main(String args[]) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        StringFormatterFactory factory = new StringFormatterFactory();
        ru.fizteh.fivt.format.StringFormatter formatter = factory.create();
        System.out.println(formatter.format(" }} {{{0}}}   }}blahblah"));
    }
}
