package ru.fizteh.fivt.students.fedyuninV.format;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;


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
            buffer.append(String.format("%" + pattern, o.toString()));
        } catch (Exception ex) {
            throw new FormatterException("Incorrect pattern");
        }
    }
}
