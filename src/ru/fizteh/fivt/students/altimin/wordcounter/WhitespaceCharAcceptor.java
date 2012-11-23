package ru.fizteh.fivt.students.altimin.wordcounter;

/**
 * User: altimin
 * Date: 10/30/12
 * Time: 5:53 PM
 */
public class WhitespaceCharAcceptor implements CharAcceptor {
    public boolean isAccepted(char c) {
        return !(Character.isWhitespace(c));
    }
}
