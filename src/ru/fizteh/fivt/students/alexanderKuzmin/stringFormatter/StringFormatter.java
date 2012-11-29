package ru.fizteh.fivt.students.alexanderKuzmin.stringFormatter

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

/**
 * @author Kuzmin A. group 196 Class StringFormatter.
 * 
 */

public class StringFormatter implements ru.fizteh.fivt.format.StringFormatter {

    private List<StringFormatterExtension> extensions;

    StringFormatter() {
        extensions = Collections.synchronizedList(new ArrayList<StringFormatterExtension>());
    }

    public StringFormatter(List<StringFormatterExtension> others) {
        extensions = others;
    }

    @Override
    public String format(String format, Object... args)
            throws FormatterException {
        StringBuilder buffer = new StringBuilder();
        format(buffer, format, args);
        return buffer.toString();
    }

    @Override
    public void format(StringBuilder buffer, String format, Object... args)
            throws FormatterException {
        toDoFormat(buffer, 0, format, args);
    }

    public void toDoFormat(StringBuilder buffer, int startPosition,
            String format, Object... args) throws FormatterException {
        int begin = format.indexOf('{', startPosition);
        int end = format.indexOf('}', startPosition);
        if (end == -1 || begin == -1) {
            if (end == -1 && begin == -1) {
                buffer.append(format.substring(startPosition));
                return;
            } else if (begin == -1) {
                begin = format.length();
            } else {
                end = format.length();
            }
        }

        if (begin < end) {
            if (begin < format.length() - 1 && format.charAt(begin + 1) == '{') {
                buffer.append(format.substring(startPosition, begin + 1));
                toDoFormat(buffer, begin + 2, format, args);
            } else {
                if (end == begin + 1 || end == format.length()) {
                    throw new FormatterException(
                            "Use correct input: Any characters, {idx}, {idx: pattern}, {idx.field}, {idx.field.field: pattern}");
                }
                buffer.append(format.substring(startPosition, begin));
                getArgs(buffer, format.substring(begin + 1, end), args);
                toDoFormat(buffer, end + 1, format, args);
            }
        } else {
            if (end < format.length() - 1 && format.charAt(end + 1) == '}') {
                buffer.append(format.substring(startPosition, end + 1));
                toDoFormat(buffer, end + 2, format, args);
            } else {
                throw new FormatterException(
                        "Use correct input: Any characters, {idx}, {idx: pattern}, {idx.field}, {idx.field.field: pattern}");
            }
        }
    }

    public void getArgs(StringBuilder buffer, String format, Object... args)
            throws FormatterException {
        if (args == null) {
            return;
        }
        int begin = format.indexOf(':');
        if (begin == -1) {
            begin = format.length();
        }
        StringTokenizer field = new StringTokenizer(format.substring(0, begin),
                ".");
        Object cur = null;
        try {
            String argPosition = field.nextToken();
            if (!Character.isDigit(argPosition.charAt(0))) {
                throw new FormatterException(
                        "Use correct input: Any characters, {idx}, {idx: pattern}, {idx.field}, {idx.field.field: pattern}");
            }
            cur = args[Integer.parseInt(argPosition)];
            while (field.hasMoreTokens()) {
                cur = getField(cur, field.nextToken());
            }
            if (cur == null) {
                return;
            }
        } catch (Throwable e) {
            throw new FormatterException(
                    "Use correct input: Any characters, {idx}, {idx: pattern}, {idx.field}, {idx.field.field: pattern}",
                    e);
        }

        if (begin == format.length()) {
            buffer.append(cur.toString());
        } else {
            StringFormatterExtension extension = null;
            for (StringFormatterExtension ex : extensions) {
                if (ex.supports(cur.getClass())) {
                    extension = ex;
                    break;
                }
            }
            if (extension != null) {
                extension.format(buffer, cur, format.substring(begin + 1));
            } else {
                throw new FormatterException("No extension.");
            }
        }
    }

    public Object getField(Object cur, String field) throws FormatterException {
        if (cur == null) {
            return null;
        }
        try {
            Class<?> parent = cur.getClass();
            while (parent != null) {
                try {
                    Field f = parent.getDeclaredField(field);
                    f.setAccessible(true);
                    return f.get(cur);
                } catch (NoSuchFieldException expt) {
                    parent = parent.getSuperclass();
                }
            }
            return null;
        } catch (Throwable e) {
            throw new FormatterException("Can't access field " + field + " in "
                    + cur.getClass(), e);
        }
    }
}