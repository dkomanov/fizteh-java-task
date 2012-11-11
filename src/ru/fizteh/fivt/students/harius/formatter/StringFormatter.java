package ru.fizteh.fivt.students.harius.formatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

public class StringFormatter
    implements ru.fizteh.fivt.format.StringFormatter {

    private List<StringFormatterExtension> ext = new ArrayList<>();

    public StringFormatter(StringFormatterExtension... ext) {
        this.ext = Arrays.asList(ext);
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

        StringBuilder safe = new StringBuilder();
        format(safe, format, 0, args);
        buffer.append(safe);
    }

    private void format(StringBuilder buffer, String format, int start, Object... args)
        throws FormatterException {

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

    private void formatOne(StringBuilder buffer, String token, Object... args)
        throws FormatterException {

        int sep = token.indexOf(":");
        if (sep == -1) {
            sep = token.length();
        }
        String chain = token.substring(0, sep);
        Object obj = getFromChain(chain, args);
        if (sep == token.length()) {
            applyPlain(buffer, obj);
        } else {
            String pattern = token.substring(sep + 1);
            applyPattern(buffer, pattern, obj);
        }
    }

    private Object getFromChain(String chain, Object... args)
        throws FormatterException {

        StringTokenizer tok = new StringTokenizer(chain, ".");
        String sIndex = tok.nextToken();
        int index = 0;
        Object arg;
        try {    
            index = Integer.parseInt(sIndex);
            arg = args[index];
        } catch (NumberFormatException notNum) {
            throw new FormatterException(sIndex + " is not a valid argument index");
        } catch (ArrayIndexOutOfBoundsException out) {
            throw new FormatterException("Argument index out of bounds: " + index);
        }
        while (tok.hasMoreTokens()) {
            String field = tok.nextToken();
            try {
                arg = arg.getClass().getField(field).get(arg);
            } catch (NoSuchFieldException | IllegalAccessException bad) {
                throw new FormatterException("Error while fetching " +
                    field + " from " + arg.getClass().getSimpleName());
            }
        }
        return arg;
    }

    private void applyPlain(StringBuilder buffer, Object arg) {
        if (arg != null) {
            buffer.append(arg.toString());
        } else {
            buffer.append("");
        }
    }

    private void applyPattern(StringBuilder buffer, String pattern, Object arg)
        throws FormatterException {

        for (StringFormatterExtension extend : ext) {
            if (extend.supports(arg.getClass())) {
                try {
                    extend.format(buffer, arg, pattern);
                } catch (Exception ex) {
                    throw new FormatterException("Error while formatting " +
                        arg.getClass() + ": " + ex.getMessage());
                }
                return;
            }
        }
        throw new FormatterException("Extention not found for " +
            arg.getClass().getSimpleName());
    }
}