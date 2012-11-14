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
            switch (pattern.charAt(pattern.length() - 1)) {
                case 'f':
                case 'e':
                    buffer.append(formatter.format("%" + pattern, o));
                    break;
                default:
                    throw new FormatterException("Incorrect pattern");
            }
        } catch (Exception ex) {
            throw new FormatterException("Incorrect pattern");
        }
    }
}
