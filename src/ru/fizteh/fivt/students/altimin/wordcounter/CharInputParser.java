package ru.fizteh.fivt.students.altimin.wordcounter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * User: altimin
 * Date: 10/30/12
 * Time: 4:07 PM
 */

public class CharInputParser implements InputParser {
    private UngetReaderWrapper reader;
    private boolean endOfInputReached;
    private CharAcceptor acceptor;

    public CharInputParser(String fileName, CharAcceptor acceptor) throws FileNotFoundException {
        this.acceptor = acceptor;
        reader = new UngetReaderWrapper(new EOLNNormalizedReader(new FileReader(fileName)));
        endOfInputReached = false;
    }

    private void close() {
        try {
            reader.close();
        }
        catch (Exception e) { // corrupted input stream. So bad.
        }
    }


    @Override
    public boolean isEndOfInput()  {
        if (endOfInputReached) {
            return true;
        }
        try {
            int charValue = reader.read();
            if (charValue == -1) {
                close();
                endOfInputReached = true;
                return true;
            } else {
                reader.unget(charValue);
                return false;
            }
        }
        catch (IOException e) {
            endOfInputReached = true;
            return true;
        }
    }


    private String unsafeNextToken() throws IOException {
        if (endOfInputReached) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        int charValue = reader.read();
        while (charValue != -1 && acceptor.isAccepted((char) charValue)) {
            stringBuilder.append((char) charValue);
            charValue = reader.read();
        }
        if (charValue == -1) {
            endOfInputReached = true;
            close();
        }
        return stringBuilder.toString();
    }

    @Override
    public String nextToken() {
        try {
            return unsafeNextToken();
        }
        catch (IOException e) {
            return "";
        }
    }
}
