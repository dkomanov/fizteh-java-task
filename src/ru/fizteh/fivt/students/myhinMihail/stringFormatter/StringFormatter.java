package ru.fizteh.fivt.students.myhinMihail.stringFormatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

import java.lang.reflect.Field;
import java.util.*;

public class StringFormatter implements ru.fizteh.fivt.format.StringFormatter {
    public List<StringFormatterExtension> extensions = new ArrayList<StringFormatterExtension>();
    public int position = 0;

    public void addToExtensions(StringFormatterExtension ext) throws FormatterException {
        if (ext == null) {
            throw new FormatterException("NULL extension");
        }
        
        try {
            extensions.add(ext);
        } catch (Exception expt) {
            throw new FormatterException(expt.getMessage());
        }
    }

    public String format(String format, Object... args) throws FormatterException {
        StringBuilder buffer = new StringBuilder();
        format(buffer, format, args);
        
        return buffer.toString();
    }

    public void format(StringBuilder buffer, String format, Object... args) throws FormatterException {
        position = 0;
        nextFormat(buffer, format, args);
    }
    
    public void nextFormat(StringBuilder buffer, String format, Object... args) throws FormatterException {
        int start = format.indexOf('{', position);
        int stop = format.indexOf('}', position);
          
        if (stop == -1  &&  start == -1) {
            buffer.append(format.substring(position));
            return;
        }
          
        if (start == -1) {
            start = format.length();
        }
          
        if (stop == -1) {
            stop = format.length();
        }
          
        if (stop > start) {
            if (start != format.length()  &&  format.charAt(start + 1) == '{') {
                buffer.append(format.substring(position, start + 1));
                position = start + 2;
                nextFormat(buffer, format, args);
            } else {
                if (stop == format.length()) {
                    throw new FormatterException("Brackets don't coincide");
                }
                  
                if (stop == start + 1) {
                    throw new FormatterException("No index");
                }
                  
                buffer.append(format.substring(position, start));
                getField(buffer, format.substring(start + 1, stop), args);
                position = stop + 1;
                nextFormat(buffer, format, args);
            }
        } else {
            if (stop != format.length()  &&  format.charAt(stop + 1) == '}') {
                buffer.append(format.substring(position, stop + 1));
                position = stop + 2;
                nextFormat(buffer, format, args);
            } else {
                throw new FormatterException("Brackets don't coincide");
            }
        }
    }

    public void getField(StringBuilder buffer, String format, Object... args) throws FormatterException {
        Object result = null;
        int pattern = format.indexOf(':');
        if (pattern == -1) {
            pattern = format.length();
        }
        
        StringTokenizer field = new StringTokenizer(format.substring(0, pattern), ".");
        try {
            result = args[Integer.parseInt(field.nextToken())];
            while (field.hasMoreTokens()) {
                result = getFieldFromObject(result, field.nextToken());
            }
            
            if (result == null) {
                return;
            }
            
        } catch (Exception expt) {
            throw new FormatterException("Bad index");
        }
        
        if (pattern == format.length()) {
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
                extension.format(buffer, result, format.substring(pattern + 1));
            } else {
                throw new FormatterException("No extension for " + result.getClass());
            }
        }
        
    }

    public Object getFieldFromObject(Object obj, String field) throws FormatterException {
        if (obj == null) {
            return null;
        }
        
        try {
            Class<?> parent = obj.getClass();
            while (parent != null) {
                try {
                    Field f = parent.getDeclaredField(field);
                    f.setAccessible(true);
                    return f.get(obj);
                } catch (NoSuchFieldException expt) {
                    parent = parent.getSuperclass();
                }
            }
            
            return null;
        } catch (Exception expt) {
            throw new FormatterException("Can not access field " + field + " in " + obj.getClass());
        }
    }
    
}
