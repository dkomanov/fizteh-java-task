package ru.fizteh.fivt.students.altimin.formatter;

import ru.fizteh.fivt.format.StringFormatterExtension;

import java.util.Formatter;

/**
 * User: altimin
 * Date: 11/30/12
 * Time: 10:44 PM
 */
public class StringFormatterLongExtension extends StringFormatterExtension {
    public StringFormatterLongExtension() {
        super(Long.class);
    }

    @Override
    public void format(StringBuilder buffer, Object object, String pattern) {
        Formatter formatter = new Formatter();
        buffer.append(formatter.format("%" + pattern, object));
    }
}
