package ru.fizteh.fivt.students.altimin.formatter;

/**
 * User: altimin
 * Date: 11/30/12
 * Time: 10:39 PM
 */
public class FormatterException extends Exception{
    public FormatterException() {
    }

    public FormatterException(String message) {
        super(message);
    }

    public FormatterException(String message, Throwable cause) {
        super(message, cause);
    }

    public FormatterException(Throwable cause) {
        super(cause);
    }

    public FormatterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
