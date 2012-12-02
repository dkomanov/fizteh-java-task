package ru.fizteh.fivt.students.altimin.xmlbinder.test;

import ru.fizteh.fivt.bind.AsXmlAttribute;

/**
 * User: altimin
 * Date: 12/1/12
 * Time: 4:17 PM
 */
public class A {
    @AsXmlAttribute
    int a = 1;
    @AsXmlAttribute
    int c = 2;

    public A(int a, int c) {
        this.a = a;
        this.c = c;
    }

    @Override
    public boolean equals(Object o) {
        if (!o.getClass().equals(this.getClass())) {
            return false;
        }
        A aa = (A) o;
        return this.a == aa.a && this.c == aa.c;
    }
    //B b = new B();
    //public static class B {
    //    int b = 2;
    //    double c = 3.5;
    //}
}
