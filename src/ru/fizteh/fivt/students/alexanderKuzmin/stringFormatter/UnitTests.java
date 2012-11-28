package ru.fizteh.fivt.students.alexanderKuzmin.stringFormatter

import java.math.BigInteger;
import java.util.Formatter;

import junit.framework.Assert;
import org.junit.Test;
import ru.fizteh.fivt.format.FormatterException;

/**
 * @author Kuzmin A. group 196 Class UnitTests.
 * 
 */

public class UnitTests {

    class Smt {
        public int pub = 123;
        private final int priv = 1234;
        protected final int prot = 1235;
    }

    @Test(expected = FormatterException.class)
    public void testFormatterFactoryWithDuplicate() {
        new StringFormatterFactory().create("first", "second", "another",
                "second");
    }

    @Test(expected = FormatterException.class)
    public void testExtensionBigIntegerNullPattern() {
        new StringFormatterExtensionBigInteger().format(new StringBuilder(),
                new BigInteger("5"), null);
    }

    @Test(expected = FormatterException.class)
    public void testExtensionBigIntegerNullBuffer() {
        new StringFormatterExtensionBigInteger().format(null, new BigInteger(
                "5"), "d");
    }

    @Test(expected = FormatterException.class)
    public void testExtensionBigIntegerNullObject() {
        new StringFormatterExtensionBigInteger().format(new StringBuilder(),
                null, "d");
    }

    @Test(expected = FormatterException.class)
    public void testExtensionBigIntegerEmptyPattern() {
        new StringFormatterExtensionBigInteger().format(new StringBuilder(),
                new BigInteger("5"), "");
    }

    @Test
    public void testExtensionBigIntegerRight() {
        StringBuilder sb = new StringBuilder();
        new StringFormatterExtensionBigInteger().format(sb, new BigInteger(
                "222"), "d");
        Assert.assertEquals("222", sb.toString());
    }

    @Test(expected = FormatterException.class)
    public void testExtensionBigIntegerIncorrectPattern() {
        new StringFormatterExtensionBigInteger().format(new StringBuilder(),
                new BigInteger("5"), "incorrect pattern");
    }

    @Test(expected = FormatterException.class)
    public void testExtensionDoubleNullPattern() {
        new StringFormatterExtensionDouble().format(new StringBuilder(),
                new Double(5), null);
    }

    @Test(expected = FormatterException.class)
    public void testExtensionDoubleEmptyPattern() {
        new StringFormatterExtensionDouble().format(new StringBuilder(),
                new Double(5), "");
    }

    @Test(expected = FormatterException.class)
    public void testExtensionDoubleIncorrectPattern() {
        StringBuilder sb = new StringBuilder();
        new StringFormatterExtensionDouble().format(sb, new Double(5),
                "incorrect pattern");
        Assert.assertEquals("5,000000", sb.toString());
    }

    @Test(expected = FormatterException.class)
    public void testExtensionDoubleNullBuffer() {
        new StringFormatterExtensionDouble().format(null, new Double(5), "f");
    }

    @Test(expected = FormatterException.class)
    public void testExtensionDoubleNullObject() {
        new StringFormatterExtensionDouble().format(new StringBuilder(), null,
                "f");
    }

    @Test
    public void testExtensionDoubleRight() {
        StringBuilder sb = new StringBuilder();
        Formatter format = new Formatter();
        new StringFormatterExtensionDouble().format(sb, new Double(5), ".6f");
        Assert.assertEquals(format.format("%.6f", 5.0000000).toString(),
                sb.toString());
    }

    @Test(expected = FormatterException.class)
    public void testStringFormatterWrongBracket() {
        new StringFormatter().format("lalala { close", 6.2);
    }

    @Test(expected = FormatterException.class)
    public void testStringFormatterWrongBracket1() {
        new StringFormatter().format("lalal } close", 6.2);
    }

    public StringFormatter formatter = new StringFormatterFactory().create(
            StringFormatterExtensionDouble.class.getName(),
            StringFormatterExtensionBigInteger.class.getName());

    @Test
    public void testStringFormatterNormalDouble() {
        String str = formatter.format("lalal }}0{{ {1.prot} {2:.2f} close", 7,
                new Smt(), 555.1412412412);
        Assert.assertEquals(str, "lalal }0{ 1235 555,14 close");
    }

    @Test
    public void testStringFormatterNormal() {
        String str = formatter.format("lalal {{0}} {0} close", 6.2);
        Assert.assertEquals(str, "lalal {0} 6.2 close");
    }

    @Test
    public void testStringFormatterNormal1() {
        String str = formatter.format("lalal }}0{{ {0.pub} close", new Smt());
        Assert.assertEquals(str, "lalal }0{ 123 close");
    }

    @Test
    public void testStringFormatterNormal2() {
        String str = formatter.format("lalal }}0{{ {1.prot} close", 7,
                new Smt(), 555);
        Assert.assertEquals(str, "lalal }0{ 1235 close");
    }

    @Test(expected = FormatterException.class)
    public void testStringFormatterWrongBracket2() {
        formatter.format("lalala {0 close", 5);
    }

    @Test(expected = FormatterException.class)
    public void testStringFormatterWrongBracket3() {
        formatter.format("lalala 0} close", 5);
    }

    @Test(expected = FormatterException.class)
    public void testStringFormatterWrongBracket4() {
        formatter.format("lalala {{{0}} close", 5);
    }

    @Test(expected = FormatterException.class)
    public void testStringFormatterWrongBracket5() {
        formatter.format("lalala {{0}}} close", 5);
    }

    @Test(expected = FormatterException.class)
    public void testStringFormatterIncorrectPattern() {
        formatter.format("lalal {0:.incorrect} end", new Double(24.5));
    }

    @Test
    public void testStringFormatterCorrectPublicPattern() {
        String str = formatter.format("lalal {0.pub} end", new Smt());
        Assert.assertEquals(str, "lalal 123 end");
    }

    @Test
    public void testStringFormatterCorrectProtectedPattern() {
        String str = formatter.format("lalal {0.prot} end", new Smt());
        Assert.assertEquals(str, "lalal 1235 end");
    }

    @Test
    public void testStringFormatterCorrectPrivatePattern() {
        String str = formatter.format("lalal {0.priv} end", new Smt());
        Assert.assertEquals(str, "lalal 1234 end");
    }

    @Test
    public void testStringFormatterCorrectFormat() {
        String str = formatter.format("lalal{0} end", null);
        Assert.assertEquals(str, "lalal end");
    }

    @Test(expected = FormatterException.class)
    public void testStringFormatterIncorrectPattern1() {
        formatter.format("lalala {0:} close", new Double(24.5));
    }

    @Test(expected = FormatterException.class)
    public void testStringFormatterNoIndex() {
        formatter.format("lalaal {} close", new Double(24.5));
    }

    @Test
    public void testStringFormatterNormalIndex() {
        String str = formatter.format("lalal {{1}} close", 6.3);
        Assert.assertEquals(str, "lalal {1} close");
    }

    @Test(expected = FormatterException.class)
    public void testStringFormatterNegativeIndex() {
        formatter.format("lalal {-0} close", new Double(142));
    }

    @Test(expected = FormatterException.class)
    public void testStringFormatterIncorrectIndex() {
        formatter.format("lalal {{1}} between {1} close", 142);
    }

    @Test(expected = FormatterException.class)
    public void testStringFormatterNoDigitInIndex() {
        formatter.format("lalal {z1} close", new Double(24.5), 6.3);
    }
}