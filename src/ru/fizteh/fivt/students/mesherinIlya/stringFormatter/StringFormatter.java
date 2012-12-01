package ru.fizteh.fivt.students.mesherinIlya.stringFormatter;


import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;
import java.lang.reflect.Field;
import java.util.Vector;

public class StringFormatter implements ru.fizteh.fivt.format.StringFormatter {
    Vector<StringFormatterExtension> extensions;

    public StringFormatter() {
        extensions = new Vector<StringFormatterExtension>();
    }

    public void addExtension(StringFormatterExtension extension) throws FormatterException {
        if (extension == null) {
            throw new FormatterException("Null pointer.");
        }
        try {
            extensions.add(extension);
        } catch (Exception e) {
            throw new FormatterException(e.getMessage(), e);
        }
    }

    @Override
    public String format(String format, Object... args) throws FormatterException {
        if (args == null) {
            args = new Object[1];
            args[0] = null;
        }
        StringBuilder builder = new StringBuilder();
        format(builder, format, args);
        return builder.toString();
    }

    @Override
    public void format(StringBuilder buffer, String format, Object... args) {
        if (args == null) {
            args = new Object[1];
            args[0] = null;
        }
        
        boolean thisIsArgument = false;
        int argumentIndex = 0;
        
        boolean thisIsField = false;
        int fieldIndex = 0;
        
        boolean thisIsPattern = true;
        int patternIndex = 0;
        
        Object argument = null;
        
        try {
            //разбор строки
            for (int i = 0; i < format.length(); i++) {
                char c = format.charAt(i);
                if (!thisIsArgument) {
                    switch (c) {
                    case '{': 
                        if (format.charAt(i + 1) == '{') {
                            buffer.append('{');
                            i++;
                        } else {
                            thisIsArgument = true;
                            argumentIndex = i + 1;
                        }
                        break;
                    case '}': 
                        if (format.charAt(i + 1) == '}') {
                            buffer.append('}');
                            i++;
                        } else {
                            throw new FormatterException("Incorrect brackets arrangement.");
                        }
                        break;
                    default:
                        buffer.append(c);
                        break;
                    }
                } else if (!thisIsField && !thisIsPattern) {
                    if (c == '.' || c == ':' || c == '}') {
                        int argumentNumber = Integer.parseInt(format.substring(argumentIndex, i));
                        if (argumentNumber >= args.length) {
                            throw new FormatterException("Argument number is out of range.");
                        }
                        argument = args[argumentNumber];
                        switch (c) {
                        case '.':
                            thisIsField = true;
                            fieldIndex = i + 1;
                            break;
                        case ':':
                            thisIsPattern = true;
                            patternIndex = i + 1;
                            break;
                        default:
                            thisIsArgument = false;
                            thisIsPattern = false;
                            thisIsField = false;
                            
                            buffer.append(argument.toString());
                            break;
                        }
                    } else if (!Character.isDigit(c)) {
                        throw new FormatterException("Incorrect format.");
                    }
                } else if (thisIsField) {
                    if (c == '.' || c == ':' || c == '}') {
                        Field fieldObject = null;
                        if (argument != null) {
                            Class clazz = argument.getClass();
                            String fieldName = format.substring(fieldIndex, i);
                            boolean noSuchField = true;
                            while (noSuchField) {
                                noSuchField = false;
                                try {
                                    fieldObject = clazz.getDeclaredField(fieldName);
                                } catch (NoSuchFieldException e) {
                                    noSuchField = true;
                                    fieldObject = null;
                                    clazz = clazz.getSuperclass();
                                    if (clazz == null) {
                                        break;
                                    }
                                }
                            }
                            if (fieldObject != null) {
                                fieldObject.setAccessible(true);
                                argument = fieldObject.get(argument);
                            } else {
                                argument = null;
                            }
                        }
                        switch (c) {
                        case ':':
                            thisIsField = false;
                            thisIsPattern = true;
                            patternIndex = i + 1;
                            break;
                        case '}':
                            buffer.append(argument.toString());
                            
                            thisIsArgument = false;
                            thisIsPattern = false;
                            thisIsField = false;
                            break;
                        case '.':
                            fieldIndex = i + 1;
                            break;
                        }
                    }
                } else if (thisIsPattern && c == '}') {
                
                    thisIsArgument = false;
                    thisIsPattern = false;
                    thisIsField = false;
            
                    if (argument != null) {
                        boolean argumentIsSupported = false;
                        
                        for (StringFormatterExtension extension : extensions) {
                            if (extension.supports(argument.getClass())) {
                                extension.format(buffer, argument, format.substring(patternIndex, i));
                                argumentIsSupported = true;
                                break;
                            }
                        }
                        
                        if (!argumentIsSupported) {
                            throw new FormatterException("The type isn't supported.");
                        }
                    }
                }   
            }
        } catch (Exception e) {
            throw new FormatterException(e.getMessage(), e);
        }
    }
}



