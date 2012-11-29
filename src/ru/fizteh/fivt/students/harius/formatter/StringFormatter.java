/*
 * StringFormatter.java
 * Nov 14, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.formatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.lang.reflect.Field;

/*
 * Formats strings
 */
public class StringFormatter
    implements ru.fizteh.fivt.format.StringFormatter {

    /* List of enabled extensions */
    private List<StringFormatterExtension> ext = new ArrayList<>();

    /* Construct formatter supporting listed extensions */
    public StringFormatter(StringFormatterExtension... ext) {
        this.ext = Arrays.asList(ext);
    }

    /* Returns the formatted string */
    @Override
    public String format(String format, Object... args)
        throws FormatterException {

        StringBuilder buffer = new StringBuilder();
        format(buffer, format, args);
        return buffer.toString();
    }

    /* Formats the string and writer the result to buffer */
    @Override
    public void format(StringBuilder buffer, String format, Object... args)
        throws FormatterException {

        if (buffer == null) {
            throw new FormatterException("Null buffer");
        }

        StringBuilder safe = new StringBuilder();
        format(safe, format, 0, args);
        buffer.append(safe);
    }

    /* Format starting from given index */
    private void format(StringBuilder buffer, String format, int start, Object... args)
        throws FormatterException {

        if (format == null) {
            throw new FormatterException("Null format string");
        }

        int first = format.indexOf('{', start);
        int close = format.indexOf('}', start);
        if (close != -1 && (first == -1 || first > close)) {
            if (close != format.length() - 1 && format.charAt(close + 1) == '}') {
                buffer.append(format.substring(start, close + 1));
                format(buffer, format, close + 2, args);
            } else {
                throw new FormatterException("Unexpected }");
            }
        } else if (first == -1) {
            buffer.append(format.substring(start));
        } else {
            if (first == format.length() - 1) {
                throw new FormatterException("Single { at the end of string");
            }
            buffer.append(format.substring(start, first));
            if (format.charAt(first + 1) == '{') {
                buffer.append("{");
                format(buffer, format, first + 2, args);
            } else {
                int match = format.indexOf("}", first);
                if (match == -1) {
                    throw new FormatterException("Unclosed }");
                } else {
                    String token = format.substring(first + 1, match);
                    formatOne(buffer, token, args);
                    format(buffer, format, match + 1, args);
                }
            }
        }
    }

    /* Format a single fragment */
    private void formatOne(StringBuilder buffer, String token, Object... args)
        throws FormatterException {

        int sep = token.indexOf(":");
        if (sep == -1) {
            sep = token.length();
        }
        String chain = token.substring(0, sep);
        Object obj = getFromChain(chain, args);
        if (obj != null) {
            if (sep == token.length()) {
                applyPlain(buffer, obj);
            } else {
                String pattern = token.substring(sep + 1);
                applyPattern(buffer, pattern, obj);
            }
        }
    }

    /* Get an object from the string like '0.foo.bar' */
    private Object getFromChain(String chain, Object... args)
        throws FormatterException {

        StringTokenizer tok = new StringTokenizer(chain, ".");
        if (!tok.hasMoreTokens()) {
            throw new FormatterException("Empty index string");
        }
        String sIndex = tok.nextToken();
        if (sIndex.startsWith("+") || sIndex.startsWith("-")) {
            throw new FormatterException("Index must be unsigned");
        }
        int index = 0;
        Object arg;
        try {  
            index = Integer.parseInt(sIndex);
            arg = args[index];
        } catch (NumberFormatException notNum) {
            throw new FormatterException(
                sIndex + " is not a valid argument index",
                notNum);

        } catch (ArrayIndexOutOfBoundsException out) {
            throw new FormatterException(
                "Argument index out of bounds: " + index,
                out);
        } catch (NullPointerException nullEx) {
            throw new FormatterException(
                "Null arguments array",
                nullEx);
        }
        while (tok.hasMoreTokens()) {
            String field = tok.nextToken();
            arg = getField(arg, field);
        }
        return arg;
    }

    /* Get a field from an object or one of its parents */
    private Object getField(Object arg, String name)
        throws FormatterException {

        if (arg == null) {
            return null;
        }

        Class deep = arg.getClass();
        try {
            try {
                Field field = deep.getField(name);
                field.setAccessible(true);
                return field.get(arg);
            } catch (NoSuchFieldException noField) {
                while (deep != null) {
                    try {
                        Field field = deep.getDeclaredField(name);
                        field.setAccessible(true);
                        return field.get(arg);
                    } catch (NoSuchFieldException noDamnField) {
                        deep = deep.getSuperclass();
                    }
                }
                return null;
            }
        } catch (IllegalAccessException accEx) {
            throw new FormatterException(
                String.format(
                    "Illegal access to field %s of %s",
                    name, arg.getClass().getSimpleName()),
                accEx
            );
        }
    }

    /* Simple format */
    private void applyPlain(StringBuilder buffer, Object arg) {
        buffer.append(arg.toString());
    }

    /* Format using an extension */
    private void applyPattern(StringBuilder buffer, String pattern, Object arg)
        throws FormatterException {

        for (StringFormatterExtension extend : ext) {
            if (extend.supports(arg.getClass())) {
                try {
                    extend.format(buffer, arg, pattern);
                } catch (Exception ex) {
                    throw new FormatterException(
                        "Error while formatting " +
                            arg.getClass() + ": " + ex.getMessage(),
                        ex);
                }
                return;
            }
        }
        throw new FormatterException("Extention not found for " +
            arg.getClass().getSimpleName());
    }
}