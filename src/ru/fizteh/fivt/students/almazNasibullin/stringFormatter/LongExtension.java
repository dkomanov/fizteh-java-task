package ru.fizteh.fivt.students.almazNasibullin.stringFormatter;

import java.util.Formatter;
import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

/**
 * 19.11.12
 * @author almaz
 */

public class LongExtension extends StringFormatterExtension {

    LongExtension() {
        super(Long.class);
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
        if (!Long.class.isAssignableFrom(o.getClass())) {
            throw new FormatterException("Bad object");
        }

        try {
            Formatter f = new Formatter();
            buffer.append(f.format("%" + pattern, o));
        } catch (Exception e) {
            throw new FormatterException("Bad pattern");
        }
    }
}
