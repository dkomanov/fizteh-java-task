package ru.fizteh.fivt.students.altimin.formatter;

import ru.fizteh.fivt.format.FormatterException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: altimin
 * Date: 12/1/12
 * Time: 12:37 AM
 */
public class StringFormatterFactory implements ru.fizteh.fivt.format.StringFormatterFactory {
    @Override
    public ru.fizteh.fivt.format.StringFormatter create(String... args) throws FormatterException {
        List<StringFormatterExtension> extensions = new ArrayList<StringFormatterExtension>();
        Arrays.sort(args);
        for (int i = 0; i < args.length; i ++) {
            String currentName = args[i];
            try {
                extensions.add((StringFormatterExtension) Class.forName(currentName).newInstance());
            } catch (Exception e) {
                throw new FormatterException(String.format("Class %s isn't supported", currentName));
            }
        }
        return new StringFormatter(extensions);
    }
}
