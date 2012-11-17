package ru.fizteh.fivt.students.nikitaAntonov.utils;

public class IncorrectUsageException extends ConsoleAppException {

    private static final long serialVersionUID = 3825224109701172462L;
    private static final String prefix = "Incorrect number of parameters\n"
            + "Usage: ";

    public IncorrectUsageException(String message) {
        super(prefix + message);
    }
}