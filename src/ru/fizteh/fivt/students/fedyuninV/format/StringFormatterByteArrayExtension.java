package ru.fizteh.fivt.students.fedyuninV.format;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

import java.util.UnknownFormatConversionException;


/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class StringFormatterByteArrayExtension extends StringFormatterExtension{
    protected StringFormatterByteArrayExtension() {
        super(byte[].class);
    }

    public void format(StringBuilder buffer, Object o, String pattern) {
        try {
            buffer.append(String.format("%" + pattern + "s", o.toString()));
        } catch (UnknownFormatConversionException ex) {
            throw new FormatterException("Incorrect pattern");
        }
    }
}
