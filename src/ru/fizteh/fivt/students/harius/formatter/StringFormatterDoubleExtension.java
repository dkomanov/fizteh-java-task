package ru.fizteh.fivt.students.harius.formatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;
import java.util.Formatter;

public class StringFormatterDoubleExtension
    extends StringFormatterExtension {

    private Formatter formatter = new Formatter();

    protected StringFormatterDoubleExtension() {
        super(Double.class);
    }

    @Override
    public void format(StringBuilder buffer, Object o, String pattern) {
        buffer.append(formatter.format("%" + pattern + "f", o));
    }
    
}