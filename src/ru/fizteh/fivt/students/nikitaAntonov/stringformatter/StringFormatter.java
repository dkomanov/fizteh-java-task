package ru.fizteh.fivt.students.nikitaAntonov.stringformatter;

import java.lang.reflect.Field;
import java.util.ArrayList;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class StringFormatter implements ru.fizteh.fivt.format.StringFormatter {

    ArrayList<StringFormatterExtension> extensions;

    StringFormatter(ArrayList<StringFormatterExtension> exList) {
        if (exList == null) {
            throw new FormatterException("exList == null");
        }

        extensions = exList;
    }

    @Override
    public String format(String format, Object... args)
            throws FormatterException {
        StringBuilder buf = new StringBuilder();
        format(buf, format, args);

        return buf.toString();
    }

    @Override
    public void format(StringBuilder buffer, String format, Object... args)
            throws FormatterException {

        if (format == null) {
            throw new FormatterException("Format musn't be null");
        }

        if (buffer == null) {
            throw new FormatterException("Buffer musn't be null");
        }

        if (args == null) {
            args = new String[1];
            args[0] = null;
        }

        int pos = 0;
        int end = format.length();

        int openBracketPos = format.indexOf('{');
        int closeBracketPos = format.indexOf('}');
        while (openBracketPos != -1 || closeBracketPos != -1) {

            while (closeBracketPos != -1
                    && (closeBracketPos < openBracketPos || openBracketPos == -1)) {
                if (closeBracketPos != end - 1
                        && format.charAt(closeBracketPos + 1) == '}') {
                    buffer.append(format.substring(pos, closeBracketPos + 1));
                    pos = closeBracketPos + 2;
                    closeBracketPos = format.indexOf('}', pos);
                } else {
                    throw new FormatterException("Unexpected closed bracket");
                }
            }

            if (openBracketPos == -1) {
                break;
            }

            buffer.append(format.substring(pos, openBracketPos));
            pos = openBracketPos + 1;

            if (openBracketPos != end - 1
                    && format.charAt(openBracketPos + 1) == '{') {
                buffer.append('{');
                ++pos;
            } else {
                if (closeBracketPos == -1) {
                    throw new FormatterException("Unexpected opened bracket");
                }
                doFormating(buffer, format.substring(pos, closeBracketPos),
                        args);
                pos = closeBracketPos + 1;
                closeBracketPos = format.indexOf('}', pos);
            }

            openBracketPos = format.indexOf('{', pos);
        }

        if (pos != end) {
            buffer.append(format.substring(pos));
        }

    }

    private void doFormating(StringBuilder buffer, String substring,
            Object args[]) {

        String selector;
        String pattern;

        int colonPos = substring.indexOf(':');

        if (colonPos == -1) {
            selector = substring;
            pattern = null;
        } else {
            selector = substring.substring(0, colonPos);
            pattern = substring.substring(colonPos + 1);
        }

        int pointPos = selector.indexOf('.');
        
        checkIsSelectorCorrect(selector);
        
        Object object;

        if (pointPos == -1) {
            int index;

            try {
                index = Integer.parseInt(selector);
            } catch (NumberFormatException e) {
                throw new FormatterException("Incorrect number: " + selector, e);
            }

            if (index < 0 || index >= args.length) {
                throw new FormatterException("Index " + index
                        + " is out of range");
            }

            object = args[index];

        } else {
            int lastPointPos = pointPos;
            int index;
            try {
                index = Integer.parseInt(selector.substring(0, pointPos));
            } catch (NumberFormatException e) {
                throw new FormatterException("Incorrect number: "
                        + selector.substring(0, pointPos), e);
            }

            if (index < 0 || index >= args.length) {
                throw new FormatterException("Index " + index
                        + " is out of range");
            }

            object = args[index];
            pointPos = selector.indexOf('.', pointPos + 1);

            while (pointPos != -1) {
                object = extractField(object,
                        selector.substring(lastPointPos + 1, pointPos));

                lastPointPos = pointPos;
                pointPos = selector.indexOf('.', pointPos + 1);
            }

            object = extractField(object, selector.substring(lastPointPos + 1));

        }

        if (pattern == null) {
            if (object != null) {
                buffer.append(object.toString());
            }
        } else if (object != null) {
            boolean thereIsNoGoodFormatter = true;

            for (StringFormatterExtension ext : extensions) {
                if (ext.supports(object.getClass())) {
                    ext.format(buffer, object, pattern);
                    thereIsNoGoodFormatter = false;
                    break;
                }
            }

            if (thereIsNoGoodFormatter) {
                throw new FormatterException(
                        "There is no good formatter for class "
                                + object.getClass().getName());
            }
        }

    }

    private static void checkIsSelectorCorrect(String selector) {
        boolean isCorrect = false;
        
        for (int i = 0, e = selector.length(); i < e; ++i) {
            char c = selector.charAt(i);
            if (c == '.') {
                break;
            } else if (Character.isDigit(c)) {
                isCorrect = true;
            } else {
                isCorrect = false;
                break;
            }
        }
        
        if (!isCorrect) {
            throw new FormatterException("Incorrect number: " + selector);
        }
        
    }

    private Object extractField(Object object, String field) {

        if (object == null) {
            return null;
        }
        
        if (field.isEmpty()) {
            throw new FormatterException("Field of class can't have empty name");
        }

        Class<?> parent = object.getClass();
        Object result = null;
        boolean wasSet = false;

        while (parent != null) {
            try {
                Field f = parent.getDeclaredField(field);
                f.setAccessible(true);
                try {
                    result = f.get(object);
                } catch (Throwable e) {
                    result = null;
                }
                wasSet = true;
                break;
            } catch (NoSuchFieldException expt) {
                parent = parent.getSuperclass();
            }
        }

        return result;
    }

}
