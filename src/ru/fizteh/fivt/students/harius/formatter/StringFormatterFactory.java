package ru.fizteh.fivt.students.harius.formatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class StringFormatterFactory
    implements ru.fizteh.fivt.format.StringFormatterFactory {

    @Override
    public StringFormatter create(String... extensionClassNames)
        throws FormatterException {

        StringFormatterExtension[] ext =
            new StringFormatterExtension[extensionClassNames.length];

        int index = 0;
        for (String name : extensionClassNames) {
            try {
                Class clazz = Class.forName(name);
                // Class<? extends StringFormatterExtension> casted =
                //     StringFormatterExtension.class.asSubclass(clazz);
                // ext[index] = casted.newInstance();
                ext[index] = StringFormatterExtension.class.cast(clazz.newInstance());
            } catch (ClassNotFoundException notFound) {
                throw new FormatterException(name + " is not a valid class");
            } catch (ClassCastException cast) {
                throw new FormatterException(name + " is not an extension class");
            } catch (InstantiationException | IllegalAccessException inst) {
                throw new FormatterException(name + " cannot been instantiated");
            }
            ++index;
        }
        return new StringFormatter(ext);
    }

}