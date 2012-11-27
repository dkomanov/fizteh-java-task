package ru.fizteh.fivt.students.alexanderKuzmin.stringFormatter

import java.math.BigInteger;
import java.util.Formatter;
import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

/**
 * @author Kuzmin A. group 196 Class StringFormatterExtensionBigInteger.
 * 
 */

public class StringFormatterExtensionBigInteger extends
        StringFormatterExtension {

    public StringFormatterExtensionBigInteger() {
        super(BigInteger.class);
    }
    
    @Override
    public void format(StringBuilder buffer, Object o, String pattern)
            throws FormatterException {
        try {
            if (pattern == null || pattern.length() == 0) {
                throw new FormatterException("NULL pattern.");
            }
            if (buffer == null) {
                throw new FormatterException("NULL buffer.");
            }
            if (o == null) {
                throw new FormatterException("NULL object.");
            }
            Formatter format = new Formatter();
            buffer.append(format.format("%" + pattern, o));
            format.close();
        } catch (Throwable e) {
            throw new FormatterException(e.getMessage());
        }
    }
}
