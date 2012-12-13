package ru.fizteh.fivt.students.frolovNikolay.stringFormatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import java.lang.reflect.Field;

public class StringFormatter implements ru.fizteh.fivt.format.StringFormatter {
    private Vector<StringFormatterExtension> extensions;
    
    StringFormatter() {
        extensions = new Vector<StringFormatterExtension>();
    }
    
    public void addExtension(StringFormatterExtension added) throws FormatterException {
        if (added == null) {
            throw new FormatterException("addExtension: null pointer");
        }
        try {
            extensions.add(added);
        } catch (Throwable exception) {
            throw new FormatterException(exception);
        }    
    }
    
    private void insertArgInBuffer(StringBuilder buffer, String objName, Object... args)
            throws FormatterException {
        int patternIdx = objName.indexOf(':');
        String pattern = null;
        Object currentObj = null;
        if (patternIdx != -1) {
            pattern = objName.substring(patternIdx + 1);
            objName = objName.substring(0, patternIdx);
        }
        if (objName.isEmpty() || objName.charAt(objName.length() - 1) == '.') {
            throw new FormatterException("Incorrect expression in some brackets");
        }
        String[] fields = objName.split("\\.");
        if (fields.length == 0) {
            throw new FormatterException("Incorrect expression in some brackets");
        } else if (fields[0].isEmpty()) {
                throw new FormatterException("Incorrect expression in some brackets");
        } else if (!Character.isDigit(fields[0].charAt(0))) {
            throw new FormatterException("Incorrect expression in some brackets");
        } else {
            try {
                int position = Integer.valueOf(fields[0]);
                if (position >= args.length) {
                    throw new FormatterException("Incorrect object-index");
                } else {
                    currentObj = args[position];
                }
            } catch (Throwable exception) {
                throw new FormatterException("Incorrect object-index", exception);
            }
            for (int i = 1; i < fields.length; ++i) {
                if (fields[i].isEmpty()) {
                    throw new FormatterException("Incorrect expression in some brackets");
                } else {
                    if (currentObj != null) {
                        try {
                            Class<?> parent = currentObj.getClass();
                            while (parent != null) {
                                try {
                                    Field field = parent.getDeclaredField(fields[i]);
                                    field.setAccessible(true);
                                    currentObj = field.get(currentObj);
                                    break;
                                } catch (NoSuchFieldException exception) {
                                    parent = parent.getSuperclass();
                                }
                            }
                            if (parent == null) {
                                currentObj = null;
                            }
                        } catch (Throwable exception) {
                            throw new FormatterException("No access to field: " + fields[i]);
                        }
                    }
                }
            }
        }
        if (currentObj != null) {
            if (pattern == null) {
                buffer.append(currentObj.toString());
            } else {
                StringFormatterExtension extension = null;
                for (StringFormatterExtension iter : extensions) {
                    if (iter.supports(currentObj.getClass())) {
                        extension = iter;
                        break;
                    }
                }
                if (extension != null) {
                    extension.format(buffer, currentObj, pattern);
                } else {
                    throw new FormatterException("No extension for: " + currentObj.getClass());
                }
            }
        }
    }
    
    @Override
    public void format(StringBuilder buffer, String format, Object... args)
            throws FormatterException {
        int lastInsert = 0;
        int position = 0;
        while (position < format.length()) {
            while (position < format.length() && format.charAt(position) != '{'
                   && format.charAt(position) != '}') {
                ++position;
            }
            if (position == format.length()) {
                buffer.append(format.substring(lastInsert));
            } else if (format.charAt(position) == '{') {
                if (position + 1 < format.length() && format.charAt(position + 1) == '{') {
                    buffer.append(format.substring(lastInsert, position + 1));
                    position += 2;
                } else {
                    buffer.append(format.substring(lastInsert, position));
                    int left = position + 1;
                    int right = position;
                    while (right < format.length() && format.charAt(right) != '}') {
                        ++right;
                    }
                    if (right == format.length()) {
                        throw new FormatterException("Incorrect placement of the brackets");
                    } else {
                        position = right + 1;
                        String objName = format.substring(left, right);
                        insertArgInBuffer(buffer, objName, args);
                    }
                }
            } else if (position + 1 < format.length() && format.charAt(position + 1) == '}') {
                    buffer.append(format.substring(lastInsert, position + 1));
                    position += 2;
            } else {
                throw new FormatterException("Incorrect placement of the brackets");
            }
            lastInsert = position;
        }
    }
    
    @Override
    public String format(String format, Object... args) throws FormatterException {
        StringBuilder buffer = new StringBuilder();
        format(buffer, format, args);
        return buffer.toString();
    }
}