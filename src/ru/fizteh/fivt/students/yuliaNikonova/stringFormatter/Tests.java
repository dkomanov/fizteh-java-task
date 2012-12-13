package ru.fizteh.fivt.students.yuliaNikonova.stringFormatter;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Formatter;

import junit.framework.TestCase;

import org.junit.*;
import org.junit.rules.ExpectedException;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.students.yuliaNikonova.stringFormatter.BigIntegerExtension;
import ru.fizteh.fivt.students.yuliaNikonova.stringFormatter.DoubleExtension;

public class Tests {
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

    @Rule
    public ExpectedException expt = ExpectedException.none();

    @Test
    public void testBadIndex1() {
        expt.expectMessage("bad index: 2");
        formatter.format("start {{1}} {{{2:.2f}}} end", 1.2345678);
    }

    @Test
    public void testBadIndex2() {
        expt.expectMessage("bad index: -2");
        formatter.format("start {{1}} {{{-2:.2f}}} end", 1.2345678);
    }

    @Test
    public void testBadIndex3() {
        expt.expectMessage("bad index: lalala");
        formatter.format("start {{1}} {{{lalala:.2f}}} end", 1.2345678);
    }

    @Test
    public void testBadIndex4() {
        expt.expectMessage("bad index: +0");
        formatter.format("start {{1}} {{{+0:.2f}}} end", 1.2345678);
    }

    @Test
    public void testWrongFormat() {
        expt.expectMessage("wrong format: 0.");
        formatter.format("{0.}", "1");
    }

    @Test
    public void testBadExtension1() {
        expt.expectMessage("No extension for class java.lang.String");
        formatter.format("start {{1}} {{{0:.2f}}} end", "lalala");
    }

    @Test
    public void testBadExtension2() {
        expt.expectMessage("No extension for class java.lang.Integer");
        formatter.format("start {{1}} {{{0:.2f}}} end", 1);
    }

    @Test
    public void testEmptyBrackets1() {
        expt.expectMessage("Empty brackets");
        formatter.format("start {{1}} {{{}}} end", 1.123);
    }

    @Test
    public void testEmptyBrackets2() {
        expt.expectMessage("Empty brackets");
        formatter.format("start {} {{{0}}} end", 1.123);
    }

    @Test
    public void testWrongBrackets1() {
        expt.expectMessage("something wrong with brackets");
        formatter.format("start {0}} {{{0.lalala:.2f}}} end", 1.123);
    }

    @Test
    public void testWrongBrackets2() {
        expt.expectMessage("something wrong with brackets");
        formatter.format("start {{1} {{{0.lalala:.2f}}} end", 1.123);
    }

    @Test
    public void testWrongBrackets3() {
        expt.expectMessage("something wrong with brackets");
        formatter.format("start 1} {{{0:.2f}}} end", 1.123);
    }

    @Test
    public void tests() {
        Formatter format = new Formatter();
        testString = formatter.format("start {{1}} {{{0:.2f}}} end", 1.2345678);
        String str1 = "start {1} {" + format.format("%.2f", 1.2345678) + "} end";
        Assert.assertEquals(str1, testString);
        format.close();
        format = new Formatter();
        testString = formatter.format("start }}{{ {0:.3f} end", 1.2345678);
        Assert.assertEquals("start }{ " + format.format("%.3f", 1.2345678) + " end", testString);
        format.close();
        format = new Formatter();
        testString = formatter.format("start {{1}} {2:.6f} end", 1.2345678, 1.234, 1.0);
        Assert.assertEquals("start {1} " + format.format("%.6f", 1.0) + " end", testString);
        format.close();

        Object o = null;
        testString = formatter.format("start {{{{1}}0{{0}}0{{{0}}}}} end", o);
        Assert.assertEquals("start {{1}0{0}0{}} end", testString);
        testString = formatter.format("start -{0:28d} end", new BigInteger("123456789987654321123456789"));
        Assert.assertEquals("start - 123456789987654321123456789 end", testString);

        format = new Formatter();
        testString = formatter.format("start {0} {1:.6f} {2} {1} end", 1.0, 2.0, 3.0);
        Assert.assertEquals("start 1.0 " + format.format("%.6f", 2.0) + " 3.0 2.0 end", testString);
        format.close();
        format = new Formatter();
        testString = formatter.format("start {0} {1:.6f} {2} {1} end", 1.0, 2.0, null);
        Assert.assertEquals("start 1.0 " + format.format("%.6f", 2.0) + "  2.0 end", testString);
        format.close();

        format = new Formatter();
        testString = formatter.format("{0.fieldDouble:.6f}", new TestClass());
        Assert.assertEquals(format.format("%.6f", 123.4) + "", testString);
        format.close();

        format = new Formatter();
        testString = formatter.format("{0.fieldProtectedDouble:.6f}", new TestClass());
        Assert.assertEquals(format.format("%.6f", 9.1) + "", testString);
        format.close();

        format = new Formatter();
        testString = formatter.format("{0.fieldProtectedDouble:.6f}", new ChildClass());
        Assert.assertEquals(format.format("%.6f", 9.1) + "", testString);
        format.close();

        format = new Formatter();
        testString = formatter.format("start {{{0.lalala:.2f}}} end", 1.123);
        Assert.assertEquals("start {} end", testString);
        format.close();
    }

}