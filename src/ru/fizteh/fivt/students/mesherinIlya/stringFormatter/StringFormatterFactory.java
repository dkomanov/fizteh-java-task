package ru.fizteh.fivt.students.mesherinIlya.stringFormatter;


import ru.fizteh.fivt.format.StringFormatterExtension;
import ru.fizteh.fivt.format.FormatterException;

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
        } catch (Exception e) {
            throw new FormatterException("Cannot create formatter with these extensions.", e);
        }
    }
}
