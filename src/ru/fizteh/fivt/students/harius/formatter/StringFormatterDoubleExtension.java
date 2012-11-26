/*
 * StringFormatterDoubleExtension.java
 * Nov 14, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.formatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;
import java.util.Formatter;

/*
 * An extension for formatting Double
 */
public class StringFormatterDoubleExtension
    extends StringFormatterExtension {

    private Formatter formatter;
    private final String conv = "bBhHsSeEfgGaA";

    /* Constructor */
    protected StringFormatterDoubleExtension() {
        super(Double.class);
    }

    /* Format the given Double */
    @Override
    public void format(StringBuilder buffer, Object o, String pattern) {
        formatter = new Formatter();
        if (pattern == null) {
            throw new FormatterException("Null pattern string");
        }
        if (!pattern.matches(".*[" + conv + "]")) {
            throw new FormatterException("Pattern for Double must end with one of " + conv);
        }
        try {
            buffer.append(formatter.format("%" + pattern, o));
        } catch (Exception ex) {
            throw new FormatterException("Error while formatting Double: " + ex.getMessage());
        }
    }
    
}