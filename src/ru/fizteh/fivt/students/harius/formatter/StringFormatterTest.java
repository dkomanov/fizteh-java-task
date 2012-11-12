package ru.fizteh.fivt.students.harius.formatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;
import java.util.Arrays;
import java.math.BigInteger;

class Another {
    public int a = 76;
}

class Dummy {
    public int x = 3;
    public float y = 7.5f;
    public Object obj = null;
    public Another an = new Another();
    private long z = -10;
}

public abstract class StringFormatterTest {

    private static StringFormatterFactory factory = new StringFormatterFactory();
    private static Dummy d = new Dummy();

    public static void main(String[] args) {
        simple();
        simpleDouble();
        simpleBigInteger();
        simpleCrash();
        System.err.println("All tests OK");
    }

    public static void simple() {
        StringFormatter basic = factory.create();
        check(basic, "hello", "hello");
        check(basic, "hello, world", "hello, {0}", "world");
        check(basic, "one two three", "{0} {1} {2}", "one", "two", "three");
        check(basic, "one one one", "{0} {0} {0}", "one");
        check(basic, d.x + " " + d.y, "{0.x} {0.y}", d);
        check(basic, "", "{0}", (Object)null);
        check(basic, "", "{0.obj}", d);
        check(basic, "{hello}", "{{{0}}}", "hello");
        check(basic, "{0}", "{{0}}", "hello");
        check(basic, "" + d.an.a, "{0.an.a}", d);
    }

    public static void simpleDouble() {
        StringFormatter dbasic = factory.create(
            StringFormatterDoubleExtension.class.getName());
        check(dbasic, "hello", "hello");
        check(dbasic, "3.14", "{0:.2}", 3.1415926);
    }

    public static void simpleBigInteger() {
        StringFormatter ibasic = factory.create(
            StringFormatterBigIntegerExtension.class.getName());
        check(ibasic, "hello", "hello");
        check(ibasic, "1234321", "{0:}", BigInteger.valueOf(1111 * 1111));
    }

    public static void simpleCrash() {
        StringFormatter basic = factory.create();
        checkFail(basic, "{0}");
        checkFail(basic, "{");
        checkFail(basic, "{ ");
        checkFail(basic, "}");
        checkFail(basic, "{0.bad}", "hello");
        checkFail(basic, "{0:field}", "hello");
        checkFail(basic, "{0}}", "hello");
        checkFail(basic, "{crash!}", "hello");
        checkFail(basic, "{-1}", "hello");
        checkFail(basic, "{0.z}", d);
    }

    public static void check(StringFormatter formatter,
        String correct, String pattern, Object... args) {

        try {
            String result = formatter.format(pattern, args);
            if (!result.equals(correct)) {
                System.err.println(String.format("%s\t!=\t%s\t%s",
                    result, pattern, Arrays.toString(args)));
                System.err.println("correct one is " + correct);
                System.exit(1);
            } else {
                System.err.println(String.format("%s\t==\t%s\t%s",
                    result, pattern, Arrays.toString(args)));
            }
        } catch (Exception ex) {
            System.err.println(ex.getClass().getSimpleName() +
                " was thrown unexpectedly");
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

    public static void checkFail(StringFormatter formatter,
        String pattern, Object... args) {

        try {
            String result = formatter.format(pattern, args);
            System.err.println(String.format("%s\t!=\t%s\t%s",
                result, pattern, Arrays.toString(args)));
            System.err.println("no exception was thrown when needed");
            System.exit(1);
        } catch (Exception ex) {
            if (FormatterException.class.isInstance(ex)) {
                System.err.println(String.format("fail:<%s>\t==\t%s\t%s",
                    ex.getMessage(), pattern, Arrays.toString(args)));
            } else {
                System.err.println("wrong exception was thrown: " +
                    ex.getClass().getSimpleName());
                System.exit(1);
            }
        }
    }
    
}