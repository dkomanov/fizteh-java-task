package ru.fizteh.fivt.students.konstantinPavlov.stringFormatter;

import java.util.Formatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class LongExtension extends StringFormatterExtension {
    LongExtension() {
        super(Long.class);
    }

    @Override
    public void format(StringBuilder buffer, Object object, String pattern)
            throws FormatterException {
        if (buffer == null) {
            throw new FormatterException("buffer is null");
        }
        if (pattern == null) {
            throw new FormatterException("pattern is null");
        }
        if (object == null) {
            throw new FormatterException("object is null");
        }
        try {
            if (object != null
                    && !Long.class.isAssignableFrom(object.getClass())) {
                throw new FormatterException("incorrect object type");
            }
            Formatter formatter = new Formatter().format("%" + pattern, object);
            buffer.append(formatter.toString());
        } catch (Throwable t) {
            throw new FormatterException(t.getMessage(), t);
        }
    }
}