package ru.fizteh.fivt.students.fedyuninV.format;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

import java.util.List;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class StringFormatter implements ru.fizteh.fivt.format.StringFormatter{

    private List<StringFormatterExtension> extensions;

    public StringFormatter(List<StringFormatterExtension> extensions) {
        this.extensions = extensions;
    }

    public String format(String format, Object... args)
            throws FormatterException {
        StringBuilder buffer = new StringBuilder();
        format(buffer, format, args);
        return buffer.toString();
    }

    public void format(StringBuilder buffer, String format, Object... args)
            throws FormatterException {
        parse(buffer, format, 0, args);
    }

    private void parse(StringBuilder buffer, String format, int leftBound, Object... args)
            throws FormatterException {
        int openBracket = format.indexOf('{', leftBound);
        int closeBracket = format.indexOf('}', leftBound);
        if (openBracket == -1) {
            openBracket = format.length();
        }
        if (closeBracket == -1) {
            closeBracket = format.length();
        }
        if (closeBracket == format.length()  &&  openBracket == format.length()) {
            buffer.append(format.substring(leftBound));
            return;
        }
        if (closeBracket >= openBracket) {  // format looks like  "*" or "*{*" or "*{*}*"
            buffer.append(format.substring(leftBound, openBracket));
            if (openBracket + 1 < format.length()  &&  format.charAt(openBracket + 1) == '{') {
                buffer.append('{');
                parse(buffer, format, openBracket + 2, args);
            } else {
                if (closeBracket == openBracket + 1  ||  closeBracket == format.length()) {
                    throw new FormatterException("Incorrect brackets sequence");
                }
                parseArg(buffer, format.substring(openBracket + 1, closeBracket), args);
                parse(buffer, format, closeBracket + 1, args);
            }
        } else {  // format looks like "*}*" or "*}*{*"
            if (closeBracket + 1 < format.length()  &&  format.charAt(closeBracket + 1) == '}') {
                buffer.append(format.substring(leftBound, closeBracket + 1));
                parse(buffer, format, closeBracket + 2, args);
            } else {
                throw new FormatterException("Incorrect bracket sequence");
            }
        }
    }

    private void parseArg(StringBuilder buffer, String format, Object... args) {
        int patternBegin = format.indexOf(':');
        if (patternBegin == -1) {
            patternBegin = format.length();
        }
        Object finalArg;
        String[] fields = format.substring(0, patternBegin).split("[.]");
        try {
            int argNum = Integer.parseInt(fields[0]);
            finalArg = args[argNum];
            for (int i = 1; i < fields.length; i++) {
                finalArg = finalArg.getClass().getField(fields[i]).get(finalArg);
            }
        } catch (Exception ex) {
            throw new FormatterException("Incorrect term in brackets");
        }
        if (patternBegin == format.length() - 1) {
            throw new FormatterException("Void pattern with ':'");
        }
        if (finalArg == null) {
            buffer.append("");
            return;
        }
        boolean extNotFound = true;
        if (patternBegin == format.length()) {
            patternBegin--;
        }
        synchronized (extensions) {
            for (StringFormatterExtension extension: extensions) {
                if (extension.supports(finalArg.getClass())) {
                    extNotFound = false;
                    extension.format(buffer, finalArg, format.substring(patternBegin + 1));
                }
            }
        }
        if (extNotFound) {
            throw new FormatterException("There is no relative extension");
        }
    }
}
