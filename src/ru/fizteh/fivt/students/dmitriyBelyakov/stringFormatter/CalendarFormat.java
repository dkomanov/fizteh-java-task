package ru.fizteh.fivt.students.dmitriyBelyakov.stringFormatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarFormat extends StringFormatterExtension {
    CalendarFormat() {
        super(Calendar.class);
    }

    @Override
    public void format(StringBuilder buffer, Object object, String pattern) throws FormatterException {
        try {
            if (pattern.equals("")) {
                throw new FormatterException("Empty pattern.", new Throwable());
            }
            if (buffer == null) {
                throw new FormatterException("Buffer is null.", new Throwable());
            }
            if (pattern == null) {
                throw new FormatterException("Pattern is null.", new Throwable());
            }
            if (object == null) {
                throw new FormatterException("Calendar is null.", new Throwable());
            }
            if (!Calendar.class.isAssignableFrom(object.getClass())) {
                throw new FormatterException("Incorrect object type.", new Throwable());
            }
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            buffer.append(formatter.format(((Calendar) object).getTime()));
        } catch (Throwable t) {
            throw new FormatterException(t.getMessage(), t);
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return this.clazz.isAssignableFrom(clazz);
    }
}
