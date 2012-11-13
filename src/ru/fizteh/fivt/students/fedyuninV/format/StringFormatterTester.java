package ru.fizteh.fivt.students.fedyuninV.format;


import org.junit.Assert;
import org.junit.Test;
import ru.fizteh.fivt.format.FormatterException;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class StringFormatterTester{

    private static StringFormatter formatter;

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(StringFormatterTester.class);
    }

    public static void main(String[] args) {
        formatter = new StringFormatterFactory().create(StringFormatterFloatExtension.class.getName(),
                                                                        StringFormatterByteArrayExtension.class.getName());
        org.junit.runner.JUnitCore.main("ru.fizteh.fivt.students.fedyuninV.format.StringFormatterTester");
    }

    @Test(expected = FormatterException.class)
    public void singleOpenBracket() {
        formatter.format("Hello {{1}} {0:.3 world!", (float) 3.1415926);
    }

    @Test(expected = FormatterException.class)
    public void singleCloseBracket() {
        formatter.format("Hello {{1}} 0:.3} world!", (float) 3.1415926);
    }

    @Test(expected = FormatterException.class)
    public void tripleSingleOpenBracket() {
        formatter.format("Hello {{{1}} {0:.3} world!", (float) 3.1415926);
    }

    @Test(expected = FormatterException.class)
    public void tripleSingleCloseBracket() {
        formatter.format("Hello {{1}}} {0:.3} world!", (float) 3.1415926);
    }

    @Test(expected = FormatterException.class)
    public void voidPattern() {
        formatter.format("Hello {{1}}} {0:} world!", (float) 3.1415926);
    }

    @Test(expected = FormatterException.class)
    public void badPattern() {
        formatter.format("Hello {{1}}} {0:hren} world!", (float) 3.1415926);
    }

    @Test
    public void goodTests() {
        String testString;

        testString = formatter.format("Hello {{1}} {0:.3} world!", (float) 3.1415926);
        Assert.assertEquals("Hello {1} 3.142 world!", testString);

        testString = formatter.format("Hello 1}} {0:.3} world!", (float) 3.1415926);
        Assert.assertEquals("Hello 1} 3.142 world!", testString);

        testString = formatter.format("Hello {{1 {0:.3} world!", (float) 3.1415926);
        Assert.assertEquals("Hello {1 3.142 world!", testString);

        testString = formatter.format("Hello {{1}} {{{0:.3} world!", (float) 3.1415926);
        Assert.assertEquals("Hello {1} {3.142 world!", testString);

        testString = formatter.format("Hello {{1}} {0:.3}}} world!", (float) 3.1415926);
        Assert.assertEquals("Hello {1} 3.142} world!", testString);

        testString = formatter.format("Hello {{1}} {0:.6} world!", (float) 3.1415926);
        Assert.assertEquals("Hello {1} 3.141593 world!", testString);

        testString = formatter.format("Hello {{1}} {0} world!", (float) 3.1415926);
        Assert.assertEquals("Hello {1} 3.1415925 world!", testString);
    }
}

