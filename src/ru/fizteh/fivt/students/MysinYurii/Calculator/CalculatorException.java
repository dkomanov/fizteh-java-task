package ru.fizteh.fivt.students.MysinYurii.Calculator;

/*
 * Author: Mysin Yurii
 * 
 * Group: 196
 */

public class CalculatorException extends Throwable {
    String exceptionMessage;

    @Override
    public String toString() {
        return exceptionMessage;
    }

    @Override
    public String getMessage() {
        return exceptionMessage;
    }

    public CalculatorException(String message) {
        exceptionMessage = message;
    }

}
