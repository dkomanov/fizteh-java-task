package ru.fizteh.fivt.students.yuliaNikonova.stringFormatter;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class StringFormatter implements ru.fizteh.fivt.format.StringFormatter {
    private List<StringFormatterExtension> extensions = Collections.synchronizedList(new ArrayList<StringFormatterExtension>());

    public void addExtension(StringFormatterExtension ext) throws FormatterException {
        if (ext == null) {
            throw new FormatterException("NULL extension");
        }

        try {
            extensions.add(ext);
        } catch (Exception e) {
            throw new FormatterException(e.getMessage(), e);
        }
    }

    @Override
    public String format(String format, Object... args) throws FormatterException {

        StringBuilder buffer = new StringBuilder();
        format(buffer, format, args);
        return buffer.toString();
    }

    @Override
    public void format(StringBuilder buffer, String format, Object... args) throws FormatterException {
        startFormat(buffer, 0, format, args);

    }

    public void startFormat(StringBuilder buffer, int pos, String format, Object... args) {
        int start = format.indexOf("{", pos);
        int end = format.indexOf("}", pos);
        if (start == -1 && end == -1) {
            buffer.append(format.substring(pos));

            return;
        }
        int len = format.length();
        if (start == -1) {
            start = len;
        }
        if (end == -1) {
            end = len;
        }

        if (start < end) {
            if (start < len - 1 && format.charAt(start + 1) == '{') {
                buffer.append(format.substring(pos, start + 1));

                startFormat(buffer, start + 2, format, args);
            } else {
                if (end == start + 1) {
                    throw new FormatterException("Empty brackets");
                }

                buffer.append(format.substring(pos, start));

                getValue(buffer, format.substring(start + 1, end), args);
                startFormat(buffer, end + 1, format, args);
            }
        } else {
            if (end < len - 1 && format.charAt(end + 1) == '}') {
                buffer.append(format.substring(pos, end + 1));

                startFormat(buffer, end + 2, format, args);
            } else {
                throw new FormatterException("something wrong with brackets");
            }
        }
    }

    private void getValue(StringBuilder buffer, String strFormat, Object[] args) {
        Object result = null;
        int pattern = strFormat.indexOf(":");
        int len = strFormat.length();
        if (pattern == -1) {
            pattern = len;
        }

        String fields[] = strFormat.substring(0, pattern).split("\\.");

        int pos = 0;
        try {
            pos = Integer.parseInt(fields[0]);
            if (pos < 0 || pos >= args.length) {
                throw new Exception();
            }

        } catch (Exception e) {
            throw new FormatterException("bad index: " + fields[0], e);
        }
        result = args[pos];
        try {
            for (int i = 1; i < fields.length; i++) {
                result = getField(result, fields[i]);

            }
        } catch (FormatterException e) {
            throw e;
        }
        if (result == null) {
            return;
        }

        if (pattern == strFormat.length()) {
            buffer.append(result.toString());

        } else {
            StringFormatterExtension extension = null;

            for (StringFormatterExtension ext : extensions) {
                if (ext.supports(result.getClass())) {
                    extension = ext;
                    break;
                }
            }
            if (extension != null) {
                extension.format(buffer, result, strFormat.substring(pattern + 1));
            } else {
                throw new FormatterException("No extension for " + result.getClass().toString());
            }
        }

    }

    private Object getField(Object result, String field) {

        if (result == null) {
            return null;
        }

        try {
            Class clazz = result.getClass();

            while (clazz != null) {

                try {
                    Field f = clazz.getDeclaredField(field);
                    f.setAccessible(true);

                    return f.get(result);
                } catch (NoSuchFieldException e) {

                    clazz = clazz.getSuperclass();
                }
            }

            throw new FormatterException("Field " + field + " in " + result.getClass() + " is unaccessible");

        } catch (Exception e) {
            throw new FormatterException("Field " + field + " in " + result.getClass() + " is unaccessible", e);
        }

    }
}
