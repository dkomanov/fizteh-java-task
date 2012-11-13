package ru.fizteh.fivt.students.kashinYana.stringFormator;

import ru.fizteh.fivt.format.StringFormatterExtension;

import java.util.Formatter;

public class StringFormatterIntegerExtension
        extends StringFormatterExtension {

    protected StringFormatterIntegerExtension() {
        super(Integer.class);
    }

    @Override
    public void format(StringBuilder buffer, Object o, String pattern) {
        Formatter formatter = new Formatter();
        buffer.append(formatter.format("%" + pattern + "d", o));
    }
}
