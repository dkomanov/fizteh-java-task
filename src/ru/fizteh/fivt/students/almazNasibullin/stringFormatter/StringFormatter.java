package ru.fizteh.fivt.students.almazNasibullin.stringFormatter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

/**
 * 19.11.12
 * @author almaz
 */

public class StringFormatter implements ru.fizteh.fivt.format.StringFormatter {
    private List<StringFormatterExtension> extensions = Collections.synchronizedList(
            new ArrayList<StringFormatterExtension>());
            
    public void addExtension(StringFormatterExtension sfe) throws FormatterException {
        if (sfe == null) {
            throw new FormatterException("NullPointer extension");
        }
        try {
            extensions.add(sfe);
        } catch (Exception e) {
            throw new FormatterException("Impossible to add the extension");
        }
    }
    
    @Override
    public String format(String format, Object... args) throws FormatterException {
        StringBuilder sb = new StringBuilder();
        format(sb, format, args);
        return sb.toString();
    }

    @Override
    public void format(StringBuilder buffer, String format, Object... args) throws FormatterException {
        myFormat(buffer, format, 0, args);
    }

    public void myFormat(StringBuilder buffer, String format, int index, Object... args)
            throws FormatterException {
        if (index >= format.length()) {
            return;
        }
        
        int open = format.indexOf("{", index);
        int close = format.indexOf("}", index);

        if (open == -1) {
            open = format.length();
        }
        if (close == -1) {
            close = format.length();
        }

        if (open == format.length() && close == format.length()) {
            buffer.append(format.substring(index));
            return;
        }


        if (open + 1 < format.length() && open < close && format.charAt(open + 1) == '{') {
            buffer.append(format.substring(index, open + 1));
            index = open + 2;
            myFormat(buffer, format, index, args);
            return;
        }
        if (close + 1 < format.length() && close < open && format.charAt(close + 1) == '}') {
            buffer.append(format.substring(index, close + 1));
            index = close + 2;
            myFormat(buffer, format, index, args);
            return;
        }

        if (open < close) {
            if (close == format.length()) {
                throw new FormatterException("Incorrect arrangement of the brackets");
            }
            if (open + 1 == close) {
                throw new FormatterException("No index in format");
            }

            buffer.append(format.substring(index, open));
            makeChange(buffer, format.substring(open + 1, close), args);
            index = close + 1;
            myFormat(buffer, format, index, args);
        } else {
            throw new FormatterException("Incorrect arrangement of the brackets");
        }
    }

    private void makeChange(StringBuilder buffer, String format, Object... args)
            throws FormatterException {
        if (args == null) {
            return;
        }
        Object o = null;
        int pattern = format.indexOf(":");
        if (pattern == -1) {
            pattern = format.length();
        }
        StringTokenizer st = new StringTokenizer(format.substring( 0, pattern), ".");

        try {
            String ind = st.nextToken();
            if (Character.isDigit(ind.charAt(0))) {
                throw new FormatterException("Incorrect index");
            }
            o = args[Integer.parseInt(ind)];
        } catch (Exception e) {
            throw new FormatterException("Incorrect index");
        }

        while(st.hasMoreTokens()) {
            o = getField(o, st.nextToken());
        }
        if (o == null) {
            return;
        }

        if (pattern == format.length()) {
            buffer.append(o.toString());
        } else {
            StringFormatterExtension sfe = null;

            for (int i = 0; i < extensions.size(); ++i) {
                if (extensions.get(i).supports(o.getClass())) {
                    sfe = extensions.get(i);
                    break;
                }
            }
            
            if (sfe == null) {
                throw new FormatterException("No available extension");
            }
            sfe.format(buffer, o, format.substring(pattern + 1));
        }
    }

    private Object getField(Object o, String field) throws FormatterException {
        if (o == null) {
            return null;
        }
        Class<?> c = o.getClass();
        while (c != null) {
            try {
                Field f = c.getDeclaredField(field);
                f.setAccessible(true);
                return f.get(o);
            } catch (NoSuchFieldException nsfe) {
                c = c.getSuperclass();
            } catch (IllegalAccessException iae) {
                throw new FormatterException("No access to field: " + field);
            }
        }
        return null;
    }
}
