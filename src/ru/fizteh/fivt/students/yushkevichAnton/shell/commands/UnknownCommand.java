package ru.fizteh.fivt.students.yushkevichAnton.shell.commands;

import ru.fizteh.fivt.students.yushkevichAnton.shell.Command;

import java.io.*;

public class UnknownCommand extends Command {
    private String name;

    public UnknownCommand(String name) {
        super(null);
        this.name = name;
    }

    @Override
    public boolean execute() {
        System.err.println("Unknown command " + name + ".");
        return false;
    }
}