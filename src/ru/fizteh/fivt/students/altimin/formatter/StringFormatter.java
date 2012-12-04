package ru.fizteh.fivt.students.altimin.formatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

import java.lang.reflect.Field;
import java.util.List;

/**
 * User: altimin
 * Date: 11/30/12
 * Time: 11:05 PM
 */
public class StringFormatter implements ru.fizteh.fivt.format.StringFormatter {
    private List<StringFormatterExtension> extensions;

    StringFormatter(List<StringFormatterExtension> extensions) {
        this.extensions = extensions;
    }

    @Override
    public String format(String format, Object... args) throws FormatterException {
        StringBuilder buffer = new StringBuilder();
        format(buffer, format, args);
        return buffer.toString();
    }

    @Override
    public void format(StringBuilder buffer, String format, Object... args) throws FormatterException {
        format(buffer, format, 0, args);
    }

    Object getField(Object object, String fieldName) throws FormatterException {
        if (object == null) {
            return null;
        }
        Class objectClass = object.getClass();
        while (objectClass != null) {
            try {
                Field field = objectClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(object);
            } catch (NoSuchFieldException e) {
            } catch (IllegalAccessException e) {
            }
            objectClass = objectClass.getSuperclass();
        }
        return null;
    }

    private void formatArgument(StringBuilder buffer, String format, Object... args) throws FormatterException {
        int colonPosition = format.indexOf(":");
        String[] splittedFormat;
        if (colonPosition != -1) {
            splittedFormat = format.substring(0, colonPosition).split("\\.");
        } else {
            splittedFormat = format.split("\\.");
        }
        if (splittedFormat.length == 0) {
            throw new FormatterException("Empty format string");
        }
        int objectIndex;
        try {
            objectIndex = Integer.parseInt(splittedFormat[0]);
            if (splittedFormat[0].startsWith("+") || splittedFormat[0].startsWith("-")) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e){
            throw new FormatterException(
                    String.format("Incorrect object index: %s is not a valid integer", splittedFormat[0]));
        }
        if (!(0 <= objectIndex && objectIndex < args.length)) {
            throw new FormatterException(String.format("Incorrect object index %d: index out of range", objectIndex));
        }
        Object object = args[objectIndex];
        for (int i = 1; i < splittedFormat.length; i ++) {
            object = getField(object, splittedFormat[i]);
        }
        if (object == null) {
            return;
        }
        if (colonPosition == -1) {
            buffer.append(object.toString());
        } else {
            for (StringFormatterExtension extension: extensions) {
                if (extension.supports(object.getClass())) {
                    try{
                        extension.format(buffer, object, format.substring(colonPosition + 1));
                    } catch (Exception e) {
                        throw new FormatterException("Failed to format " + format.substring(colonPosition + 1));
                    }
                    return;
                }
            }
            throw new FormatterException(String.format("No formatter for class %s", object.getClass().getSimpleName()));
        }
    }

    private void format(StringBuilder buffer, String format, int position, Object... args) throws FormatterException {
        if (position >= format.length()) {
            return;
        }
        if (format.charAt(position) == '{') {
            if (position + 1 < format.length() && format.charAt(position + 1) == '{') {
                buffer.append('{');
                format(buffer, format, position + 2, args);
                return;
            }
            int closeBracketPos = format.indexOf('}', position);
            if (closeBracketPos == -1) {
                throw new FormatterException(
                        String.format("Failed to parse %s: unmatched bracket at position %d", format, position));
            }
            formatArgument(buffer, format.substring(position + 1, closeBracketPos), args);
            format(buffer, format, closeBracketPos + 1, args);
            return;
        }
        if (format.charAt(position) == '}') {
            if (position + 1 < format.length() && format.charAt(position + 1) == '}') {
                buffer.append('}');
                format(buffer, format, position + 2, args);
                return;
            }
            throw new FormatterException(
                    String.format("Failed to parse %s: unexpected closing bracket at position %d", format, position));
        }
        buffer.append(format.charAt(position));
        format(buffer, format, position + 1, args);
    }
}
