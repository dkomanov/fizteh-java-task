package ru.fizteh.fivt.students.nikitaAntonov.stringformatter;

import ru.fizteh.fivt.students.nikitaAntonov.stringformatter.StringFormatterFactory;

public class Testing {
    public static void main(String args[]) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        StringFormatterFactory factory = new StringFormatterFactory();
        ru.fizteh.fivt.format.StringFormatter formatter = factory.create();
        System.out.println(formatter.format("test {0.p1}, {0.p2}, {0.p3}, fuckthisshit!{   ", new TestingStaff(10, "abc", 12.4)));
    }
}

class TestingStaff {
    int p1;
    String p2;
    double p3;
    
    public TestingStaff (int a, String b, double c) {
        p1 = a;
        p2 = b;
        p3 = c;
    }
}