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
            throw new FormatterException("Null pointer.", new Throwable());
        }
        try {
            extensions.add(extension);
        } catch (Throwable t) {
            throw new FormatterException(t.getMessage(), new Throwable());
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
        throw new FormatterException("Extension not find.", new Throwable());
    }

    private void formatWithArray(StringBuilder buffer, String format, Object[] args) throws FormatterException {
        if (args == null) {
            throw new FormatterException("Args is null.", new Throwable());
        }
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
                            throw new FormatterException("Incorrect format.", new Throwable());
                        }
                    } else {
                        buffer.append(c);
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
                                    buffer.append("");
                                } else {
                                    buffer.append(object.toString());
                                }
                                isArgument = false;
                                objectGet = false;
                                pattern = false;
                                field = false;
                            }
                        } else if (!Character.isDigit(c)) {
                            throw new FormatterException("Incorrect format.", new Throwable());
                        }
                    } else {
                        if (field) {
                            if (c == '.' || c == ':' || c == '}') {
                                boolean notFound;
                                Field fieldObject;
                                if (object != null) {
                                    Class clazz = object.getClass();
                                    String nameOfField = format.substring(numOfFieldPosition, i);
                                    do {
                                        notFound = false;
                                        try {
                                            fieldObject = clazz.getDeclaredField(nameOfField);
                                        } catch (NoSuchFieldException e) {
                                            notFound = true;
                                            fieldObject = null;
                                            clazz = clazz.getSuperclass();
                                            if (clazz == null) { // Hasn't superclass
                                                buffer.append("");
                                                break;
                                            }
                                        }
                                    } while (notFound);
                                    if (fieldObject != null) {
                                        fieldObject.setAccessible(true);
                                        object = fieldObject.get(object);
                                    } else {
                                        object = null;
                                    }
                                }
                                if (c == ':') {
                                    field = false;
                                    pattern = true;
                                    numOfPatternPosition = i + 1;
                                } else if (c == '}') {
                                    if (object == null) {
                                        buffer.append("");
                                    } else {
                                        buffer.append(object.toString());
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
                                    buffer.append("");
                                } else {
                                    if (!supported(object.getClass())) {
                                        throw new FormatterException("Type doesn't supported.", new Throwable());
                                    }
                                    getExtension(object).format(buffer, object, format.substring(numOfPatternPosition, i));
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
            throw new FormatterException(t.getMessage(), t);
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
        formatWithArray(buffer, format, args);
    }
}
