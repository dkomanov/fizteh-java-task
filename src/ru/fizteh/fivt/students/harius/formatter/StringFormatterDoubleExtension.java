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

    private Formatter formatter = new Formatter();

    /* Constructor */
    protected StringFormatterDoubleExtension() {
        super(Double.class);
    }

    /* Format the given Double */
    @Override
    public void format(StringBuilder buffer, Object o, String pattern) {
        buffer.append(formatter.format("%" + pattern, o));
    }
    
}