package ru.fizteh.fivt.students.yuliaNikonova.stringFormatter;

import java.math.BigInteger;
import java.util.Formatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class BigIntegerExtension extends StringFormatterExtension {

    public BigIntegerExtension() {
        super(BigInteger.class);

    }

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
