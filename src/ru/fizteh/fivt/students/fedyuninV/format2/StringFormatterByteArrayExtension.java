package ru.fizteh.fivt.students.fedyuninV.format2;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

import java.util.Arrays;


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
            if (pattern.length() != 0) {
                throw new FormatterException("Incorrect pattern");
            }
            buffer.append(String.format("%" + pattern + "s", Arrays.toString((byte[]) o)));
        } catch (Exception ex) {
            throw new FormatterException("Incorrect pattern");
        }
    }
}
