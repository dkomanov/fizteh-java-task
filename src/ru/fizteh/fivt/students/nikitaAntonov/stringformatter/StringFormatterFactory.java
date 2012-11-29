package ru.fizteh.fivt.students.nikitaAntonov.stringformatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.students.nikitaAntonov.stringformatter.StringFormatter;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class StringFormatterFactory implements
        ru.fizteh.fivt.format.StringFormatterFactory {
    private volatile Map<String, StringFormatterExtension> knownsExtensions;

    public StringFormatterFactory() {
        knownsExtensions = Collections
                .synchronizedMap(new HashMap<String, StringFormatterExtension>());
    }

    @Override
    public ru.fizteh.fivt.format.StringFormatter create(
            String... extensionClassNames) throws FormatterException {

        ArrayList<StringFormatterExtension> extensionsList = new ArrayList<>();

        for (String extensionName : extensionClassNames) {
            if (extensionName == null) {
                throw new FormatterException("Name of the extension class can't be null");
            }

            StringFormatterExtension extension = knownsExtensions
                    .get(extensionName);

            try {
                if (extension == null) {
                    synchronized (knownsExtensions) {

                        extension = knownsExtensions.get(extensionName);
                        if (extension == null) {

                            extension = (StringFormatterExtension) Class
                                    .forName(extensionName).newInstance();

                            knownsExtensions.put(extensionName, extension);
                        }
                    }
                }
            } catch (Throwable e) {
                throw new FormatterException("Can't create instance of "
                        + extensionName, e);
            }

            extensionsList.add(extension);
        }
        return new StringFormatter(extensionsList);
    }
}
