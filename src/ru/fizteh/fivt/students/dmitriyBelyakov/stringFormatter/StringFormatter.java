package ru.fizteh.fivt.students.dmitriyBelyakov.stringFormatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

import java.util.HashMap;

public class StringFormatter implements ru.fizteh.fivt.format.StringFormatter {
    HashMap<Class<?>, StringFormatterExtension> extensions;

    StringFormatter() {
        extensions = new HashMap<>();
    }

    public void addExtension(Class<?> clazz, StringFormatterExtension extension) throws FormatterException {
        if (clazz == null || extension == null) {
            throw new FormatterException("Null pointer.");
        }
        if (!extension.supports(clazz)) {
            throw new FormatterException("This extension doesn't supports this class.");
        }
        try {
            extensions.put(clazz, extension);
        } catch (Throwable t) {
            throw new FormatterException(t.getMessage());
        }
    }

    public boolean supported(Class<?> clazz) {
        return extensions.containsKey(clazz);
    }

    private void formatWithArray(StringBuilder buffer, String format, Object[] args) throws FormatterException {
        try {
            boolean isArgument = false;
            boolean objectGet = false;
            boolean field = false;
            boolean pattern = true;
            int numOfObjectPosition = 0;
            Object object;
            for (int i = 0; i < format.length(); ++i) {
                char c = format.charAt(i);
                if (!isArgument) {
                    if (c == '{') {
                        if (format.charAt(i + 1) == '{') {
                            buffer.append('{');
                            ++i;
                        } else {
                            isArgument = true;
                            numOfObjectPosition = i + 1;
                        }
                    } else if (c == '}') {
                        if (format.charAt(i + 1) == '}') {
                            buffer.append('}');
                            ++i;
                        } else {
                            throw new FormatterException("Incorrect format.");
                        }
                    } else {
                        buffer.append(c);
                    }
                } else {
                    if (!objectGet) {
                        if (c == '.' || c == ':' || c == '}') {
                            try {
                                int argumentNumber = Integer.parseInt(format.substring(numOfObjectPosition, i));
                                object = args[argumentNumber];
                                objectGet = true;
                                if (c == '.') {
                                    field = true;
                                } else if (c == ':') {
                                    pattern = true;
                                } else {
                                    buffer.append(object.toString());
                                    isArgument = false;
                                    objectGet = false;
                                    pattern = false;
                                    field = false;
                                }
                            } catch (Throwable t) {
                                throw new FormatterException(t.getMessage());
                            }
                        } else if (!Character.isDigit(c)) {
                            throw new FormatterException("Incorrect format.");
                        }
                    } else {
                        if (field) {
                            if(c == '.' || c == ':' || c == '}') {
                                //object = object.getClass().getField(format.substring())
                            }
                        } else if (pattern) {
                            // TODO pattern
                        }
                    }
                }
            }
        } catch (Throwable t) {
            throw new FormatterException(t.getMessage());
        }
    }

    @Override
    public String format(String format, Object... args) throws FormatterException {
        StringBuilder builder = new StringBuilder();
        format(builder, format, args);
        return builder.toString();
    }

    @Override
    public void format(StringBuilder buffer, String format, Object... args) {
        formatWithArray(buffer, format, args);
    }
}