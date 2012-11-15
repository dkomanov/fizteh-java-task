package ru.fizteh.fivt.students.kashinYana.stringFormator;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

import java.util.ArrayList;

public class StringFormatterFactory
        implements ru.fizteh.fivt.format.StringFormatterFactory {

    @Override
    public StringFormatter create(String... classNames)
            throws FormatterException {

        ArrayList<StringFormatterExtension> ext = new ArrayList<StringFormatterExtension>();
        for (String name : classNames) {
            try {
                Class clazz = Class.forName(name);
                ext.add((StringFormatterExtension) clazz.newInstance());
            } catch (Exception e) {
                throw new FormatterException(name + " error in access class");
            }
        }
        return new StringFormatter(ext);
    }
}