package ru.fizteh.fivt.students.yuliaNikonova.stringFormatter;

import java.util.Formatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class DoubleExtension extends StringFormatterExtension {

    public DoubleExtension() {
        super(Double.class);

    }

    @Override
    public void format(StringBuilder buffer, Object o, String pattern) {
        if (pattern == null || pattern.length() == 0) {
            throw new FormatterException("wrong pattern");
        }
        try {
            Formatter formatter = new Formatter();

            buffer.append(formatter.format("%" + pattern, o));

            formatter.close();
        } catch (Exception e) {
            throw new FormatterException("wrong pattern", e);
        }
    }

}
