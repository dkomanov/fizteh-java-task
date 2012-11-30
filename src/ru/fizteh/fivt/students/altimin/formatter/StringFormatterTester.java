package ru.fizteh.fivt.students.altimin.formatter;

import java.util.Calendar;
import java.util.Locale;

/**
 * User: altimin
 * Date: 12/1/12
 * Time: 12:49 AM
 */

public class StringFormatterTester {
    private static StringFormatterFactory factory = new StringFormatterFactory();

    static void check(String output, String answer) {
        if (!output.equals(answer)) {
            System.out.println("Expected = " + answer + ", got = " + output);
            System.exit(1);
        }
    }

    static void testCalendar() {
        IStringFormatter formatter = null;
        try {
            formatter = factory.create(StringFormatterCalendarExtension.class.getName());
        } catch (FormatterException e) {
            System.out.println("Unexpected exception while creating StringFormatter");
            System.exit(1);
        }
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.set(2012, 6, 6, 12, 56, 18);
        try {
            check(formatter.format("{0:y/M/d}", calendar), "2012/7/6");
            check(formatter.format("{0:y/M/d H:m:s}", calendar), "2012/7/6 12:56:18");
        } catch (FormatterException e) {
            System.out.println("Unexpected exception while formatting calendar: " + e.getMessage());
            System.exit(1);
        }
    }

    static void testLong() {
        IStringFormatter formatter = null;
        try {
            formatter = factory.create(StringFormatterLongExtension.class.getName());
        } catch (FormatterException e) {
            System.out.println("Unexpected exception while creating StringFormatter");
            System.exit(1);
        }
        try {
            check(formatter.format("{0:d}", new Long(0)), "0");
            check(formatter.format("{0:d}", new Long(3)), "3");
            check(formatter.format("{0:d}", new Long(-23)), "-23");
            check(formatter.format("{0:d}", new Long(143060154)), "143060154");
            check(formatter.format("{0:d}", new Long(-615354423)), "-615354423");
            check(formatter.format("{0:4d}", new Long(3)), "   3");
            check(formatter.format("{0:4d}", new Long(-2)), "  -2");
        } catch (FormatterException e) {
            System.out.println("Unexpected exception while formatting long: " + e.getMessage());
            System.exit(1);
        }
    }

    static void checkIncorrectCase(IStringFormatter stringFormatter, String format, Object... args) {
        try {
            stringFormatter.format(format, args);
        } catch (FormatterException e) {
            return;
        }
        System.out.println("Expected FormatterException, but nothing got");
        System.exit(1);
    }

    static void testBad() {
        IStringFormatter formatter = null;
        try {
            formatter = factory.create();
        } catch (FormatterException e) {
            System.out.println("Unexpected exception while creating StringFormatter");
            System.exit(1);
        }
        checkIncorrectCase(formatter, "{");
        checkIncorrectCase(formatter, "}");
        checkIncorrectCase(formatter, "{0}");
        checkIncorrectCase(formatter, "{1}", 1);
        checkIncorrectCase(formatter, "{6}", 1, 2, 3, 4, 5, 6);
        checkIncorrectCase(formatter, "{-1}", 1, 2, 3, 4, 5, 6);
        checkIncorrectCase(formatter, "{0:aba}", 1);
    }

    static void testBase() {
        IStringFormatter formatter = null;
        try {
            formatter = factory.create();
        } catch (FormatterException e) {
            System.out.println("Unexpected exception while creating StringFormatter");
            System.exit(1);
        }

        class A {
            int a = 1;
            class B {
                int c = 2;
            }
            B b = new B();
        }

        class C extends A {
        }

        try {
            A a = new A();
            C c = new C();
            check(formatter.format("{0.a}", a), "1");
            check(formatter.format("{0.b.c}", a), "2");
            check(formatter.format("{0.a}", c), "1");
            check(formatter.format("{0.b.c}", c), "2");
            check(formatter.format("{{{0}", 3), "{3");
            check(formatter.format("{0}}}", 4), "4}");
            check(formatter.format("{{{0}}}", 5), "{5}");
            check(formatter.format("{0} {1}", 1, 2), "1 2");
        } catch (FormatterException e) {
            System.out.println("Unexpected exception while formatting: " + e.getMessage());
            System.exit(1);
        }


    }

    public static void main(String[] args) {
        testCalendar();
        testLong();
        testBase();
        testBad();
        System.out.println("OK");
    }
}
