package ru.fizteh.fivt.students.tolyapro.stringFormatter;

import java.util.Formatter;

import ru.fizteh.fivt.format.FormatterException;

public class DoubleFormatter extends
        ru.fizteh.fivt.format.StringFormatterExtension {

    protected DoubleFormatter() {
        super(Double.class);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void format(StringBuilder buffer, Object o, String pattern) {
        // TODO Auto-generated method stub
        if (pattern == null) {
            throw new FormatterException("null pattern");
        }
        try {
            Formatter formatter = new Formatter();
            buffer.append(formatter.format("%" + pattern, o));
            formatter.close();
        } catch (Exception e) {
            throw new FormatterException("Bad pattern");
        }
    }

}
