package ru.fizteh.fivt.students.mesherinIlya.stringFormatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;
    
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarFormatter extends StringFormatterExtension {

    public CalendarFormatter() {
        super(Calendar.class);
    }

    @Override
    public void format(StringBuilder buffer, Object object, String pattern) throws FormatterException {
        if (pattern.equals("")) {
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
        if (!supports(object.getClass())) {
            throw new FormatterException("Incorrect object type.");
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            buffer.append(formatter.format(((Calendar) object).getTime()));
        } catch (Exception e) {
            throw new FormatterException(e.getMessage(), e);
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return this.clazz.isAssignableFrom(clazz);
    }
}
