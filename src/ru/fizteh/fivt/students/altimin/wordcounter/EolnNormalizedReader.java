package ru.fizteh.fivt.students.altimin.wordcounter;

import java.io.IOException;
import java.io.Reader;

/**
 * User: altimin
 * Date: 11/6/12
 * Time: 4:04 PM
 */

public class EolnNormalizedReader extends Reader {
    private final Reader reader;

    private boolean hasUngetValue;
    private int ungetValue;

    private void unget(int value) {
        ungetValue = value;
        hasUngetValue = true;
    }


    public EolnNormalizedReader(Reader reader) {
        this.reader = reader;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    @Override
    public int read() throws IOException {
        int value;
        if (hasUngetValue) {
            value = ungetValue;
            hasUngetValue = false;
        } else {
            value = reader.read();
        }
        if (value == '\r') { // normalizing bad eoln char
            value = reader.read();
            if (value == '\n') { //Windows-style eoln
                return '\n';
            } else { // MACOS-style eoln
                unget(value);
                return '\n';
            }
        }
        return value;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        for (int i = 0; i < len; i ++) {
            int value = read();
            if (value == -1)
                return i;
            cbuf[off + i] = (char)value;
        }
        return len;
    }
}
