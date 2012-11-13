package ru.fizteh.fivt.students.fedyuninV.format;

import ru.fizteh.fivt.format.StringFormatterExtension;

import java.util.Formatter;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class StringFormatterByteArrayExtension extends StringFormatterExtension{
    protected StringFormatterByteArrayExtension() {
        super(byte[].class);
    }

    public void format(StringBuilder buffer, Object o, String pattern) {
        buffer.append(String.format("%" + pattern + "s", o.toString()));
    }
}
