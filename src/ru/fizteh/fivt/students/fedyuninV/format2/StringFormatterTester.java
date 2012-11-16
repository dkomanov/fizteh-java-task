package ru.fizteh.fivt.students.fedyuninV.format2;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void incorrectClass() {
        thrown.expect(FormatterException.class);
        thrown.expectMessage("There is no relative extension");
        formatter.format("Hello {0.deep:d} world!", new ChildForTest());
    }

    @Test
    public void singleOpenBracket() {
        thrown.expect(FormatterException.class);
        thrown.expectMessage("Incorrect bracket sequence");
        formatter.format("Hello {{0}} {0:.3f world!", 3.1415926f);
    }

    @Test
    public void singleCloseBracket() {
        thrown.expect(FormatterException.class);
        thrown.expectMessage("Incorrect bracket sequence");
        formatter.format("Hello {{0}} 0:.3f} world!", 3.1415926f);
    }

    @Test
    public void fieldsInherited() {
        B b = new B();
        b.a = 1;
        b.b = 2;
        Assert.assertEquals("12", formatter.format("{0.a}{0.b}", b));
    }

    private static class A {
        public int a;
    }

    private static class B extends A {
        public int b;
    }

        @Test
    public void tripleSingleOpenBracket() {
        thrown.expect(FormatterException.class);
        thrown.expectMessage("Incorrect bracket sequence");
        formatter.format("Hello {{{0}} {0:.3f} world!", 3.1415926f);
    }

    @Test
    public void tripleSingleCloseBracket() {
        thrown.expect(FormatterException.class);
        thrown.expectMessage("Incorrect bracket sequence");
        formatter.format("Hello {{1}}} {0:.3f} world!", 3.1415926f);
    }

    @Test
    public void badPattern() {
        thrown.expect(FormatterException.class);
        thrown.expectMessage("Incorrect pattern");
        formatter.format("Hello {{1}} {0:.....2323....f} world!", 3.1415926f, 3.1415926f);
    }

    @Test
    public void outOfArray() {
        thrown.expect(FormatterException.class);
        thrown.expectMessage("Index out of array");
        formatter.format("Hello {{1}} {2} world!", 3.1415926f, 3.1415926f);
    }

    @Test
    public void tryingToParentPrivateField() {
        String testString = formatter.format("{0.deep}", new ChildForTest(), 3.1415926f);
        Assert.assertEquals(testString, "100");
    }

    @Test
    public void tryingToParentProtectedField() {
        String testString = formatter.format("{0.dispersion}", new ChildForTest(), 3.1415926f);
        Assert.assertEquals(testString, "0.12");
    }

    @Test
    public void tryingToPrivateField() {
        String testString = formatter.format("{0.deep}", new ClassForTest(), 3.1415926f);
        Assert.assertEquals(testString, "100");
    }

    @Test
    public void tryingToProtectedField() {
        String testString = formatter.format("{0.dispersion}", new ClassForTest(), 3.1415926f);
        Assert.assertEquals(testString, "0.12");
    }

    @Test
    public void tryingToNonExistingField() {
        String testString = formatter.format("{0.nonExistingField}", new ClassForTest(), 3.1415926f);
        Assert.assertEquals(testString, "");
    }

    @Test
    public void nullParentField() {
        String testString = formatter.format("{0.x} {0.y}", new ChildForTest());
        Assert.assertEquals(testString, " ");

        testString = formatter.format("{1.x}", new ChildForTest(), new ChildForTest());
        Assert.assertEquals(testString, "");
    }

    @Test
    public void wrongNumber() {
        thrown.expect(FormatterException.class);
        thrown.expectMessage("Incorrect number in brackets");
        formatter.format("{0{1}}", new ClassForTest(), 3.1415926f);
    }


    @Test
    public void minusZero() {
        thrown.expect(FormatterException.class);
        thrown.expectMessage("Incorrect number in brackets");
        formatter.format("{-0}", new ClassForTest(), 3.1415926f);
    }

    @Test
    public void minusOne() {
        thrown.expect(FormatterException.class);
        thrown.expectMessage("Incorrect number in brackets");
        formatter.format("{-1}", new ClassForTest(), 3.1415926f);
    }

    @Test
    public void voidFloatPattern() {
        thrown.expect(FormatterException.class);
        thrown.expectMessage("Incorrect patter");
        formatter.format("{1:}", new ClassForTest(), 3.1415926f);
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

        testString = formatter.format("{0.height:.6f} {0.width} {0.params:}", new ChildForTest());
        Assert.assertEquals(testString, "100.000000 1000 [12, 13, 14, 15]");

        testString = formatter.format("{0.height:.6f} {0.params:}", new ClassForTest());
        Assert.assertEquals(testString, "100.000000 [12, 13, 14, 15]");
    }
}

