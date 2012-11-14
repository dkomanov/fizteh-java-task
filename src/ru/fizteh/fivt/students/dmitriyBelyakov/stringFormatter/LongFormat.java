package ru.fizteh.fivt.students.dmitriyBelyakov.stringFormatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

import java.util.Formatter;

public class LongFormat extends StringFormatterExtension {
    LongFormat() {
        super(Long.class);
    }

    @Override
    public void format(StringBuilder buffer, Object object, String pattern) throws FormatterException {
        if (buffer == null) {
            throw new FormatterException("Buffer is null.");
        }
        if (pattern == null) {
            throw new FormatterException("Pattern is null.");
        }
        if (object == null) {
            throw new FormatterException("Object is null.");
        }
        try {
            if (object != null && !Long.class.isAssignableFrom(object.getClass())) {
                throw new FormatterException("Incorrect object type.");
            }
            Formatter formatter = new Formatter().format("%" + pattern, object);
            buffer.append(formatter.toString());
        } catch (Throwable t) {
            throw new FormatterException(t.getMessage());
        }
    }
}
