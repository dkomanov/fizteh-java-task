package ru.fizteh.fivt.students.yuliaNikonova.stringFormatter;

import java.math.BigInteger;
import java.util.Formatter;

import junit.framework.TestCase;

import org.junit.*;
import org.junit.rules.ExpectedException;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.students.yuliaNikonova.stringFormatter.BigIntegerExtension;
import ru.fizteh.fivt.students.yuliaNikonova.stringFormatter.DoubleExtension;

public class Tests extends TestCase {
    public class TestClass {
        public final double fieldDouble = 123.4;
        public BigInteger fieldBig = new BigInteger("123456789987654321123456789");
        private final double fieldPrivateDouble = 567.8;
        protected final double fieldProtectedDouble = 9.1;
        public Integer fieldNullInteger = null;
    }

    public class ChildClass extends TestClass {
        public BigInteger childBig = new BigInteger("12345678901234567890");
    }

    public static StringFormatter formatter = new StringFormatterFactory().create(DoubleExtension.class.getName(), BigIntegerExtension.class.getName());;
    public String testString = "";

    @Test
    public void testBadIndex() {
        try {
            testString = formatter.format("start {{1}} {{{2:.2f}}} end", 1.2345678);
        } catch (FormatterException e) {
            assertEquals("bad index: 2", e.getMessage());
        }

        try {
            testString = formatter.format("start {{1}} {{{-2:.2f}}} end", 1.2345678);
        } catch (FormatterException e) {
            assertEquals("bad index: -2", e.getMessage());
        }

        try {
            testString = formatter.format("start {{1}} {{{lalala:.2f}}} end", 1.2345678);
        } catch (FormatterException e) {
            assertEquals("bad index: lalala", e.getMessage());
        }

    }

    @Test
    public void testBadExtension() {
        try {
            testString = formatter.format("start {{1}} {{{0:.2f}}} end", "lalala");
        } catch (FormatterException e) {
            assertEquals("No extension for class java.lang.String", e.getMessage());
        }

        try {
            testString = formatter.format("start {{1}} {{{0:.2f}}} end", 1);
        } catch (FormatterException e) {
            assertEquals("No extension for class java.lang.Integer", e.getMessage());
        }
    }

    @Test
    public void testBadField() {
        try {
            testString = formatter.format("start {{1}} {{{0.lalala:.2f}}} end", 1.123);
        } catch (FormatterException e) {
            assertEquals("Field lalala in class java.lang.Double is unaccessible", e.getMessage());
        }
    }

    @Test
    public void testEmptyBrackets() {
        try {
            testString = formatter.format("start {{1}} {{{}}} end", 1.123);
        } catch (FormatterException e) {
            assertEquals("Empty brackets", e.getMessage());
        }

        try {
            testString = formatter.format("start {{}} {{{0}}} end", 1.123);
        } catch (FormatterException e) {
            assertEquals("Empty brackets", e.getMessage());
        }
    }

    @Test
    public void testWrongBrackets() {
        try {
            testString = formatter.format("start {0}} {{{0.lalala:.2f}}} end", 1.123);
        } catch (FormatterException e) {
            assertEquals("something wrong with brackets", e.getMessage());
        }

        try {
            testString = formatter.format("start {{1} {{{0.lalala:.2f}}} end", 1.123);
        } catch (FormatterException e) {
            assertEquals("something wrong with brackets", e.getMessage());
        }

        try {
            testString = formatter.format("start 1}} {{{0:.2f}}} end", 1.123);
        } catch (FormatterException e) {
            assertEquals("something wrong with brackets", e.getMessage());
        }
    }

    public void tests() {
        Formatter format = new Formatter();
        /* testString = formatter.format("start {{1}} {{{0:.2f}}} end",
         * 1.2345678);
         * String str1 = "start {1} {" + format.format("%.2f", 1.2345678) +
         * "} end";
         * // System.out.println(testString + " || " + str1);
         * Assert.assertEquals(str1, testString);
         * format.close();
         * 
         * format = new Formatter();
         * testString = formatter.format("start }}{{ {0:.3f} end", 1.2345678);
         * Assert.assertEquals("start }{ " + format.format("%.3f", 1.2345678) +
         * " end", testString);
         * format.close();
         * format = new Formatter();
         * testString = formatter.format("start {{1}} {2:.6f} end", 1.2345678,
         * 1.234, 1.0);
         * Assert.assertEquals("start {1} " + format.format("%.6f", 1.0) +
         * " end", testString);
         * format.close();
         * 
         * String testString2 = null;
         * testString = formatter.format("start {{{{1}}0{{0}}0{{{0}}}}} end",
         * testString2);
         * Assert.assertEquals("start {{1}0{0}0{}} end", testString);
         * 
         * testString = formatter.format("start -{0:28d} end", new
         * BigInteger("123456789987654321123456789"));
         * Assert.assertEquals("start - 123456789987654321123456789 end",
         * testString);
         * 
         * format = new Formatter();
         * testString = formatter.format("start {0} {1:.6f} {2} {1} end", 1.0,
         * 2.0, 3.0);
         * Assert.assertEquals("start 1.0 " + format.format("%.6f", 2.0) +
         * " 3.0 2.0 end", testString);
         * format.close();
         * 
         * format = new Formatter();
         * testString = formatter.format("start {0} {1:.6f} {2} {1} end", 1.0,
         * 2.0, null);
         * Assert.assertEquals("start 1.0 " + format.format("%.6f", 2.0) +
         * "  2.0 end", testString);
         * format.close();
         * 
         * format = new Formatter();
         * testString = formatter.format("{0.fieldDouble:.6f}", new
         * TestClass());
         * Assert.assertEquals(format.format("%.6f", 123.4) + "", testString);
         * format.close();
         * 
         * format = new Formatter();
         * testString = formatter.format("{0.fieldProtectedDouble:.6f}", new
         * TestClass());
         * Assert.assertEquals(format.format("%.6f", 9.1) + "", testString);
         * format.close(); */

        format = new Formatter();
        testString = formatter.format("{0.fieldProtectedDouble:.6f}", new ChildClass());
        Assert.assertEquals(format.format("%.6f", 9.1) + "", testString);
        format.close();

    }

}