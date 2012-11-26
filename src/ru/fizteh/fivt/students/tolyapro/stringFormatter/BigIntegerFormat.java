package ru.fizteh.fivt.students.tolyapro.stringFormatter;

import java.math.BigInteger;
import java.util.Formatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class BigIntegerFormat extends StringFormatterExtension {

    protected BigIntegerFormat() {
        super(BigInteger.class);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void format(StringBuilder buffer, Object o, String pattern) {
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
