package ru.fizteh.fivt.students.kashinYana.stringFormator;

import ru.fizteh.fivt.format.FormatterException;

import java.util.GregorianCalendar;
import java.util.HashMap;

public class StringFormatterTest {

    private static StringFormatterFactory factory = new StringFormatterFactory();

    public static void main(String[] args) throws Exception {
        try {
            testSuperClass();
            testWithString();
            testWithInteger();
            testWithDate();
            testWrong();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    static void testSuperClass() throws Exception {
        class A {
            public Integer a;
        }
        class B extends A {
            private int b;

            B(int aa, int bb) {
                b = bb;
                a = aa;
            }
        }
        class C {
            A a;
            private A a2;

            C(int n) {
                a = new A();
                a2 = new A();
                a.a = n;
                a2.a = n * 2;
            }
        }
        class CWithNull {
            private A a;

            CWithNull(int n) {
                a = null;
            }
        }
        B testB = new B(1, 2);
        StringFormatter basic = factory.create();
        check("1 2 ", basic.format("{0.a} {0.b} {0.c}", testB));
        C testC = new C(33);
        check("33 66", basic.format("{0.a.a} {0.a2.a}", testC));
        CWithNull cWithNull = new CWithNull(20);
        check("", basic.format("{0.a.a}", cWithNull));
        CWithNull cWithNull2 = null;
        check("", basic.format("{0.a.a}", cWithNull2));
    }

    static void testWithString() throws Exception {
        StringFormatter basic = factory.create();
        check("simple {papa, {  sister, } mama", basic.format("simple {{{0}, {{  {2}, }} {1}", "papa", "mama", "sister"));
        check("little papa { papa", basic.format("little {0} {{ {0}", "papa"));
        check("}} little {{", basic.format("}}}} little {{{{", "papa"));
        check("}} little {{papa}}", basic.format("}}}} little {{{{{0}}}}}", "papa"));
        check("}} little ", basic.format("}}}} little {0.int}", "papa"));
        class Pair {
            int x, y;

            Pair(int xx, int yy) {
                x = xx;
                y = yy;
            }
        }
        Pair pair = new Pair(3, 4);
        check("Pair 3 4 ", basic.format("Pair {0.x} {0.y} {0.z}", pair));
    }

    static void testWithInteger() throws Exception {
        StringFormatter basic = factory.create(StringFormatterDateExtension.class.getName(),
                StringFormatterIntegerExtension.class.getName());
        check("Int = 12", basic.format("Int = {0}", 12));
        check("Int = 0012 00010", basic.format("Int = {0:04d} {1:05d}", 12, 10));
        check("Int = {   7}", basic.format("Int = {{{0:4d}}}", 7));
        check("Int = {10}", basic.format("Int = {{{0:o}}}", 8));
        Integer numberNull = null;
        check("Int null = ", basic.format("Int null = {0:o}", numberNull));
    }

    static void testWithDate() throws Exception {
        StringFormatter basic = factory.create(StringFormatterDateExtension.class.getName(),
                StringFormatterIntegerExtension.class.getName());
        check("Date = 2009.11.01", basic.format("Date = {0:yyyy.MM.dd}", new GregorianCalendar(2009, 10, 1).getTime()));
        check("Date = 2009-11-01", basic.format("Date = {0:yyyy-MM-dd}", new GregorianCalendar(2009, 10, 1).getTime()));

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(GregorianCalendar.AM_PM, GregorianCalendar.AM);
        calendar.set(GregorianCalendar.HOUR, 5);
        calendar.set(GregorianCalendar.MINUTE, 30);
        calendar.set(GregorianCalendar.SECOND, 07);

        check("Date = 05:30:07", basic.format("Date = {0:HH:mm:ss}", calendar.getTime()));
    }

    static void check(String correct, String answer) {
        if (!correct.equals(answer)) {
            System.err.println(answer + " | " + correct);
            System.exit(1);
        }
    }

    static void testWrong() {
        checkWrong("Strange {", "{");
        checkWrong("Strange }", "}");
        checkWrong("Extention not found.", "{0:int}", new HashMap());
        checkWrong("index of argv ouf of range.", "number {6}", 1, 2);
        checkWrong("index of argv ouf of range.", "number {-5}", 1, 2);
        checkWrong("Found error type of argvs.", "number {int}", 1, 2);
        checkWrong("Error in format extend", "Int null = {0:ut}", 20);
        checkWrong("I forget smth. Don't use \"*:\"", "{0:}", 4);
        checkWrong("I forget smth. Don't use {}.", "{}", 4);
        checkWrong("-0 not good", "... {-0} ... ", 98);

    }

    static void checkWrong(String correct, String input, Object... argv) {
        StringFormatter basic = factory.create(StringFormatterDateExtension.class.getName(),
                StringFormatterIntegerExtension.class.getName());
        try {
            basic.format(input, argv);
        } catch (FormatterException e) {
            if (!e.getMessage().equals(correct)) {
                System.err.println(correct + " | " + e.getMessage());
                System.exit(1);
            } else {
                return;
            }
        }
        System.err.println("Don't fall");
        System.exit(1);
    }
}
