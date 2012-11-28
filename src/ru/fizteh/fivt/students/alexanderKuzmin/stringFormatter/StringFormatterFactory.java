package ru.fizteh.fivt.students.alexanderKuzmin.stringFormatter

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

/**
 * @author Kuzmin A. group 196 Class StringFormatterFactory.
 * 
 */

public class StringFormatterFactory implements
        ru.fizteh.fivt.format.StringFormatterFactory {

    @Override
    public StringFormatter create(String... extensionClassNames)
            throws FormatterException {
        ArrayList<StringFormatterExtension> extension = new ArrayList<StringFormatterExtension>();
        try {
            Collections.sort(Arrays.asList(extensionClassNames));
        } catch (Throwable e) {
            throw new FormatterException("NULL extension.", e);
        }
        for (int i = 0; i < extensionClassNames.length; i++) {
            String currentClassName = extensionClassNames[i];
            if (extensionClassNames[i] == null) {
                throw new FormatterException("Incorrect class name.");
            }
            if (i != 0 && extensionClassNames[i - 1].equals(currentClassName)) {
                throw new FormatterException("The classes are duplicate.");
            }
            try {
                extension.add((StringFormatterExtension) Class.forName(
                        currentClassName).newInstance());
            } catch (FormatterException e) {
                throw e;
            } catch (Exception ex) {
                throw new FormatterException(
                        "Can't create a formatter with this extensions.", ex);
            }
        }
        return new StringFormatter(extension);
    }
}