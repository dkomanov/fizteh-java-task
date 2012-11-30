package ru.fizteh.fivt.students.konstantinPavlov.stringFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class CalendarExtension extends StringFormatterExtension {
    public CalendarExtension() {
        super(Calendar.class);
    }

    @Override
    public void format(StringBuilder buffer, Object object, String pattern)
            throws FormatterException {
        
        if (pattern == null) {
            throw new FormatterException("pattern is null");
        }
        if (pattern.equals("")) {
            throw new FormatterException("empty pattern");
        }
        if (buffer == null) {
            throw new FormatterException("buffer is null");
        }
        if (object == null) {
            throw new FormatterException("calendar is null");
        }

        try {
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