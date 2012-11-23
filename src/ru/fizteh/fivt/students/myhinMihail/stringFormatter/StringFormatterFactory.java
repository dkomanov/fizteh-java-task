package ru.fizteh.fivt.students.myhinMihail.stringFormatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class StringFormatterFactory implements ru.fizteh.fivt.format.StringFormatterFactory {

    public StringFormatter create(String... extensionClassNames) {
        try {
            StringFormatter stringFormat = new StringFormatter();
            for (String className : extensionClassNames) {
                stringFormat.addToExtensions((StringFormatterExtension)Class.forName(className).newInstance());
            }
            return stringFormat;
            
        } catch (Exception expt) {
            throw new FormatterException("Bad extension for formatter");
        }
    }
    
}
