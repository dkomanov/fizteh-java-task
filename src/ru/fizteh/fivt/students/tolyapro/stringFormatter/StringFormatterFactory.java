package ru.fizteh.fivt.students.tolyapro.stringFormatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class StringFormatterFactory implements
        ru.fizteh.fivt.format.StringFormatterFactory {

    @Override
    public ru.fizteh.fivt.students.tolyapro.stringFormatter.StringFormatter create(
            String... extensionClassNames) throws FormatterException {

        ru.fizteh.fivt.students.tolyapro.stringFormatter.StringFormatter stringFormatter = new ru.fizteh.fivt.students.tolyapro.stringFormatter.StringFormatter();
        for (String className : extensionClassNames) {
            if (className == null) {
                throw new FormatterException("Bad classname");
            }

            try {
                StringFormatterExtension ext = (StringFormatterExtension) Class
                        .forName(className).newInstance();
                stringFormatter.addToExtensions(ext);
            } catch (ClassNotFoundException e) {
                throw new FormatterException("Extension is not supported: "
                        + className, e);
            } catch (Exception e) {
                throw new FormatterException(e);
            }
        }
        return stringFormatter;

    }
}
