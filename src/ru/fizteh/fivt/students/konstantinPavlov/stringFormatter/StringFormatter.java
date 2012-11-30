package ru.fizteh.fivt.students.konstantinPavlov.stringFormatter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class StringFormatter implements ru.fizteh.fivt.format.StringFormatter {
    private final List<StringFormatterExtension> listOfExtensions = Collections
            .synchronizedList(new ArrayList<StringFormatterExtension>());

    public void addToListOfExtensions(StringFormatterExtension extension)
            throws FormatterException {
        if (extension == null) {
            throw new FormatterException("null extension");
        }

        try {
            listOfExtensions.add(extension);
        } catch (Exception e) {
            throw new FormatterException(e.getMessage());
        }
    }

    @Override
    public String format(String stringToFormat, Object... args)
            throws FormatterException {
        StringBuilder buffer = new StringBuilder();
        format(buffer, stringToFormat, args);
        return buffer.toString();
    }

    @Override
    public void format(StringBuilder buffer, String stringToFormat,
            Object... args) throws FormatterException {
        formatter(buffer, 0, stringToFormat, args);
    }

    private void formatter(StringBuilder buffer, int fromIdx,
            String stringToFormat, Object... args) throws FormatterException {
        int openingBraceIdx = stringToFormat.indexOf('{', fromIdx);
        int closingBraceIdx = stringToFormat.indexOf('}', fromIdx);

        if (closingBraceIdx == -1 && openingBraceIdx == -1) {
            buffer.append(stringToFormat.substring(fromIdx));
            return;
        }

        if (openingBraceIdx == -1) {
            openingBraceIdx = stringToFormat.length();
        }

        if (closingBraceIdx == -1) {
            closingBraceIdx = stringToFormat.length();
        }

        if (closingBraceIdx > openingBraceIdx) {
            if (openingBraceIdx < stringToFormat.length() - 1
                    && stringToFormat.charAt(openingBraceIdx + 1) == '{') {
                buffer.append(stringToFormat.substring(fromIdx,
                        openingBraceIdx + 1));
                formatter(buffer, openingBraceIdx + 2, stringToFormat, args);
            } else {
                if (closingBraceIdx == stringToFormat.length()) {
                    throw new FormatterException("something wrong whith braces");
                }

                if (closingBraceIdx == openingBraceIdx + 1) {
                    throw new FormatterException("no index of argument");
                }

                buffer.append(stringToFormat
                        .substring(fromIdx, openingBraceIdx));
                getField(buffer, stringToFormat.substring(openingBraceIdx + 1,
                        closingBraceIdx), args);
                formatter(buffer, closingBraceIdx + 1, stringToFormat, args);
            }
        } else {
            if (closingBraceIdx < stringToFormat.length() - 1
                    && stringToFormat.charAt(closingBraceIdx + 1) == '}') {
                buffer.append(stringToFormat.substring(fromIdx,
                        closingBraceIdx + 1));
                formatter(buffer, closingBraceIdx + 2, stringToFormat, args);
            } else {
                throw new FormatterException("something wrong whith braces");
            }
        }
    }

    private void getField(StringBuilder buffer, String stringToFormat,
            Object... args) throws FormatterException {
        Object object = null;
        int pattern = stringToFormat.indexOf(':');
        if (pattern == -1) {
            pattern = stringToFormat.length();
        }

        StringTokenizer stringTokenizer = new StringTokenizer(
                stringToFormat.substring(0, pattern), ".");
        try {
            String token = stringTokenizer.nextToken();
            if (token.charAt(0) == '-' || token.charAt(0) == '+') {
                throw new FormatterException("invalid index of argument");
            }

            object = args[Integer.parseInt(token)];
            while (stringTokenizer.hasMoreTokens()) {
                object = getFieldFromObject(object, stringTokenizer.nextToken());
            }

            if (object == null) {
                return;
            }

        } catch (Exception e) {
            throw new FormatterException("invalid index of argument");
        }

        if (pattern == stringToFormat.length()) {
            buffer.append(object.toString());
        } else {
            StringFormatterExtension extension = null;
            for (StringFormatterExtension availableExtension : listOfExtensions) {
                if (availableExtension.supports(object.getClass())) {
                    extension = availableExtension;
                    break;
                }
            }

            if (extension != null) {
                extension.format(buffer, object,
                        stringToFormat.substring(pattern + 1));
            } else {
                throw new FormatterException("no extension for "
                        + object.getClass());
            }
        }

    }

    private Object getFieldFromObject(Object object, String field)
            throws FormatterException {
        if (object == null) {
            return null;
        }

        try {
            Class<?> parent = object.getClass();
            while (parent != null) {
                try {
                    Field f = parent.getDeclaredField(field);
                    f.setAccessible(true);
                    return f.get(object);
                } catch (NoSuchFieldException e) {
                    parent = parent.getSuperclass();
                }
            }
            return null;
        } catch (Exception e) {
            throw new FormatterException("not access to the field " + field
                    + " in " + object.getClass());
        }
    }

}