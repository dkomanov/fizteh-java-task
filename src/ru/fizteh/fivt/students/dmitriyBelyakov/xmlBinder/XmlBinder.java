package ru.fizteh.fivt.students.dmitriyBelyakov.xmlBinder;

public class XmlBinder<T> extends ru.fizteh.fivt.bind.XmlBinder<T> {
    XmlBinder(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public byte[] serialize(T value) {
        return null;
    }

    @Override
    public T deserialize(byte[] bytes) {
        return null;
    }
}
