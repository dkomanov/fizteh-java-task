package ru.fizteh.fivt.format;

/**
 * @author Dmitriy Komanov (dkomanov@ya.ru)
 */
public abstract class StringFormatterExtension {

    private final Class<?> clazz;

    protected StringFormatterExtension(Class<?> clazz) {
        this.clazz = clazz;
    }

    public final boolean supports(Class<?> clazz) {
        return this.clazz.equals(clazz);
    }

    public abstract void format(StringBuilder buffer, Object o, String pattern);
}
