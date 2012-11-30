package ru.fizteh.fivt.students.frolovNikolay.stringFormatter;

import ru.fizteh.fivt.students.frolovNikolay.Closer;
import java.util.Formatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class IntegerExtension extends StringFormatterExtension {
    
    IntegerExtension() {
        super(Integer.class);
    }

    @Override
    public void format(StringBuilder buffer, Object obj, String pattern) throws FormatterException {
        if (pattern == null || pattern.isEmpty()) {
            throw new FormatterException("Bad pattern");
        } else {
            Formatter formatter = null;
            try {
                formatter = new Formatter();
                buffer.append(formatter.format("%" + pattern, obj));
            } catch (Throwable exception) {
                Closer.close(formatter);
                throw new FormatterException("Bad pattern", exception);
            } finally {
                Closer.close(formatter);
            }
        }
    }
}
