package ru.fizteh.fivt.students.verytable.formatter;

import ru.fizteh.fivt.format.FormatterException;
import java.util.*;

public class FormatterTest {

    private static StringFormatterFactory factory = new StringFormatterFactory();

    static public class A {
        private final int i1 = 1;
        protected Integer i2 = 2;
        public Integer i3 = 3;
    }

    static public class B extends A {
        public final int i3 = -3;
        private final int i4 = 4;
    }

    static public class C {
        public A a = new A();
        private B b = new B();
    }

    static public class D {
        A a;
        D(int n) {
            a = null;
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            testSimple();
            System.out.println("Simple tests ok.");
            testInteger();
            System.out.println("With integers works fine.");
            testDate();
            System.out.println("With date works fine.");
            testFalls();
            System.out.println("With exceptions works fine.");
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

    static void testSimple() throws Exception {

        StringFormatter formatter = factory.create();

        check(formatter, "a", "a");
        check(formatter, "a b", "a {0}", "b");
        check(formatter, "a b", "{0} {1}", "a", "b");
        check(formatter, "a b", "{2} {1}", "c" , "b", "a");
        check(formatter, "ab", "a{0}b", "");
        check(formatter, "", "{0}", (Integer)null);
        check(formatter, "", "{0:s}", (String)null);
        check(formatter, "{a}", "{{{0}}}", "a");
        check(formatter, "{0}", "{{0}}", "a");
        check(formatter, "a{{", "a{{{{", "b");
        check(formatter, "}}a", "}}}}a", "b");
        check(formatter, "}}a{{b}}", "}}}}a{{{{{0}}}}}", "b");

        A a = new A();
        check(formatter, "1 2 3" , "{0.i1} {1.i2} {2.i3}", a, a ,a);
        B b = new B();
        check(formatter, "-3 4 ", "{0.i3} {0.i4} {0.i7}", b);
        C c = new C();
        check(formatter, "4 1", "{0.b.i4} {1.a.i1}", c, c);
        D d = new D(0);
        check(formatter, "", "{0.a.a}", d);
        D dd = null;
        check(formatter, "", "{0.a.a}", dd);
    }

    static void testInteger() throws Exception {

        StringFormatter integerFormatter = factory.create(IntegerExtension.class.getName());

        check(integerFormatter, "0", "{0}", 0);
        check(integerFormatter, "1", "{1}", 0, 1);
        check(integerFormatter, "1 0", "{0} {1}", 1, 0);
        check(integerFormatter, "1 0", "{1} {0}", 0, 1);
        check(integerFormatter, "01", "0{0}1", "");
        check(integerFormatter, "7 / 2 = 3", "7 / 2 = {0:1d}", 7 / 2);
        check(integerFormatter, "{0}", "{{{0}}}", 0);
        check(integerFormatter, "01", "{0:02d}", 1);
        check(integerFormatter, " 1", "{0:2d}", 1);
        check(integerFormatter, "{111}", "{{{0:o}}}", 73);
        check(integerFormatter, "", "{0:o}", (Integer) null);
    }

    static void testDate() throws Exception {
        
        StringFormatter dateFormatter = factory.create(DateExtension.class.getName());

        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, 2012);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DAY_OF_MONTH, 21);
        calendar.set(Calendar.AM_PM, GregorianCalendar.AM);
        calendar.set(Calendar.HOUR, 21);
        calendar.set(Calendar.MINUTE, 12);
        calendar.set(Calendar.SECOND, 0);

        check(dateFormatter, "2012.12.21", "{0:yyyy.MM.dd}", calendar.getTime());
        check(dateFormatter, "2012-12-21", "{0:yyyy-MM-dd}", calendar.getTime());
        check(dateFormatter, "21.12.00", "{0:HH.mm.ss}", calendar.getTime());
        check(dateFormatter, "21:12:00!", "{0:HH:mm:ss}!", calendar.getTime());
    }

    static void check(StringFormatter formatter, String correct, String input,
                      Object... args) {

        try {
            String result = formatter.format(input, args);
            if (!result.equals(correct)) {
                System.err.println("Mine   : " + result);
                System.err.println("Correct: " + correct);
                System.exit(1);
            }
        } catch (Exception ex) {
            System.err.println("Unexpected exception: " + ex.getMessage());
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

    static void testFalls() {

        StringFormatter formatterForFall = factory.create(DateExtension.class.getName(),
                                                          IntegerExtension.class.getName());

        checkForFall(formatterForFall, "Error: unexpected '{'.", "{");
        checkForFall(formatterForFall, "Error: unexpected '}'.", "}");
        checkForFall(formatterForFall, "Error: no suitable extensions.", "{0:int}", new HashMap());
        checkForFall(formatterForFall, "Error: invalid argument number.", "{2}", 1);
        checkForFall(formatterForFall, "Error: argument number must be non-negative"
                                       + " integer, containing only digits.", "{-1}", 1);
        checkForFall(formatterForFall, "Error: argument number must be non-negative"
                                       + " integer, containing only digits.", "{-0}", 1);
        checkForFall(formatterForFall, "Error: argument number must be non-negative "
                                       + "integer, containing only digits.", "{+0}", 1);
        checkForFall(formatterForFall, "Error: argument number must be non-negative "
                                       + "integer, containing only digits.", "{+1}", 1);
        checkForFall(formatterForFall, "Error: argument number must be non-negative "
                                       + "integer, containing only digits.", "{abc}", 1);
        checkForFall(formatterForFall, "Error: invalid argument number.", "{1bc}", 1);
        checkForFall(formatterForFall, "Error: invalid pattern.", "{0:}", 1);
    }

    static void checkForFall(StringFormatter formatter, String correct,
                             String input, Object... args) {

        try {
            formatter.format(input, args);
            System.err.println("No exception thrown.");
            System.exit(1);
        } catch (FormatterException fex) {
            if (!fex.getMessage().equals(correct)) {
                System.err.println("Mine   : " + fex.getMessage());
                System.err.println("Correct: " + correct);
                System.exit(1);
            }
        }
    }
}
