package ru.fizteh.fivt.students.mesherinIlya.stringFormatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

import java.util.Formatter;

public class LongFormatter extends StringFormatterExtension {
    
    LongFormatter() {
        super(Long.class);
    }

    @Override
    public void format(StringBuilder buffer, Object object, String pattern) throws FormatterException {
        try {
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
                throw new FormatterException("Object is null.");
            }
            if (!Long.class.isAssignableFrom(object.getClass())) {
                throw new FormatterException("Incorrect object type.");
            }
            Formatter formatter = new Formatter().format("%" + pattern, object);
            buffer.append(formatter.toString());
        } catch (Exception e) {
            throw new FormatterException(e.getMessage(), e);
        }
    }
}
