package ru.fizteh.fivt.students.mysinYurii.stringFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class StringFormatterFactory implements ru.fizteh.fivt.format.StringFormatterFactory {
    public StringFormatter create(String... classNames) throws FormatterException {
        ArrayList<StringFormatterExtension> extentions = new ArrayList<StringFormatterExtension>();
        if (classNames == null) {
            throw new FormatterException("No extention class names");
        }
        Collections.sort(Arrays.asList(classNames));
        for (int i = 0; i < classNames.length; ++i) {
            if (classNames[i] == null) {
                throw new FormatterException("Null class name");
            }
            if ((i > 0) && (classNames[i].equals(classNames[i - 1]))) {
                throw new FormatterException("Two class names are equal: " + classNames[i]);
            }
            try {
                extentions.add((StringFormatterExtension) Class.forName(classNames[i]).newInstance());
            } catch(Throwable e) {
                throw new FormatterException(e.getMessage());
            }
        }
        return new StringFormatter(extentions);
    }
}
