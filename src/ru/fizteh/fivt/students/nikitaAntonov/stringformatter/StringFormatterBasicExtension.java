package ru.fizteh.fivt.students.nikitaAntonov.stringformatter;

import java.util.Formatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;
import ru.fizteh.fivt.students.nikitaAntonov.utils.Utils;

public class StringFormatterBasicExtension extends StringFormatterExtension {
    protected StringFormatterBasicExtension(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public void format(StringBuilder buffer, Object o, String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            throw new FormatterException("Pattern must be non empty string");
        }

        if (buffer == null) {
            throw new FormatterException("Buffer == null");
        }

        if (o == null) {
            throw new FormatterException("Null isn't correct object to format");
        }

        Formatter f = null;

        try {
            f = new Formatter();
            buffer.append(f.format("%" + pattern, o));
        } catch (Throwable e) {
            throw new FormatterException("Incorrect pattern: \"" + pattern
                    + "\"", e);
        } finally {
            Utils.closeResource(f);
        }
    }

}
