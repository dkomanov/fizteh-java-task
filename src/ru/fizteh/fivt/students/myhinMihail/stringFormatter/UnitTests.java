package ru.fizteh.fivt.students.myhinMihail.stringFormatter;

import java.math.BigInteger;

import org.junit.*;
import org.junit.rules.ExpectedException;

public class UnitTests {
    
    public class TestClass {
        public final double fieldDouble = 123.4;
        public  BigInteger fieldBig = new BigInteger("123456789987654321123456789");
        private final double fieldPrivateDouble = 567.8;
        protected final double fieldProtectedDouble = 9.1;
        public Integer fieldNullInteger = null;
    }

    class ChildClass extends TestClass {
        public int fieldPublicInt = 1234;
    }

    public static StringFormatter formatter;
    public static String testString;

    @Before
    public void init() {
        formatter = new StringFormatterFactory().create(
                DoubleExtension.class.getName(), BigIntegerExtension.class.getName());
    }

    @Rule
    public ExpectedException expt = ExpectedException.none();

    @Test
    public void noExtension() {
        expt.expectMessage("No extension for class java.lang.Integer");
        formatter.format("start {0.fieldPublicInt:d} end", new ChildClass());
    }

    @Test
    public void wrongBrackets() {
        expt.expectMessage("Brackets don't coincide");
        formatter.format("start {{0}} {0:.1f end", 1.23);
    }

    @Test
    public void wrongBrackets2() {
        expt.expectMessage("Brackets don't coincide");
        formatter.format("start {{0}} 0:.1f} end", 1.23);
    }

    @Test
    public void wrongBrackets3() {
        expt.expectMessage("Brackets don't coincide");
        formatter.format("start {{{0}} {0:.1f} end", 1.23);
    }

    @Test
    public void wrongBrackets4() {
        expt.expectMessage("Brackets don't coincide");
        formatter.format("start {{1}}} {0:.1f} end", 1.23);
    }
 
    @Test
    public void badPattern() {
        expt.expectMessage("Bad pattern");
        formatter.format("start {{1}} {0:.bad} end", 1.2345678, 1.2345678);
    }
    
    @Test
    public void badPattern2() {
        expt.expectMessage("Bad pattern");
        formatter.format("{1:}", new TestClass(), 1.2345678);
    }
    
    @Test
    public void noIndex() {
        expt.expectMessage("No index");
        formatter.format("start {} end", new ChildClass());
    }

    @Test
    public void badIndex() {
        expt.expectMessage("Bad index");
        formatter.format("start {{1}} {2} end", 1.23, 1.23);
    }
    
    @Test
    public void badIndex2() {
        expt.expectMessage("Bad index");
        formatter.format("{0{1}}", new TestClass(), 2.0);
    }

    @Test
    public void badIndex3() {
        expt.expectMessage("Bad index");
        formatter.format("{-1}", new TestClass(), 1.23);
    }

    @Test
    public void fieldTests() {
        testString = formatter.format("{0.fieldPrivateDouble}", new ChildClass());
        Assert.assertEquals(testString, "567.8");

        testString = formatter.format("{0.fieldProtectedDouble}", new ChildClass());
        Assert.assertEquals(testString, "9.1");

        testString = formatter.format("{0.fieldPrivateDouble}", new TestClass());
        Assert.assertEquals(testString, "567.8");

        testString = formatter.format("{0.fieldProtectedDouble}", new TestClass());
        Assert.assertEquals(testString, "9.1");

        testString = formatter.format("{0.noField}", new TestClass());
        Assert.assertEquals(testString, "");

        testString = formatter.format("{0.fieldNullInteger}text{0.fieldNullInteger}", new ChildClass());
        Assert.assertEquals(testString, "text");
    }

    @Test
    public void tests() {
        testString = formatter.format("start {{1}} {{{0:.2f}}} end", 1.2345678);
        Assert.assertEquals("start {1} {1.23} end", testString);

        testString = formatter.format("start }}{{ {0:.3f} end", 1.2345678);
        Assert.assertEquals("start }{ 1.235 end", testString);

        testString = formatter.format("start {{1}} {2:.6f} end", 1.2345678, 1.234, 1.0);
        Assert.assertEquals("start {1} 1.000000 end", testString);

        testString = formatter.format("start {{1}} {0} end {0}{{", 1.2345678);
        Assert.assertEquals("start {1} 1.2345678 end 1.2345678{", testString);

        String testString2 = null;
        testString = formatter.format("start {{{{1}}0{{0}}0{{{0}}}}} end", testString2);
        Assert.assertEquals("start {{1}0{0}0{}} end", testString);
        
        testString = formatter.format("start -{0:28d} end", new BigInteger("123456789987654321123456789"));
        Assert.assertEquals("start - 123456789987654321123456789 end", testString);
        
        testString = formatter.format("start {0} {1:.6f} {2} {1} end", 1.0, 2.0, 3.0);
        Assert.assertEquals("start 1.0 2.000000 3.0 2.0 end", testString);
    }
}
