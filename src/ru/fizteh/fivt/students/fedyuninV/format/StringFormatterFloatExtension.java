package ru.fizteh.fivt.students.fedyuninV.format;

import ru.fizteh.fivt.format.StringFormatterExtension;

import java.util.Formatter;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class StringFormatterFloatExtension extends StringFormatterExtension{
    private Formatter formatter;

    protected StringFormatterFloatExtension() {
        super(Float.class);
        formatter = new Formatter();
    }

    public void format(StringBuilder buffer, Object o, String pattern) {
        buffer.append(formatter.format("%" + pattern + "f", o));
    }
}
