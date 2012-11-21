package ru.fizteh.fivt.students.almazNasibullin.stringFormatter;

import java.util.Calendar;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import ru.fizteh.fivt.format.FormatterException;

/**
 * 19.11.12
 * @author almaz
 */

public class UnitTest {
    //Tests for CalendarExtension

    @Test(expected = FormatterException.class)
    public void calendarNullPointer() {
        new CalendarExtension().format(null, Calendar.getInstance(), "EEE, MMM d, ''yy");
    }

    @Test(expected = FormatterException.class)
    public void calendarBadPatternFormat() {
        new CalendarExtension().format(new StringBuilder(),
                Calendar.getInstance(), "Bad pattern");
    }

    @Test(expected = FormatterException.class)
    public void calendarEmptyPattern() {
        new CalendarExtension().format(null, Calendar.getInstance(), "");
    }

    @Test(expected = FormatterException.class)
    public void calendarBadObject() {
        new CalendarExtension().format(new StringBuilder(),
                new StringBuilder(), "EEE, MMM d, ''yy");
    }

    @Test
    public void calendarExtensionInAll() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 19);
        c.set(Calendar.MONTH, Calendar.NOVEMBER);
        c.set(Calendar.YEAR, 2012);
        c.set(Calendar.MINUTE, 11);
        c.set(Calendar.SECOND, 12);
        StringBuilder sb = new StringBuilder();
        new CalendarExtension().format(sb, c, "yyyy.MM.dd mm:ss");
        Assert.assertEquals("2012.11.19 11:12", sb.toString());
        sb = new StringBuilder();
        new CalendarExtension().format(sb, c, "dd.MM.yy");
        Assert.assertEquals("19.11.12", sb.toString());
        sb = new StringBuilder();
    }

    //Tests for LongExtension

    @Test(expected = FormatterException.class)
    public void longNullPointer() {
        new CalendarExtension().format(new StringBuilder(), null, "5x");
    }

    @Test(expected = FormatterException.class)
    public void longBadBuffer() {
        new CalendarExtension().format(null, new Long(2012), "5x");
    }

    @Test(expected = FormatterException.class)
    public void longBadPatternFormat() {
        new CalendarExtension().format(new StringBuilder(), new Long(2012), "something bad");
    }

    @Test(expected = FormatterException.class)
    public void longEmptyPattern() {
        new CalendarExtension().format(new StringBuilder(), new Long(2012), "");
    }

    @Test
    public void longExtensionInAll() {
        StringBuilder sb = new StringBuilder();
        new LongExtension().format(sb, new Long(256), "3x");
        Assert.assertEquals("100", sb.toString());
        sb = new StringBuilder();
        new LongExtension().format(sb, new Long(65), "3o");
        Assert.assertEquals("101", sb.toString());
    }

    //Tests for StringFormatterFactory

    @Test(expected = FormatterException.class)
    public void factorySameExtensions() {
        new StringFormatterFactory().create("same", "diff", "same");
    }

    @Test(expected = FormatterException.class)
    public void factoryStrangeNameOfExtension() {
        new StringFormatterFactory().create("reallyStrangeName");
    }

    @Test(expected = FormatterException.class)
    public void factoryNullPointer() {
        new StringFormatterFactory().create(null);
    }

    //Tests for StringFormatter

    @Test(expected = FormatterException.class)
    public void formatterBadOrderOfBrackets1() {
        new StringFormatter().format("something }{ here", new Long(2012));
    }

    @Test(expected = FormatterException.class)
    public void formatterBadOrderOfBrackets2() {
        new StringFormatter().format("something {{{0}} here", new Long(2012));
    }

    @Test(expected = FormatterException.class)
    public void formatterBadIndex() {
        new StringFormatter().format("something {{{1}} here", new Long(2012));
    }

    @Test(expected = FormatterException.class)
    public void formatterIncorrectformat() {
        new StringFormatter().format("something {1{0}} here", new Long(2012),
                Calendar.getInstance());
    }

    @Test(expected = FormatterException.class)
    public void formatterNoIndexInFormat() {
        new StringFormatter().format("something {} here", new Long(2012),
                Calendar.getInstance());
    }

    @Test(expected = FormatterException.class)
    public void formatterIncorrectIndex() {
        new StringFormatter().format("something {1,2} here", new Long(2012),
                Calendar.getInstance());
    }

    @Test(expected = FormatterException.class)
    public void formatterNoAvailableExtension() {
        StringFormatter sf = new StringFormatter();
        sf.addExtension(new CalendarExtension());
        sf.format("something {0:5x} here", new Long(2012));
    }

    class ParentTest {
        Long a = 111L;
        private Long b = 222L;
        protected long c = 333L;
    }

    class ChildTest extends ParentTest {
        Long d = 65L;
    }

    @Test
    public void formatterGetNoField() {
        Assert.assertEquals("something here", new StringFormatter().format(
                "something {0.e}here", new ChildTest()));
    }

    @Test
    public void formatterParentField() {
        Assert.assertEquals("something111here", new StringFormatter().format(
                "something{0.a}here", new ChildTest()));
    }

    @Test
    public void formatterParentPrivateField() {
        Assert.assertEquals("something 222 here", new StringFormatter().format(
                "something {0.b} here", new ChildTest()));
    }

    @Test
    public void formatterParentProtectedField() {
        Assert.assertEquals("something 333 here", new StringFormatter().format(
                "something {0.c} here", new ChildTest()));
    }

    
    private StringFormatter formatter;

    @Before
    public void init() {
        formatter = new StringFormatterFactory().create(CalendarExtension.class.getName(),
                LongExtension.class.getName());
    }

    @Test
    public void formatterGoodFormatForLong() {
        Assert.assertEquals("something 101 here", formatter.format(
                "something {0.d:3o} here", new ChildTest()));
    }

    @Test
    public void formatterDoubleBrackets() {
        Assert.assertEquals("{0}", formatter.format(
                "{{0}}", new ChildTest()));
    }

    @Test
    public void formatterTripleBrackets() {
        Assert.assertEquals("{111}", formatter.format(
                "{{{0.a}}}", new ChildTest()));
    }
    
    @Test
    public void formatterNullPointerArgument() {
        Assert.assertEquals("ab", formatter.format(
                "a{0}b", null));
    }
    
    @Test
    public void formatterGoodFormatForCalendar() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 20);
        c.set(Calendar.MONTH, Calendar.NOVEMBER);
        c.set(Calendar.YEAR, 2012);
        Assert.assertEquals("2012.11.20", formatter.format(
                "{0:yyyy.MM.dd}", c);
    }
}
