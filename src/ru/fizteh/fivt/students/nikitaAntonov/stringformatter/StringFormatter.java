package ru.fizteh.fivt.students.nikitaAntonov.stringformatter;

import java.util.ArrayList;

import ru.fizteh.fivt.format.FormatterException;
import ru.fizteh.fivt.format.StringFormatterExtension;

public class StringFormatter implements ru.fizteh.fivt.format.StringFormatter {

    ArrayList<StringFormatterExtension> extensions;

    public StringFormatter(ArrayList<StringFormatterExtension> exList) {
        if (exList == null) {
            throw new FormatterException(new NullPointerException());
        }

        extensions = exList;
    }

    @Override
    public String format(String format, Object... args)
            throws FormatterException {
        StringBuilder buf = new StringBuilder();
        format(buf, format, args);

        return buf.toString();
    }

    @Override
    public void format(StringBuilder buffer, String format, Object... args)
            throws FormatterException {
        
        int pos = 0;
        int end = format.length();
        
        int openBracketPos = format.indexOf('{');
        int closeBracketPos = format.indexOf('}');
        while (openBracketPos != -1 || closeBracketPos != -1) {
            
            while (closeBracketPos != -1 && (closeBracketPos < openBracketPos || openBracketPos == -1)) {
                if (closeBracketPos != end - 1 && format.charAt(closeBracketPos + 1) == '}') {
                    buffer.append(format.substring(pos, closeBracketPos + 1));
                    pos = closeBracketPos + 2;
                    closeBracketPos = format.indexOf('}', pos);
                } else {
                    throw new FormatterException("Unexpected closed bracket");
                }
            }
            
            if (openBracketPos == -1) {
                break;
            }
            
            buffer.append(format.substring(pos, openBracketPos));
            pos = openBracketPos + 1;
            
            if (openBracketPos != end - 1 && format.charAt(openBracketPos + 1) == '{') {
                buffer.append('{');
                ++pos;
            } else {
                doFormating(buffer, format.substring(pos, closeBracketPos));
                pos = closeBracketPos + 1;
                closeBracketPos = format.indexOf('}', pos);
            }
  
            openBracketPos = format.indexOf('{', pos);
        }
        
        if (pos != end) {
            buffer.append(format.substring(pos));
        }
        

    }

    private void doFormating(StringBuilder buffer, String substring) {
        // TODO: Исправить, пока для теста
        buffer.append("{наформат: ");
        buffer.append(substring);
        buffer.append("}");
    }

}
