package ru.fizteh.fivt.students.dmitriyBelyakov.stringFormatter;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class StringFormatter implements ru.fizteh.fivt.format.StringFormatter {
    ArrayList<StringFormatterExtension> extensions;

    StringFormatter() {
        extensions = new ArrayList<>();
    }

    public void addExtension(StringFormatterExtension extension) throws FormatterException {
        if (extension == null) {
            throw new FormatterException("Null pointer.");
        }
        try {
            extensions.add(extension);
        } catch (Throwable t) {
            throw new FormatterException(t.getMessage());
        }
    }

    public boolean supported(Class<?> clazz) {
        for (StringFormatterExtension extension : extensions) {
            if (extension.supports(clazz)) {
                return true;
            }
        }
        return false;
    }

    public StringFormatterExtension getExtension(Object object) {
        for (StringFormatterExtension extension : extensions) {
            if (extension.supports(object.getClass())) {
                return extension;
            }
        }
        throw new FormatterException("Extension not find.");
    }

    private void formatWithArray(StringBuilder buffer, String format, Object[] args) throws FormatterException {
        if (args == null) {
            throw new FormatterException("Args is null.");
        }
        StringBuilder builder = new StringBuilder();
        try {
            boolean isArgument = false;
            boolean objectGet = false;
            boolean field = false;
            boolean pattern = true;
            int numOfObjectPosition = 0;
            int numOfFieldPosition = 0;
            int numOfPatternPosition = 0;
            Object object = null;
            for (int i = 0; i < format.length(); ++i) {
                char c = format.charAt(i);
                if (!isArgument) {
                    if (c == '{') {
                        if (format.charAt(i + 1) == '{') {
                            builder.append('{');
                            ++i;
                        } else {
                            isArgument = true;
                            numOfObjectPosition = i + 1;
                        }
                    } else if (c == '}') {
                        if (format.charAt(i + 1) == '}') {
                            builder.append('}');
                            ++i;
                        } else {
                            throw new FormatterException("Incorrect format.");
                        }
                    } else {
                        builder.append(c);
                    }
                } else {
                    if (!objectGet) {
                        if (c == '.' || c == ':' || c == '}') {
                            int argumentNumber = Integer.parseInt(format.substring(numOfObjectPosition, i));
                            object = args[argumentNumber];
                            objectGet = true;
                            if (c == '.') {
                                field = true;
                                numOfFieldPosition = i + 1;
                            } else if (c == ':') {
                                pattern = true;
                                numOfPatternPosition = i + 1;
                            } else {
                                if (object == null) {
                                    return;
                                } else {
                                    builder.append(object.toString());
                                }
                                isArgument = false;
                                objectGet = false;
                                pattern = false;
                                field = false;
                            }
                        } else if (!Character.isDigit(c)) {
                            throw new FormatterException("Incorrect format.");
                        }
                    } else {
                        if (field) {
                            if (c == '.' || c == ':' || c == '}') {
                                Field fieldObject = object.getClass().getDeclaredField(format.substring(numOfFieldPosition, i));
                                fieldObject.setAccessible(true);
                                object = fieldObject.get(object);
                                fieldObject.setAccessible(false);
                                if (object == null) {
                                    throw new FormatterException("Null pointer field.");
                                }
                                if (c == ':') {
                                    field = false;
                                    pattern = true;
                                    numOfPatternPosition = i + 1;
                                } else if (c == '}') {
                                    if (object == null) {
                                        return;
                                    } else {
                                        builder.append(object.toString());
                                    }
                                    isArgument = false;
                                    objectGet = false;
                                    pattern = false;
                                    field = false;
                                } else if (c == '.') {
                                    numOfFieldPosition = i + 1;
                                }
                            }
                        } else if (pattern) {
                            if (c == '}') {
                                if (object == null) {
                                    return;
                                } else {
                                    if (!supported(object.getClass())) {
                                        throw new FormatterException("Type doesn't supported.");
                                    }
                                    getExtension(object).format(builder, object, format.substring(numOfPatternPosition, i));
                                }
                                isArgument = false;
                                objectGet = false;
                                pattern = false;
                                field = false;
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            throw new FormatterException(t.getMessage());
        }
        buffer.append(builder.toString());
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
        formatWithArray(buffer, format, args);
    }
}
