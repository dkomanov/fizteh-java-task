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
    public void format(StringBuilder buffer, Object object, String pattern) throws FormatterException{
        try {
            if (object == null) {
                throw new FormatterException("Null pointer.");
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
}