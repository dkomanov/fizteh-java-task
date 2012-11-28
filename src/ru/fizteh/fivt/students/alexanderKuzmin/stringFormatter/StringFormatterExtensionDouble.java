package ru.fizteh.fivt.students.alexanderKuzmin.stringFormatter

import java.util.Formatter;
import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

/**
 * @author Kuzmin A. group 196 Class StringFormatterExtensionDouble.
 * 
 */

public class StringFormatterExtensionDouble extends StringFormatterExtension {

    public StringFormatterExtensionDouble() {
        super(Double.class);
    }

    @Override
    public void format(StringBuilder buffer, Object o, String pattern)
            throws FormatterException {
        try {
            if (pattern == null || pattern.length() == 0) {
                throw new Exception("NULL pattern.");
            }
            if (buffer == null) {
                throw new Exception("NULL buffer.");
            }
            if (o == null) {
                throw new Exception("NULL object.");
            }
            Formatter format = new Formatter();
            buffer.append(format.format("%" + pattern, o));
            format.close();
        } catch (Throwable e) {
            throw new FormatterException(e.getMessage(), e);
        }
    }
}