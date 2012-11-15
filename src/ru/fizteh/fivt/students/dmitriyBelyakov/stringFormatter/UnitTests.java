package ru.fizteh.fivt.students.dmitriyBelyakov.stringFormatter;

import org.junit.Assert;
import org.junit.Test;
import ru.fizteh.fivt.format.FormatterException;

import java.util.Calendar;

class ClassEleven {
    private int intField = 11;
    public String stringField = "eleven";
    String nullField = null;
}

class ClassTwelve extends ClassEleven {
    ClassEleven eleven = new ClassEleven();
}

class ClassWithCalendar {
    Calendar calendar;

    ClassWithCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
}

public class UnitTests extends Assert {

    // Tests for class CalendarFormat

    @Test(expected = FormatterException.class)
    public void testCalendarFormatIncorrectType() {
        new CalendarFormat().format(new StringBuilder(), new String(), "yyyy.MM.dd HH:mm:ss");
    }

    @Test(expected = FormatterException.class)
    public void testCalendarFormatCalendarNullPointer() {
        new CalendarFormat().format(new StringBuilder(), null, "yyyy.MM.dd HH:mm:ss");
    }

    @Test(expected = FormatterException.class)
    public void testCalendarFormatPatternNullPointer() {
        new CalendarFormat().format(new StringBuilder(), Calendar.getInstance(), null);
    }

    @Test(expected = FormatterException.class)
    public void testCalendarFormatBufferNullPointer() {
        new CalendarFormat().format(null, Calendar.getInstance(), "yyyy");
    }

    @Test(expected = FormatterException.class)
    public void testCalendarFormatEmptyPattern() {
        new CalendarFormat().format(new StringBuilder(), Calendar.getInstance(), "");
    }

    @Test
    public void testCalendarFormat() {
        StringBuilder builder = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 50);
        calendar.set(Calendar.SECOND, 00);
        calendar.set(Calendar.YEAR, 1993);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DAY_OF_MONTH, 11);
        CalendarFormat formatter = new CalendarFormat();
        formatter.format(builder, calendar, "yyyy.MM.dd HH:mm:ss");
        assertEquals("1993.12.11 16:50:00", builder.toString());
        builder = new StringBuilder();
        formatter.format(builder, calendar, "dd.MM.yy");
        assertEquals("11.12.93", builder.toString());
        builder = new StringBuilder();
        formatter.format(builder, calendar, "HH.mm dd.MM.yyyy");
        assertEquals("16.50 11.12.1993", builder.toString());
        builder = new StringBuilder();
    }

    @Test(expected = FormatterException.class)
    public void testCalendarFormatIncorrectFormatString() {
        new CalendarFormat().format(new StringBuilder(), Calendar.getInstance(), "It is incorrect format string.");
    }

    // Tests for class LongFormat

    @Test(expected = FormatterException.class)
    public void testLongFormatIncorrectType() {
        new LongFormat().format(new StringBuilder(), new String(), "\"%4$2s %3$2s %2$2s %1$2s\", \"a\", \"b\", \"c\", \"d\"");
    }

    @Test(expected = FormatterException.class)
    public void testLongFormatIncorrectFormatString() {
        new LongFormat().format(new StringBuilder(), new Long(11), "i am d");
    }

    @Test(expected = FormatterException.class)
    public void testLongFormatNullPointerToBuffer() {
        new LongFormat().format(null, new Long(11), "d");
    }

    @Test(expected = FormatterException.class)
    public void testLongFormatNullPointerToPattern() {
        new LongFormat().format(new StringBuilder(), new Long(11), null);
    }

    @Test(expected = FormatterException.class)
    public void testLongFormatNullPointerToObject() {
        new LongFormat().format(new StringBuilder(), null, "5d");
    }

    @Test
    public void testLongFormat() {
        StringBuilder builder = new StringBuilder();
        LongFormat formatter = new LongFormat();
        formatter.format(builder, new Long(11), "o");
        assertEquals("13", builder.toString());
        builder = new StringBuilder();
        formatter.format(builder, new Long(11), "5x");
        assertEquals("    b", builder.toString());
    }

    // Tests for StringFormatter

    @Test(expected = FormatterException.class)
    public void testStringFormatterNullPointerExtension() {
        new StringFormatter().addExtension(null);
    }

    @Test
    public void testStringFormatterAddExtension() {
        StringFormatter formatter = new StringFormatter();
        formatter.addExtension(new CalendarFormat());
        assertFalse(formatter.supported(Long.class));
        assertTrue(formatter.supported(Calendar.class));
    }

    @Test(expected = FormatterException.class)
    public void testStringFormatterIncorrectCountOfArguments() {
        new StringFormatter().format("{{1}}, {1}", 0);
    }

    @Test(expected = FormatterException.class)
    public void testStringFormatterIncorrectFormat() {
        new StringFormatter().format("{{0}", 0);
    }

    @Test(expected = FormatterException.class)
    public void testStringFormatterUnsupportedType() {
        new StringFormatter().format("{0:yyyy.MM.dd}", Calendar.getInstance());
    }

    @Test(expected = FormatterException.class)
    public void testStringFormatterIncorrectFormatWithField() {
        new StringFormatter().format("{.field} something", new ClassEleven());
    }

    @Test
    public void testStringFormatter() {
        StringFormatter formatter = new StringFormatter();
        assertEquals("first text {second text} third text", formatter.format("first text {{second text}} third text"));
        assertEquals("This is text: text example.", formatter.format("This is text: {0}.", "text example"));
        assertEquals("Example: first {1} 2.", formatter.format("Example: {0} {{1}} {1}.", "first", 2));
        String string = "Hello, world!";
        assertEquals("Text 'Hello, world!', length {13}.", formatter.format("Text '{0}', length {{{1}}}.", string, string.length()));
        assertEquals("Class class java.util.Calendar", formatter.format("Class {0}", Calendar.class));
        assertEquals("eleven is 11", formatter.format("{0.stringField} is {0.intField}", new ClassEleven()));
        assertEquals("eleven", formatter.format("{0.eleven.stringField}", new ClassTwelve()));
        assertEquals("eleven", formatter.format("{0.stringField}", new ClassTwelve()));
        assertEquals(" something", formatter.format("{0.nullField} something", new ClassEleven()));
        assertEquals("", formatter.format("{0.notExistField}", new ClassTwelve()));
        formatter.addExtension(new CalendarFormat());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 50);
        calendar.set(Calendar.SECOND, 00);
        calendar.set(Calendar.YEAR, 1993);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DAY_OF_MONTH, 11);
        assertEquals("1993.12.11", formatter.format("{0:yyyy.MM.dd}", calendar));
        assertEquals("I was born 11.12.1993 at 16:50.", formatter.format("I was born {0:dd.MM.yyyy} at {1.calendar:HH:mm}.",
                calendar, new ClassWithCalendar(calendar)));
        formatter.addExtension(new LongFormat());
        assertEquals("11 is eleven. b in hex.", formatter.format("{0:d} is eleven. {0:x} in hex.", new Long(11)));
        assertEquals(" is null.", formatter.format("{0} is null.", null));
        assertEquals("Without arguments.", formatter.format("Without arguments."));
    }

    // Tests for StringFormatterFactory

    @Test(expected = FormatterException.class)
    public void testStringFormatterExtension() {
        new StringFormatterFactory().create("ru.fizteh.fivt.students.dmitriyBelyakov.stringFormatter.CalendarFormat", "java.util.Calendar");
    }

    @Test
    public void testStringFormatterFactory() {
        StringFormatter formatter = new StringFormatterFactory().create("ru.fizteh.fivt.students.dmitriyBelyakov.stringFormatter.CalendarFormat");
        assertTrue(formatter.supported(Calendar.class));
        assertFalse(formatter.supported(Long.class));
    }
}
