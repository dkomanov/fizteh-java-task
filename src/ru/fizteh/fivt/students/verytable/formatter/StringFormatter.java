package ru.fizteh.fivt.students.verytable.formatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class StringFormatter implements ru.fizteh.fivt.format.StringFormatter {


    public List<StringFormatterExtension> extensions;

    public StringFormatter(ArrayList<StringFormatterExtension> extension) {

        int extSize = extension.size();
        extensions = Collections.synchronizedList(extension.subList(0, extSize));
    }

    @Override
    public String format(String inputString, Object... args)
                         throws FormatterException {

        StringBuilder sb = new StringBuilder();
        format(sb, inputString, args);
        return sb.toString();
    }

    @Override
    public void format(StringBuilder sb, String format, Object... args)
                       throws FormatterException {

        format(sb, format, 0, args);
    }

    public void format(StringBuilder sb, String format, int startPos,
                       Object... args) throws FormatterException {

        if (format == null) {
            throw new FormatterException("Error: empty format string.");
        }

        int opBracketPos = format.indexOf('{', startPos);
        int clBracketPos = format.indexOf('}', startPos);

        if (opBracketPos == -1) {
            opBracketPos = format.length();
        }

        if (clBracketPos == -1) {
            clBracketPos = format.length();
        }

        if (clBracketPos == format.length() && opBracketPos == format.length()) {
            sb.append(format.substring(startPos));
            return;
        }

        if (opBracketPos > clBracketPos) {
            if (clBracketPos < format.length() - 1
                && format.charAt(clBracketPos + 1) == '}') {
                sb.append(format.substring(startPos, clBracketPos + 1));
                format(sb, format, clBracketPos + 2, args);
            } else {
                throw new FormatterException("Error: unexpected '}'.");
            }
        } else {
            if (opBracketPos < format.length() - 1
                && format.charAt(opBracketPos + 1) == '{') {
                sb.append(format.substring(startPos, opBracketPos + 1));
                format(sb, format, opBracketPos + 2, args);
            } else {
                if (clBracketPos == opBracketPos + 1
                    || clBracketPos == format.length()) {
                    throw new FormatterException("Error: unexpected '{'.");
                }
                sb.append(format.substring(startPos, opBracketPos));
                formatArg(sb, format.substring(opBracketPos + 1, clBracketPos),
                          args);
                format(sb, format, clBracketPos + 1, args);
            }
        }

    }

    public void formatArg(StringBuilder sb, String format, Object... args)
                          throws FormatterException {

        if (args == null) {
            return;
        }

        int patternStartPos = format.indexOf(':');
        if (patternStartPos == -1) {
            patternStartPos = format.length();
        }

        StringTokenizer st;
        st = new StringTokenizer(format.substring(0, patternStartPos), ".");
        Object o;
        String argNumber = st.nextToken();

        if (!Character.isDigit(argNumber.charAt(0))) {
            throw new FormatterException("Error: argument number must be "
                                         + "non-negative integer, containing"
                                         + " only digits.");
        }

        try {
            o = args[Integer.parseInt(argNumber)];
        } catch (Exception ex) {
            throw new FormatterException("Error: invalid argument number.", ex);
        }

        while(st.hasMoreTokens()) {
            o = getField(o, st.nextToken());
        }

        if (o == null) {
            return;
        }

        if (patternStartPos == format.length()) {
            sb.append(o.toString());
            return;
        }

        StringFormatterExtension formatterExtension = null;
        for (int i = 0; i < extensions.size(); ++i) {
            if (extensions.get(i).supports(o.getClass())) {
                formatterExtension = extensions.get(i);
                break;
            }
        }

        if (formatterExtension == null) {
            throw new FormatterException("Error: no suitable extensions.");
        }

        formatterExtension.format(sb, o, format.substring(patternStartPos + 1));
    }

    public Object getField(Object o, String fieldName) throws FormatterException {

        if (o == null) {
            return null;
        }

        Class parent = o.getClass();
        try {
            while (parent != null) {
                try {
                    Field field = parent.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    return field.get(o);
                } catch (NoSuchFieldException nsfex) {
                    parent = parent.getSuperclass();
                }
            }
        } catch (IllegalAccessException iaex) {
            throw new FormatterException("Error: unable to get field: "
                                         + fieldName, iaex);
        }
        return null;
    }

}
