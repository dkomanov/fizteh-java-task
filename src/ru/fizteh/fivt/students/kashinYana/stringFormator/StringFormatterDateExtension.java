package ru.fizteh.fivt.students.kashinYana.stringFormator;

import ru.fizteh.fivt.format.StringFormatterExtension;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringFormatterDateExtension
        extends StringFormatterExtension {

    protected StringFormatterDateExtension() {
        super(Date.class);
    }

    @Override
    public void format(StringBuilder buffer, Object object, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        buffer.append(formatter.format(object));
    }
}