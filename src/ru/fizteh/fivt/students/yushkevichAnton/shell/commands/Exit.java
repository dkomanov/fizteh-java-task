package ru.fizteh.fivt.students.yushkevichAnton.shell.commands;

import ru.fizteh.fivt.students.yushkevichAnton.shell.Command;

import java.io.*;

public class Exit extends Command {
    public Exit() {
        super(null);
    }

    @Override
    public boolean execute() {
        System.exit(0);
        return true;
    }
}