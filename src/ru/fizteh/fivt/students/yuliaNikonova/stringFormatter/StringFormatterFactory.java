package ru.fizteh.fivt.students.yuliaNikonova.stringFormatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class StringFormatterFactory implements ru.fizteh.fivt.format.StringFormatterFactory {

    public StringFormatter create(String... extensionClassNames) throws FormatterException {
        StringFormatter strFormatter = new StringFormatter();
        try {
            for (String className : extensionClassNames) {

                strFormatter.addExtension((StringFormatterExtension) Class.forName(className).newInstance());

            }
        } catch (Exception e) {
            throw new FormatterException("Bad extension");
        }
        return strFormatter;

    }

}
