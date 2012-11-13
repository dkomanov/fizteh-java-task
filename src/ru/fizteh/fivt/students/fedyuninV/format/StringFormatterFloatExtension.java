package ru.fizteh.fivt.students.fedyuninV.format;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

import java.util.Formatter;
import java.util.UnknownFormatConversionException;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class StringFormatterFloatExtension extends StringFormatterExtension{
    private Formatter formatter;

    protected StringFormatterFloatExtension() {
        super(Float.class);

    }

    public void format(StringBuilder buffer, Object o, String pattern) {
        formatter = new Formatter();
        try {
            buffer.append(formatter.format("%" + pattern + "f", o));
        } catch (UnknownFormatConversionException ex) {
            throw new FormatterException("Incorrect pattern");
        }
    }
}
