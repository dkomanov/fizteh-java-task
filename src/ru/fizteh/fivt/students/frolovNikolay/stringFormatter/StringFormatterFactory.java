package ru.fizteh.fivt.students.frolovNikolay.stringFormatter;

import java.util.Arrays;
import java.util.Collections;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class StringFormatterFactory implements ru.fizteh.fivt.format.StringFormatterFactory {
    
    @Override
    public StringFormatter create(String... extensionClassNames)
            throws FormatterException {
        StringFormatter producedFormatter = new StringFormatter();
        if (extensionClassNames.length == 0) {
            return producedFormatter;
        } else {
            Collections.sort(Arrays.asList(extensionClassNames));
            String last = extensionClassNames[0];
            try {
                producedFormatter.addExtension((StringFormatterExtension) Class.forName(extensionClassNames[0]).newInstance());
            } catch (Throwable exception) {
                throw new FormatterException("Can't create StringFormatter with these extensions", exception);
            }
            for (int i = 1; i < extensionClassNames.length; ++i) {
                if (extensionClassNames[i] == last) {
                    throw new FormatterException("Have same classes: " + extensionClassNames[i]);
                } else {
                    try {
                        producedFormatter.addExtension((StringFormatterExtension) Class.forName(extensionClassNames[i]).newInstance());
                    } catch (Throwable exception) {
                        throw new FormatterException("Can't create StringFormatter with these extensions", exception);
                    }
                }
            }
            return producedFormatter;
        }   
    }
}
