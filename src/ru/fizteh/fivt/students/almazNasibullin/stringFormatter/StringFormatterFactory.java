package ru.fizteh.fivt.students.almazNasibullin.stringFormatter;

import java.util.Arrays;
import java.util.Collections;
import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

/**
 * 19.11.12
 * @author almaz
 */

public class StringFormatterFactory implements ru.fizteh.fivt.format.StringFormatterFactory {
    
    @Override
    public StringFormatter create(String... extensionClassNames) throws FormatterException {
        if (extensionClassNames == null) {
            throw new FormatterException("Bad pointer for classNames");
        }
        Collections.sort(Arrays.asList(extensionClassNames));
        StringFormatter sf = new StringFormatter();
        for (int i = 0; i < extensionClassNames.length; ++i) {
            if (extensionClassNames[i] == null) {
                throw new FormatterException("Bad class name");
            }
            if (i > 0 && extensionClassNames[i].equals(extensionClassNames[i - 1])) {
                throw new FormatterException("Same classes");
            }
            try {
                sf.addExtension((StringFormatterExtension)Class.
                        forName(extensionClassNames[i]).newInstance());
            } catch (FormatterException fe) {
                throw fe;
            } catch (Exception e) {
                throw new FormatterException("Bad extension");
            }
        }
        return sf;
    }
}
