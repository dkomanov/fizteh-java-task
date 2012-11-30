package ru.fizteh.fivt.students.tolyapro.stringFormatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class StringFormatterFactory implements
        ru.fizteh.fivt.format.StringFormatterFactory {

    @Override
    public ru.fizteh.fivt.students.tolyapro.stringFormatter.StringFormatter create(
            String... extensionClassNames) throws FormatterException {
        try {
            ru.fizteh.fivt.students.tolyapro.stringFormatter.StringFormatter stringFormatter = new ru.fizteh.fivt.students.tolyapro.stringFormatter.StringFormatter();
            for (String className : extensionClassNames) {
                if (className == null) {
                    throw new Exception("Bad classname");
                }
                stringFormatter
                        .addToExtensions((StringFormatterExtension) Class
                                .forName(className).newInstance());
            }
            return stringFormatter;

        } catch (Exception e) {
            throw new FormatterException("Extension is not supported", e);
        }
    }

}
