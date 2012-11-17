package ru.fizteh.fivt.students.nikitaAntonov.utils;

public class ConsoleAppException extends Exception {

    private static final long serialVersionUID = -5154101410931907193L;

    public ConsoleAppException(String message) {
        super(message);
    }
    
    public ConsoleAppException(Throwable cause) {
        super(cause);
    }
    
    public ConsoleAppException(String message, Throwable cause) {
        super(message, cause);
    }
}
