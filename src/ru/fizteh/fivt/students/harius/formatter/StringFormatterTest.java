/*
 * StringFormatterTest.java
 * Nov 14, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.formatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;
import java.util.Arrays;
import java.math.BigInteger;

/*
 * A pair of classes for testing
 */

class Another {
    public int a = 76;
}

class Dummy {
    public int x = 3;
    public float y = 7.5f;
    public Object obj = null;
    public Another an = new Another();
    private long z = -10;
    private short zz = 32;
    protected String s = "oO";
}

class Stuff extends Dummy {
    private long z = -9;
}

class OneMore extends Stuff {
    String hello = "goodbye";
}

/*
 * Several correctness tests for StringFormatter
 */
public abstract class StringFormatterTest {
    private static StringFormatterFactory factory = new StringFormatterFactory();
    private static Dummy d = new Dummy();
    private static Stuff st = new Stuff();
    private static OneMore om = new OneMore();

    /* Do the tests */
    public static void main(String[] args) {
        simple();
        simpleDouble();
        simpleBigInteger();
        simpleCrash();
        simpleDoubleCrash();
        System.err.println("All tests OK");
    }

    /* Basic tests */
    public static void simple() {
        StringFormatter basic = factory.create();
        check(basic, "hello", "hello");
        check(basic, "hello, world", "hello, {0}", "world");
        check(basic, "one two three", "{0} {1} {2}", "one", "two", "three");
        check(basic, "one one one", "{0} {0} {0}", "one");
        check(basic, d.x + " " + d.y, "{0.x} {0.y}", d);
        check(basic, "", "{0}", (Object)null);
        check(basic, "", "{0:d}", (Object)null);
        check(basic, "", "{0.obj}", d);
        check(basic, "{hello}", "{{{0}}}", "hello");
        check(basic, "{0}", "{{0}}", "hello");
        check(basic, "" + d.an.a, "{0.an.a}", d);
        check(basic, "-10", "{0.z}", d);
        check(basic, "-9", "{0.z}", st);
        check(basic, "32", "{0.zz}", st);
        check(basic, "7.5", "{0.y}", st);
        check(basic, "32", "{0.zz}", om);
    }

    /* Basic tests of formatting double */
    public static void simpleDouble() {
        StringFormatter dbasic = factory.create(
            StringFormatterDoubleExtension.class.getName());
        check(dbasic, "hello", "hello");
        check(dbasic, "3.14", "{0:.2f}", 3.1415926);
        check(dbasic, "pi=3!", "pi={0:.0f}!", Math.PI);
        check(dbasic, "", "{0:E}", (Object)null);
    }

    /* Basic tests of crashing double formatting */
    public static void simpleDoubleCrash() {
        StringFormatter dbasic = factory.create(
            StringFormatterDoubleExtension.class.getName());
        checkFail(dbasic, "{0:test_test}?!", 4.33);
    }

    /* Basic tests of formatting BigInteger */
    public static void simpleBigInteger() {
        StringFormatter ibasic = factory.create(
            StringFormatterBigIntegerExtension.class.getName());
        check(ibasic, "hello", "hello");
        check(ibasic, "1234321", "{0:d}", BigInteger.valueOf(1111 * 1111));
        check(ibasic, "ab 1 cd", "ab {0:X} cd", BigInteger.valueOf(1));
    }

    /* Basic crash tests */
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
        checkFail(basic, null);
        checkFail(basic, "{0} {1}", (Object[])null);
        checkFail(basic, "{-0}", "hello");
        checkFail(basic, "{+3}", "hello");

        StringBuilder builder = null;
        checkBufferFail(basic, builder, "hello, {0}", "world");
    }

    /* Check correctness */
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

    /* Check that code fails with expected exception */
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

    /* Check fails of the buffered formatter variant */
    public static void checkBufferFail(StringFormatter formatter, 
        StringBuilder buffer, String pattern, Object... args) {

        try {
            formatter.format(buffer, pattern, args);
            String result = buffer.toString();
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