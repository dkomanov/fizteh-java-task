package ru.fizteh.fivt.students.fedyuninV.format;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.format.FormatterException;

import java.util.Arrays;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class StringFormatterTester{

    private static StringFormatter formatter;

    @Before
    public void init() {
        formatter = new StringFormatterFactory().create(StringFormatterFloatExtension.class.getName(),
                StringFormatterByteArrayExtension.class.getName());
    }

    @Test(expected = FormatterException.class)
    public void singleOpenBracket() {
        formatter.format("Hello {{1}} {0:.3f world!", 3.1415926f);
    }

    @Test(expected = FormatterException.class)
    public void singleCloseBracket() {
        formatter.format("Hello {{1}} 0:.3f} world!", 3.1415926f);
    }

    @Test(expected = FormatterException.class)
    public void tripleSingleOpenBracket() {
        formatter.format("Hello {{{1}} {0:.3f} world!", 3.1415926f);
    }

    @Test(expected = FormatterException.class)
    public void tripleSingleCloseBracket() {
        formatter.format("Hello {{1}}} {0:.3f} world!", 3.1415926f);
    }

    @Test(expected = FormatterException.class)
    public void voidPattern() {
        formatter.format("Hello {{1}}} {0:} world!", 3.1415926f);
    }

    @Test(expected = FormatterException.class)
    public void badPattern() {
        formatter.format("Hello {{1}} {0:.....2323....f} world!", 3.1415926f, 3.1415926f);
    }

    @Test(expected = FormatterException.class)
    public void outOfArray() {
        formatter.format("Hello {{1}} {2} world!", 3.1415926f, 3.1415926f);
    }

    @Test
    public void goodTests() {
        String testString;

        testString = formatter.format("Hello {{1}} {0:.3f} world!", 3.1415926f);
        Assert.assertEquals("Hello {1} 3.142 world!", testString);

        testString = formatter.format("Hello {{1}} {{{0:.3f}}} world!", 3.1415926f);
        Assert.assertEquals("Hello {1} {3.142} world!", testString);

        testString = formatter.format("Hello 1}} {0:.3f} world!", 3.1415926f);
        Assert.assertEquals("Hello 1} 3.142 world!", testString);

        testString = formatter.format("Hello {{1 {0:.3f} world!", 3.1415926f);
        Assert.assertEquals("Hello {1 3.142 world!", testString);

        testString = formatter.format("Hello {{1}} {{{0:.3f} world!", 3.1415926f);
        Assert.assertEquals("Hello {1} {3.142 world!", testString);

        testString = formatter.format("Hello {{1}} {0:.3f}}} world!", 3.1415926f);
        Assert.assertEquals("Hello {1} 3.142} world!", testString);

        testString = formatter.format("Hello {{1}} {2:.6f} world!", 3.1415926f, 3.1415926f, 3.1415926f);
        Assert.assertEquals("Hello {1} 3.141593 world!", testString);

        testString = formatter.format("Hello {{1}} {0} world!", 3.1415926f);
        Assert.assertEquals("Hello {1} 3.141593 world!", testString);

        byte[] test = {1, 12, 123, 14, 15};
        testString = formatter.format("Hello {{1}} {0} world!", test);
        Assert.assertEquals("Hello {1} " + Arrays.toString(test) + " world!", testString);

        test = null;
        testString = formatter.format("Hello {{1}} {{{0}}} world!", test);
        Assert.assertEquals("Hello {1} {} world!", testString);

        testString = formatter.format("Hello {{1}} {0} world!", test);
        Assert.assertEquals("Hello {1}  world!", testString);
    }
}

