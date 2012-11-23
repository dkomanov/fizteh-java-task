package ru.fizteh.fivt.students.altimin.wordcounter;

import java.io.IOException;
import java.io.Reader;
import java.util.Stack;

/**
 * User: altimin
 * Date: 11/7/12
 * Time: 2:28 PM
 */
public class UngetReaderWrapper extends Reader {
    private Reader reader;
    private Stack<Integer> ungetStack;
    UngetReaderWrapper(Reader reader) {
        this.reader = reader;
        ungetStack = new Stack<Integer>();
    }
    public void unget(int charValue) {
        ungetStack.push(charValue);
    }
    @Override
    public int read() throws IOException {
        if (!ungetStack.empty()) {
            return ungetStack.pop();
        }
        else
            return reader.read();
    }
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        for (int i = 0; i < len; i ++) {
            int value = read();
            if (value == -1)
                return i;
            cbuf[off + i] = (char) value;
        }
        return len;
    }
    @Override
    public void close() throws IOException {
        reader.close();
    }
}
