package ru.fizteh.fivt.students.kashinYana.stringFormator;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class StringFormatterFactory
        implements ru.fizteh.fivt.format.StringFormatterFactory {

    @Override
    public StringFormatter create(String... classNames)
            throws FormatterException {

        ArrayList<StringFormatterExtension> extension = new ArrayList<StringFormatterExtension>();
        Collections.sort(Arrays.asList(classNames));
        for (int i = 0; i < classNames.length; i++) {
            String currentClassName = classNames[i];
            if (i != 0  &&  classNames[i - 1].equals(currentClassName)) {
                throw new FormatterException("I found 2 equals files.");
            }
            try {
                extension.add((StringFormatterExtension)Class.forName(currentClassName).newInstance());
            } catch (Exception ex) {
                throw new FormatterException("I don't work with this class.");
            }
        }
        return new StringFormatter(extension);
   }
}