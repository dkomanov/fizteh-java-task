package ru.fizteh.fivt.students.yushkevichAnton.shell.commands;

import ru.fizteh.fivt.students.yushkevichAnton.shell.IOCommand;
import ru.fizteh.fivt.students.yushkevichAnton.shell.MovableFile;

import java.io.*;

public class Dir extends IOCommand {
    public Dir(MovableFile position) {
        super(null, position);
    }

    @Override
    public boolean execute() {
        String[] files = position.getFile().list();
        if (files == null) {
            return false;
        }
        for (String s : files) {
            System.out.println(s);
        }
        return true;
    }
}