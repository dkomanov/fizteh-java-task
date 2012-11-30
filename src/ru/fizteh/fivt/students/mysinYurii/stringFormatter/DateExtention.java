package ru.fizteh.fivt.students.mysinYurii.stringFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class DateExtention extends StringFormatterExtension {
    DateExtention() {
        super(Date.class);
    }
    
    public void format(StringBuilder result, Object tempObject, String formatPattern) throws FormatterException {
        if (formatPattern == null) {
            throw new FormatterException("Pattern is null");
        }
        if (formatPattern.length() == 0) {
            throw new FormatterException("Empty pattern");
        }
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(formatPattern);
            result.append(dateFormatter.format(tempObject));
        } catch (Throwable e) {
            throw new FormatterException("Wrong pattern: " + formatPattern);
        }
    }
}
