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
            if(pattern.equals("")) {
                throw new FormatterException("Empty pattern.");
            }
            if (buffer == null) {
                throw new FormatterException("Buffer is null.");
            }
            if (pattern == null) {
                throw new FormatterException("Pattern is null.");
            }
            if (object == null) {
                throw new FormatterException("Calendar is null.");
            }
            if (!Calendar.class.isAssignableFrom(object.getClass())) {
                throw new FormatterException("Incorrect object type.");
            }
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            buffer.append(formatter.format(((Calendar) object).getTime()));
        } catch (Throwable t) {
            throw new FormatterException(t.getMessage());
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return this.clazz.isAssignableFrom(clazz);
    }
}
