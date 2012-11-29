package ru.fizteh.fivt.students.verytable.formatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class DateExtension extends StringFormatterExtension{

    public DateExtension() {
        super(Date.class);
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
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            sb.append(formatter.format(o));
        } catch (Exception ex) {
            throw new FormatterException("Error: invalid pattern.", ex);
        }
    }
}
