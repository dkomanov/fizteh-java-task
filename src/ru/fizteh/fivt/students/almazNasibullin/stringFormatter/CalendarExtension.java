package ru.fizteh.fivt.students.almazNasibullin.stringFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

/**
 * 19.11.12
 * @author almaz
 */

public class CalendarExtension extends StringFormatterExtension {
    CalendarExtension() {
        super(Calendar.class);
    }

    @Override
    public void format(StringBuilder buffer, Object o, String pattern)
            throws FormatterException {
        if (pattern == null || pattern.isEmpty()) {
            throw new FormatterException("Bad pattern");
        }
        if (buffer == null) {
            throw new FormatterException("Bad buffer");
        }
        if (o == null) {
            throw new FormatterException("Bad object");
        }
        if (!Calendar.class.isAssignableFrom(o.getClass())) {
            throw new FormatterException("Bad object");
        }

        SimpleDateFormat sdf = null;
        try {
            sdf = new SimpleDateFormat(pattern);
        } catch (Exception e) {
            throw new FormatterException("Bad pattern");
        }
        try {
            buffer.append(sdf.format(((Calendar)o).getTime()));
        } catch (Exception e) {
            throw new FormatterException("Bad object");
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return this.clazz.isAssignableFrom(clazz);
    }
}
