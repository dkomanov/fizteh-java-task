package ru.fizteh.fivt.students.verytable.formatter;

import java.util.Formatter;
import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class IntegerExtension extends StringFormatterExtension{

    public IntegerExtension() {
        super(Integer.class);
    }

    @Override
    public void format(StringBuilder sb, Object o, String pattern)
                       throws FormatterException {

        if (sb == null) {
            throw new FormatterException("Error: empty buffer.");
        }
        if (o == null) {
            throw new FormatterException("Error: empty object");
        }
        if (pattern == null) {
            throw new FormatterException("Error: empty pattern.");
        }

        try {
            Formatter formatter = new Formatter();
            sb.append(formatter.format("%" + pattern, o));
        } catch (Exception ex) {
            throw new FormatterException("Error: invalid pattern.");
        }
    }
}
