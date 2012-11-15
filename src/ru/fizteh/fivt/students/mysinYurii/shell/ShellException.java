package ru.fizteh.fivt.students.mysinYurii.shell;

public class ShellException extends Exception {
    ShellException(String task, String s) {
        super(task + ": " + s);
    }
}
