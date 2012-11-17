package ru.fizteh.fivt.students.kashinYana.stringFormator;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class StringFormatter implements ru.fizteh.fivt.format.StringFormatter {

    private List<StringFormatterExtension> template;

    public StringFormatter(ArrayList<StringFormatterExtension> ext) {
        template = ext.subList(0, ext.size());
    }

    @Override
    public String format(String inputString, Object... args)
            throws FormatterException {
        StringBuilder answer = new StringBuilder();
        format(answer, inputString, args);
        return answer.toString();
    }

    @Override
    public void format(StringBuilder buffer, String inputString, Object... args) throws FormatterException {
        format(buffer, inputString, 0, args);
    }

    private void format(StringBuilder answer, String string, int startPosition, Object... args)
            throws FormatterException {

        int firstOpen = string.indexOf('{', startPosition);
        int firstClose = string.indexOf('}', startPosition);

        if (firstClose < firstOpen && firstClose != -1 && firstOpen != -1) {
            if (firstClose + 1 < string.length() && string.charAt(firstClose + 1) == '}') {
                answer.append(string.substring(startPosition, firstClose + 1));
                format(answer, string, firstClose + 2, args);
            } else {
                throw new FormatterException("Strange }");
            }
        } else if (firstClose == -1 && firstOpen == -1) {
            answer.append(string.substring(startPosition));
        } else if (firstClose != -1 && firstOpen == -1) {
            if (firstClose + 1 < string.length() && string.charAt(firstClose + 1) == '}') {
                answer.append(string.substring(startPosition, firstClose + 1));
                format(answer, string, firstClose + 2, args);
            } else {
                throw new FormatterException("Strange }");
            }
        } else if (firstOpen != -1 && firstClose == -1) {
            if (firstOpen + 1 < string.length() && string.charAt(firstOpen + 1) == '{') {
                answer.append(string.substring(startPosition, firstOpen + 1));
                format(answer, string, firstOpen + 2, args);
            } else {
                throw new FormatterException("Strange {");
            }
        } else {
            if (string.charAt(firstOpen + 1) == '{') {
                answer.append(string.substring(startPosition, firstOpen + 1));
                format(answer, string, firstOpen + 2, args);
            } else {
                answer.append(string.substring(startPosition, firstOpen));
                String token = string.substring(firstOpen + 1, firstClose);
                formatArgument(answer, token, args);
                format(answer, string, firstClose + 1, args);
            }
        }
    }

    private void formatArgument(StringBuilder buffer, String token, Object... args)
            throws FormatterException {

        int endName = token.indexOf(":");

        if (endName == -1) {
            endName = token.length();
        }

        String name = token.substring(0, endName);
        Object object = null;
        object = getField(name, args);

        if (endName == token.length()) {
            if (object != null) {
                buffer.append(object.toString());
            }
        } else {
            if (object != null) {
                String pattern = token.substring(endName + 1);
                useExtension(buffer, pattern, object);
            }
        }
    }

    private Object getField(String string, Object... args)
            throws FormatterException {

        StringTokenizer tokenRead = new StringTokenizer(string, ".");
        if(!tokenRead.hasMoreTokens()) {
            return null;
        }
        String indexInString = tokenRead.nextToken();
        int index = 0;
        Object object;
        try {
            index = Integer.parseInt(indexInString);
            if(indexInString.equals("-0")) {
                throw new FormatterException("-0 not good");
            }
            object = args[index];
        } catch (Exception notNum) {
            throw new FormatterException("Found error number or type of argvs.");
        }
        while (tokenRead.hasMoreTokens()) {
            if (object == null) {
                return object;
            }
            String token = tokenRead.nextToken();
            Class parent = object.getClass();
            boolean isFind = false;
            while (parent != null) {
                try {
                    Field privateStringField = parent.getDeclaredField(token);
                    privateStringField.setAccessible(true);
                    object = privateStringField.get(object);
                    isFind = true;
                    break;
                } catch (Exception e) {
                    parent = parent.getSuperclass();
                }
            }
            if (!isFind) {
                object = null;
                return object;
            }
        }
        return object;
    }

    private void useExtension(StringBuilder buffer, String pattern, Object arg)
            throws FormatterException {

        for (StringFormatterExtension extend : template) {
            if (extend.supports(arg.getClass())) {
                try {
                    extend.format(buffer, arg, pattern);
                } catch (Exception ex) {
                    throw new FormatterException("Error in format extend");
                }
                return;
            }
        }
        throw new FormatterException("Extention not found.");
    }
}
