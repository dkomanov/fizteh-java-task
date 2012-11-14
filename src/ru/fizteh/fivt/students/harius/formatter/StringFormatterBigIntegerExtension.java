/*
 * StringFormatterBigIntegerExtension.java
 * Nov 14, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.formatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;
import java.math.BigInteger;
import java.util.Formatter;

/*
 * An extension for formatting BigInteger
 */
public class StringFormatterBigIntegerExtension
    extends StringFormatterExtension {

    private Formatter formatter = new Formatter();
    private final String conv = "bBhHsSdoxX";

    /* Constructor */
    protected StringFormatterBigIntegerExtension() {
        super(BigInteger.class);
    }

    /* Format the given BigInteger */
    @Override
    public void format(StringBuilder buffer, Object o, String pattern) {
        if (!pattern.matches(".*[" + conv + "]")) {
            throw new FormatterException("Pattern for BigInteger must end with one of " + conv);
        }
        buffer.append(formatter.format("%" + pattern, o));
    }

}