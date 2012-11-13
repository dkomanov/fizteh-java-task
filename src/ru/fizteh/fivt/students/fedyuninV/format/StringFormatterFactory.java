package ru.fizteh.fivt.students.fedyuninV.format;



import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class StringFormatterFactory implements ru.fizteh.fivt.format.StringFormatterFactory{

    List<StringFormatterExtension> extensionList;
    public StringFormatter create(String... extensionClassNames) throws FormatterException {
        extensionList = new ArrayList<StringFormatterExtension>();
        List<String> classNames = new ArrayList<String>();
        Collections.addAll(classNames, extensionClassNames);
        Collections.sort(classNames);
        for (int i = 0; i < classNames.size(); i++) {
            String currClassName = classNames.get(i);
            if (i != 0   &&   classNames.get(i - 1).equals(currClassName)) {
                throw new FormatterException("Dublicate classes were found");
            }
            try {
                extensionList.add((StringFormatterExtension) Class.forName(currClassName).newInstance());
            } catch (Exception ex) {
                throw new FormatterException("Unable to create formatter for tihs classes");
            }
        }
        return new StringFormatter(extensionList);
    }
}
