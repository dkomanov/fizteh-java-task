package ru.fizteh.fivt.students.dmitriyBelyakov.stringFormatter;

public abstract class StringFormatterExtension {

    private final Class<?> clazz;

    protected StringFormatterExtension(Class<?> clazz) {
        this.clazz = clazz;
    }

    public final boolean supports(Class<?> clazz) {
        return this.clazz.isAssignableFrom(clazz);
    }

    public abstract void format(StringBuilder buffer, Object o, String pattern);
}
