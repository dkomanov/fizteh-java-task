package ru.fizteh.fivt.students.mysinYurii.stringFormatter;

import java.util.Formatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;
import ru.fizteh.fivt.students.mysinYurii.*;

public class IntegerExtention extends StringFormatterExtension {
    public IntegerExtention() {
        super(Integer.class);
    }
    
    public void format(StringBuilder result, Object tempObject, String formatPattern) throws FormatterException {
        if (formatPattern == null) {
            throw new FormatterException("Pattern is null");
        }
        if (formatPattern.length() == 0) {
            throw new FormatterException("Empty pattern");
        }
        Formatter integerFormatter = null;
        try {
            integerFormatter = new Formatter();
            result.append(integerFormatter.format("%" + formatPattern, tempObject));
            ObjectCloser.close(integerFormatter);
        } catch (Throwable e) {
            ObjectCloser.close(integerFormatter);
            throw new FormatterException("Wrong pattern: " + formatPattern);
        }
    }
}
