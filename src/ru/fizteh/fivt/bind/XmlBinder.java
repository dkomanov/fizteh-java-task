package ru.fizteh.fivt.bind;

/**
 * Наследники класса должны быть потокобезопасными.
 *
 * @author Dmitriy Komanov (dkomanov@ya.ru)
 */
public abstract class XmlBinder<T> {

    private final Class<T> clazz;

    protected XmlBinder(Class<T> clazz) {
        this.clazz = clazz;
    }

    protected final Class<T> getClazz() {
        return clazz;
    }

    public abstract byte[] serialize(T value);

    public abstract T deserialize(byte[] bytes);
}
