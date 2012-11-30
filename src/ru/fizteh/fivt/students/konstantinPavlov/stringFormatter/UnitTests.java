package ru.fizteh.fivt.students.konstantinPavlov.stringFormatter;

import static org.junit.Assert.*;

import java.util.Calendar;
import org.junit.Assert.*;
import org.junit.Test;

import ru.fizteh.fivt.format.FormatterException;

public class UnitTests {

    class TestClass {
        private final int fieldInt = 100;
        public String fieldString = "string content";
    }

    class TestChildClass extends TestClass {
        TestClass testClass = new TestClass();
    }

    class CalendarTestClass {
        Calendar calendar;

        CalendarTestClass(Calendar calendar) {
            this.calendar = calendar;
        }
    }

    @Test(expected = FormatterException.class)
    public void testEmptyField() {
        new StringFormatter().format("{0.}", "1");
    }

    @Test(expected = FormatterException.class)
    public void testCalendarFormatIncorrectType() {
        new CalendarExtension().format(new StringBuilder(), new String(),
                "yyyy.MM.dd HH:mm:ss");
    }

    @Test(expected = FormatterException.class)
    public void testCalendarFormatCalendarNullPointer() {
        new CalendarExtension().format(new StringBuilder(), null,
                "yyyy.MM.dd HH:mm:ss");
    }

    @Test(expected = FormatterException.class)
    public void testCalendarFormatPatternNullPointer() {
        new CalendarExtension().format(new StringBuilder(),
                Calendar.getInstance(), null);
    }

    @Test(expected = FormatterException.class)
    public void testCalendarFormatBufferNullPointer() {
        new CalendarExtension().format(null, Calendar.getInstance(), "yyyy");
    }

    @Test(expected = FormatterException.class)
    public void testCalendarFormatEmptyPattern() {
        new CalendarExtension().format(new StringBuilder(),
                Calendar.getInstance(), "");
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
        CalendarExtension formatter = new CalendarExtension();
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
        new CalendarExtension().format(new StringBuilder(),
                Calendar.getInstance(), "incorrect format string");
    }

    @Test(expected = FormatterException.class)
    public void testLongFormatIncorrectFormatString() {
        new LongExtension().format(new StringBuilder(), new Long(11), "i am d");
    }

    @Test(expected = FormatterException.class)
    public void testLongFormatNullPointerToBuffer() {
        new LongExtension().format(null, new Long(11), "d");
    }

    @Test(expected = FormatterException.class)
    public void testLongFormatNullPointerToPattern() {
        new LongExtension().format(new StringBuilder(), new Long(11), null);
    }

    @Test(expected = FormatterException.class)
    public void testLongFormatNullPointerToObject() {
        new LongExtension().format(new StringBuilder(), null, "5d");
    }

    @Test
    public void testLongFormat() {
        StringBuilder builder = new StringBuilder();
        LongExtension formatter = new LongExtension();
        formatter.format(builder, new Long(11), "o");
        assertEquals("13", builder.toString());
        builder = new StringBuilder();
        formatter.format(builder, new Long(11), "5x");
        assertEquals("    b", builder.toString());
    }

    @Test(expected = FormatterException.class)
    public void testStringFormatterNullPointerExtension() {
        new StringFormatter().addToListOfExtensions(null);
    }

    @Test(expected = FormatterException.class)
    public void testStringFormatterIncorrectPatternFormat() {
        new StringFormatter().format("something0}");
    }

    @Test(expected = FormatterException.class)
    public void testStringFormatterNotDigit() {
        new StringFormatter().format("{a0:d}", new Long(11));
    }

    @Test(expected = FormatterException.class)
    public void testStringFormatterUnsupportedExtensionInPattern() {
        new StringFormatter().format("{0:d} something", new Long(11));
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
        new StringFormatter().format("{.field} something", new TestClass());
    }

    @Test(expected = FormatterException.class)
    public void testStringFormatterIncorrectFormatWithFormat() {
        new StringFormatter().format("{:2d} something", new Long(11));
    }

    @Test
    public void testStringFormatter() {
        StringFormatter formatter = new StringFormatter();
        assertEquals("first text {second text} third text",
                formatter.format("first text {{second text}} third text"));
        assertEquals("Example: one {1} 1",
                formatter.format("Example: {0} {{1}} {1}", "one", 1));
        assertEquals("Class class java.util.Calendar",
                formatter.format("Class {0}", Calendar.class));
        assertEquals("string content + 100", formatter.format(
                "{0.fieldString} + {0.fieldInt}", new TestClass()));
        assertEquals("string content", formatter.format(
                "{0.testClass.fieldString}", new TestChildClass()));
        assertEquals("string content",
                formatter.format("{0.fieldString}", new TestChildClass()));
        formatter.addToListOfExtensions(new CalendarExtension());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 1234);
        calendar.set(Calendar.MONTH, 9);
        calendar.set(Calendar.DAY_OF_MONTH, 12);
        assertEquals("1234.10.12", formatter.format("{0:yyyy.MM.dd}", calendar));
        formatter.addToListOfExtensions(new LongExtension());
        assertEquals("64", formatter.format("{0:x}", new Long(100)));
    }
}