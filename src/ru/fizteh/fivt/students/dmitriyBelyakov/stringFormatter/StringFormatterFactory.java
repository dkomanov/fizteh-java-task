package ru.fizteh.fivt.students.dmitriyBelyakov.stringFormatter;

public class StringFormatterFactory implements ru.fizteh.fivt.format.StringFormatterFactory {
    @Override
    public StringFormatter create(String... extensionClassNames) {
        // TODO
        return new StringFormatter();
    }
}