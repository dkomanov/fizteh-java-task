package ru.fizteh.fivt.students.fedyuninV.format;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.format.FormatterException;

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

    @Test(expected = FormatterException.class)
    public void tryingToParentPrivateField() {
        formatter.format("{0.deep}", new ChildForTest(), 3.1415926f);
    }

    @Test(expected = FormatterException.class)
    public void tryingToParentProtectedField() {
        formatter.format("{0.dispersion}", new ChildForTest(), 3.1415926f);
    }

    @Test(expected = FormatterException.class)
    public void tryingToPrivateField() {
        formatter.format("{0.deep}", new ClassForTest(), 3.1415926f);
    }

    @Test(expected = FormatterException.class)
    public void tryingToProtectedField() {
        formatter.format("{0.dispersion}", new ClassForTest(), 3.1415926f);
    }

    @Test(expected = FormatterException.class)
    public void tryingToNonExistingField() {
        formatter.format("{0.nonExistingField}", new ClassForTest(), 3.1415926f);
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
        Assert.assertEquals("Hello {1} 3.1415925 world!", testString);

        byte[] test = {1, 12, 123, 14, 15};
        testString = formatter.format("Hello {{1}} {0} world!", test);
        Assert.assertEquals("Hello {1} " + test.toString() + " world!", testString);

        test = null;
        testString = formatter.format("Hello {{1}} {{{0}}} world!", test);
        Assert.assertEquals("Hello {1} {} world!", testString);

        testString = formatter.format("Hello {{1}} {0} world!", test);
        Assert.assertEquals("Hello {1}  world!", testString);

        testString = formatter.format("{0.height:.6f} {0.width} {0.params:s}", new ChildForTest());
        Assert.assertEquals(testString, "100.000000 1000 [12, 13, 14, 15]");

        testString = formatter.format("{0.height:.6f} {0.params:s}", new ClassForTest());
        Assert.assertEquals(testString, "100.000000 [12, 13, 14, 15]");
    }
}

