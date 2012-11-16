package ru.fizteh.fivt.students.yushkevichAnton.shell.commands;

import ru.fizteh.fivt.students.yushkevichAnton.shell.IOCommand;
import ru.fizteh.fivt.students.yushkevichAnton.shell.MovableFile;

import java.io.*;

public class Cd extends IOCommand {
    public Cd(String[] arguments, MovableFile movableFile) {
        super(arguments, movableFile);
    }

    @Override
    public boolean execute() {
        if (arguments.length == 0) {
            System.err.println("Incorrect syntax. You should use it like:");
            System.err.println("cd <relative path | absolute path>");
            return false;
        }
        String cd = arguments[0];
        return position.move(cd);
    }
}