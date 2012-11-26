/*
 * StringFormatterFactory.java
 * Nov 14, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.formatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;


/*
 * A factory for creating StringFormatters with extensions
 */
public class StringFormatterFactory
    implements ru.fizteh.fivt.format.StringFormatterFactory {

    /* Create a formatter with given extension names */
    @Override
    public StringFormatter create(String... extensionClassNames)
        throws FormatterException {

        if (extensionClassNames == null) {
            throw new FormatterException("Got null array of extension class names");
        }

        StringFormatterExtension[] ext =
            new StringFormatterExtension[extensionClassNames.length];

        int index = 0;
        for (String name : extensionClassNames) {
            if (name == null) {
                throw new FormatterException("Got null extension class name");
            }
            try {
                Class clazz = Class.forName(name);
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