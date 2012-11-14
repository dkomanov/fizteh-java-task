package ru.fizteh.fivt.students.fedyuninV.format;

import ru.fizteh.fivt.format.FormatterException;
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

    }

    public void format(StringBuilder buffer, Object o, String pattern) {
        formatter = new Formatter();
        try {
            if (pattern.lastIndexOf('f') == -1  &&  pattern.lastIndexOf('e') == -1) {
                throw new FormatterException("Incorrect pattern");
            }
            buffer.append(formatter.format("%" + pattern, o));
        } catch (Exception ex) {
            throw new FormatterException("Incorrect pattern");
        }
    }
}
