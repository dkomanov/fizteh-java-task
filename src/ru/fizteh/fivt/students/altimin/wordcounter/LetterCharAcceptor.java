package ru.fizteh.fivt.students.altimin.wordcounter;

/**
 * User: altimin
 * Date: 10/30/12
 * Time: 6:02 PM
 */
public class LetterCharAcceptor implements CharAcceptor {
    @Override
    public boolean isAccepted(char c) {
        return Character.isAlphabetic(c) || Character.isDigit(c) || c == '_' || c == '-';
    }
}
