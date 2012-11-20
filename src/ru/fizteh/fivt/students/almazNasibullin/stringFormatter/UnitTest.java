package ru.fizteh.fivt.students.almazNasibullin.stringFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    public void CalendarNullPointer() {
        new CalendarExtension().format(null, Calendar.getInstance(), "EEE, MMM d, ''yy");
    }

    @Test(expected = FormatterException.class)
    public void CalendarBadPatternFormat() {
        new CalendarExtension().format(new StringBuilder(),
                Calendar.getInstance(), "Bad pattern");
    }

    @Test(expected = FormatterException.class)
    public void CalendarEmptyPattern() {
        new CalendarExtension().format(null, Calendar.getInstance(), "");
    }

    @Test(expected = FormatterException.class)
    public void CalendarBadObject() {
        new CalendarExtension().format(new StringBuilder(),
                new StringBuilder(), "EEE, MMM d, ''yy");
    }

    @Test
    public void CalendarExtensionInAll() {
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
    public void LongNullPointer() {
        new CalendarExtension().format(new StringBuilder(), null, "5x");
    }

    @Test(expected = FormatterException.class)
    public void LongBadBuffer() {
        new CalendarExtension().format(null, new Long(2012), "5x");
    }

    @Test(expected = FormatterException.class)
    public void LongBadPatternFormat() {
        new CalendarExtension().format(new StringBuilder(), new Long(2012), "something bad");
    }

    @Test(expected = FormatterException.class)
    public void LongEmptyPattern() {
        new CalendarExtension().format(new StringBuilder(), new Long(2012), "");
    }

    @Test
    public void LongExtensionInAll() {
        StringBuilder sb = new StringBuilder();
        new LongExtension().format(sb, new Long(256), "3x");
        Assert.assertEquals("100", sb.toString());
        sb = new StringBuilder();
        new LongExtension().format(sb, new Long(65), "3o");
        Assert.assertEquals("101", sb.toString());
    }

    //Tests for StringFormatterFactory

    @Test(expected = FormatterException.class)
    public void FactorySameExtensions() {
        new StringFormatterFactory().create("same", "diff", "same");
    }

    @Test(expected = FormatterException.class)
    public void FactoryStrangeNameOfExtension() {
        new StringFormatterFactory().create("reallyStrangeName");
    }

    @Test(expected = FormatterException.class)
    public void FactoryNullPointer() {
        new StringFormatterFactory().create(null);
    }

    //Tests for StringFormatter

    @Test(expected = FormatterException.class)
    public void FormatterBadOrderOfBrackets1() {
        new StringFormatter().format("something }{ here", new Long(2012));
    }

    @Test(expected = FormatterException.class)
    public void FormatterBadOrderOfBrackets2() {
        new StringFormatter().format("something {{{0}} here", new Long(2012));
    }

    @Test(expected = FormatterException.class)
    public void FormatterBadIndex() {
        new StringFormatter().format("something {{{1}} here", new Long(2012));
    }

    @Test(expected = FormatterException.class)
    public void FormatterIncorrectformat() {
        new StringFormatter().format("something {1{0}} here", new Long(2012),
                Calendar.getInstance());
    }

    @Test(expected = FormatterException.class)
    public void FormatterNoIndexInFormat() {
        new StringFormatter().format("something {} here", new Long(2012),
                Calendar.getInstance());
    }

    @Test(expected = FormatterException.class)
    public void FormatterIncorrectIndex() {
        new StringFormatter().format("something {1,2} here", new Long(2012),
                Calendar.getInstance());
    }

    @Test(expected = FormatterException.class)
    public void FormatterNoAvailableExtension() {
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
    public void FormatterGetNoField() {
        Assert.assertEquals("something here", new StringFormatter().format(
                "something {0.e}here", new ChildTest()));
    }

    @Test
    public void FormatterParentField() {
        Assert.assertEquals("something111here", new StringFormatter().format(
                "something{0.a}here", new ChildTest()));
    }

    @Test
    public void FormatterParentPrivateField() {
        Assert.assertEquals("something 222 here", new StringFormatter().format(
                "something {0.b} here", new ChildTest()));
    }

    @Test
    public void FormatterParentProtectedField() {
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
    public void AdditionalTests() {
        Assert.assertEquals("something 101 here", formatter.format(
                "something {0.d:3o} here", new ChildTest()));
        Assert.assertEquals("{0}", formatter.format(
                "{{0}}", new ChildTest()));
        Assert.assertEquals("{111}", formatter.format(
                "{{{0.a}}}", new ChildTest()));
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        Assert.assertEquals(dateFormat.format(new Date()), formatter.format(
                "{0:yyyy.MM.dd}", Calendar.getInstance()));
    }
}
