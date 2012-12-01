package ru.fizteh.fivt.students.altimin.formatter;

import ru.fizteh.fivt.format.StringFormatterExtension;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * User: altimin
 * Date: 11/30/12
 * Time: 10:53 PM
 */
public class StringFormatterCalendarExtension extends StringFormatterExtension {
    public StringFormatterCalendarExtension() {
        super(Calendar.class);
    }

    @Override
    public boolean supports(Class clazz) {
        return super.clazz.isAssignableFrom(clazz);
    }

    @Override
    public void format(StringBuilder buffer, Object object, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        Calendar calendar = (Calendar) object;
        buffer.append(formatter.format(calendar.getTime()));
    }
}
