package ru.fizteh.fivt.students.fedyuninV.format;



import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

import java.util.ArrayList;
import java.util.Arrays;
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
        Collections.sort(Arrays.asList(extensionClassNames));
        for (int i = 0; i < extensionClassNames.length; i++) {
            String currClassName = extensionClassNames[i];
            if (i != 0  &&  extensionClassNames[i - 1].equals(currClassName)) {
                throw new FormatterException("Duplicate classes were found");
            }
            try {
                extensionList.add((StringFormatterExtension) Class.forName(currClassName).newInstance());
            } catch (Exception ex) {
                throw new FormatterException("Unable to create formatter for this classes");
            }
        }
        return new StringFormatter(extensionList);
    }
}
