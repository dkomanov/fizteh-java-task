package ru.fizteh.fivt.students.yushkevichAnton.shell.commands;

import ru.fizteh.fivt.students.yushkevichAnton.shell.IOCommand;
import ru.fizteh.fivt.students.yushkevichAnton.shell.MovableFile;

import java.io.*;

public class Mkdir extends IOCommand {
    public Mkdir(String[] arguments, MovableFile position) {
        super(arguments, position);
    }

    @Override
    public boolean execute() {
        if (arguments.length == 0) {
            System.out.println("Incorrect syntax. You should use it like:");
            System.out.println("mkdir <directory name>");
            return false;
        }

        File dir = new File(position.getFile().getAbsolutePath() + File.separator + arguments[0]);
        return dir.mkdirs();
    }
}