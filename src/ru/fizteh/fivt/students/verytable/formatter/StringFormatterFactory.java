package ru.fizteh.fivt.students.verytable.formatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class StringFormatterFactory implements ru.fizteh.fivt.format.StringFormatterFactory{

    @Override
    public StringFormatter create(String... classNames) throws FormatterException {

        ArrayList<StringFormatterExtension> extension = new ArrayList<StringFormatterExtension>();

        try {
            Collections.sort(Arrays.asList(classNames)); //to search identical
        } catch (Exception ex) {
            throw new FormatterException("Error: empty classNames.");
        }

        for (int i = 0; i < classNames.length; ++i) {
            String curName = classNames[i];
            if (classNames[i] == null) {
                throw new FormatterException("Error: invalid className.");
            }
            if (i != 0 && classNames[i - 1].equals(curName)) {
                throw new FormatterException("Error: to identical classNames: "
                                             + curName);
            }
            try {
                extension.add((StringFormatterExtension) Class.forName(curName).newInstance());
            } catch (Exception ex) {
                throw new FormatterException("Error: invalid extension.");
            }
        }
        return new StringFormatter(extension);
    }
}
