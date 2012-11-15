package ru.fizteh.fivt.students.altimin.wordcounter;

import java.io.IOException;

/**
 * User: altimin
 * Date: 10/30/12
 * Time: 2:57 PM
 */

public interface InputParser {
    public boolean isEndOfInput();

    public String nextToken();
}
