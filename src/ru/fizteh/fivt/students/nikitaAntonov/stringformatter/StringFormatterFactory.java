package ru.fizteh.fivt.students.nikitaAntonov.stringformatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatter;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class StringFormatterFactory implements
        ru.fizteh.fivt.format.StringFormatterFactory {
    private Map<String, StringFormatterExtension> knownsExtensions;

    public StringFormatterFactory() {
        knownsExtensions = Collections
                .synchronizedMap(new HashMap<String, StringFormatterExtension>());
    }

    @Override
    public StringFormatter create(String... extensionClassNames)
            throws FormatterException {

        ArrayList<StringFormatterExtension> extensionsList = new ArrayList<>();

        try {
            for (String extensionName : extensionClassNames) {
                if (extensionName == null) {
                    throw new NullPointerException();
                }

                StringFormatterExtension extension = knownsExtensions
                        .get(extensionName);

                if (extension == null) {
                    extension = (StringFormatterExtension) Class.forName(
                            extensionName).newInstance();
                    
                    knownsExtensions.put(extensionName, extension);
                }

                extensionsList.add(extension);
            }
        } catch (Throwable e) {
            throw new FormatterException("Incorrect extension", e);
        }

        return new StringFormatter(extensionsList);
    }
}
