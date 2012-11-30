package ru.fizteh.fivt.students.konstantinPavlov.stringFormatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class StringFormatterFactory implements
        ru.fizteh.fivt.format.StringFormatterFactory {

    @Override
    public StringFormatter create(String... extensions) {
        try {
            StringFormatter stringFormat = new StringFormatter();
            for (String extensionClassName : extensions) {
                stringFormat
                        .addToListOfExtensions((StringFormatterExtension) Class
                                .forName(extensionClassName).newInstance());
            }
            return stringFormat;

        } catch (Exception e) {
            throw new FormatterException("bad extension for formatter");
        }
    }

}