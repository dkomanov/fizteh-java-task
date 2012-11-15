package ru.fizteh.fivt.students.harius.formatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;
import java.math.BigInteger;
import java.util.Formatter;

public class StringFormatterBigIntegerExtension
    extends StringFormatterExtension {

    private Formatter formatter = new Formatter();

    protected StringFormatterBigIntegerExtension() {
        super(BigInteger.class);
    }

    @Override
    public void format(StringBuilder buffer, Object o, String pattern) {
        buffer.append(formatter.format("%" + pattern + "d", o));
    }

}