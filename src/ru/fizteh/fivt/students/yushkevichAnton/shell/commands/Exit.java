package ru.fizteh.fivt.students.yushkevichAnton.shell.commands;

import ru.fizteh.fivt.students.yushkevichAnton.shell.Command;

import java.io.*;

public class Exit extends Command {
    public Exit(String[] arguments) {
        super(arguments);
    }

    @Override
    public boolean execute() {
        if (arguments.length != 0) {
            System.err.println("Incorrect syntax. You should use it like:");
            System.err.println("exit");
            return false;
        }

        System.exit(0);
        return true;
    }
}