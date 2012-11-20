package ru.fizteh.fivt.students.myhinMihail.stringFormatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

import java.util.Formatter;

public class DoubleExtension extends StringFormatterExtension {

    DoubleExtension() {
        super(Double.class);
    }

    public void format(StringBuilder buffer, Object o, String pattern) {
        try {
            if (pattern == null || pattern.length() == 0) {
                throw new FormatterException("Bad pattern");
            }
            
            Formatter formatter = new Formatter();
            buffer.append(formatter.format("%" + pattern, o));
            formatter.close();
        } catch (Exception ex) {
            throw new FormatterException("Bad pattern");
        }
    }
}