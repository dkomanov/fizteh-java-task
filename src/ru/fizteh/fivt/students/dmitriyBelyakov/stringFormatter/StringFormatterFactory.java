package ru.fizteh.fivt.students.dmitriyBelyakov.stringFormatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class StringFormatterFactory implements ru.fizteh.fivt.format.StringFormatterFactory {
    @Override
    public StringFormatter create(String... extensionClassNames) {
        try {
            StringFormatter formatter = new StringFormatter();
            for (String extensionClass : extensionClassNames) {
                StringFormatterExtension extension = (StringFormatterExtension) Class.forName(extensionClass).newInstance();
                formatter.addExtension(extension);
            }
            return formatter;
        } catch (Throwable t) {
            throw new FormatterException("Cannot create formatter with this extensions.");
        }
    }
}
