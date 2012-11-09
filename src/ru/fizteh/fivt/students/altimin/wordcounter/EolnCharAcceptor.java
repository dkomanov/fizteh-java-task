package ru.fizteh.fivt.students.altimin.wordcounter;

/**
 * User: altimin
 * Date: 10/30/12
 * Time: 5:55 PM
 */
public class EolnCharAcceptor implements CharAcceptor {
    @Override
    public boolean isAccepted(char c) {
        return c != '\n';
    }
}
