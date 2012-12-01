package ru.fizteh.fivt.students.frolovNikolay.stringFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class DateExtension extends StringFormatterExtension {
    
    public DateExtension() {
        super(Date.class);
    }

    @Override
    public void format(StringBuilder buffer, Object obj, String pattern) throws FormatterException {
        if (pattern == null || pattern.isEmpty()) {
            throw new FormatterException("Bad pattern");
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            buffer.append(formatter.format(obj));
        } catch (Throwable exception) {
            throw new FormatterException("Bad pattern", exception);
        }    
    }
}