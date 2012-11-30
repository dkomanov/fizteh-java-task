package ru.fizteh.fivt.students.altimin.formatter;

/**
 * @author Dmitriy Komanov (dkomanov@ya.ru)
 */
public abstract class StringFormatterExtension {

    protected final Class<?> _class;

    protected StringFormatterExtension(Class<?> _class) {
        this._class = _class;
    }

    public boolean supports(Class<?> clazz) {
        return this._class.equals(_class);
    }

    public abstract void format(StringBuilder buffer, Object object, String pattern);
}