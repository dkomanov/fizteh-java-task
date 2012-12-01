package ru.fizteh.fivt.students.yushkevichAnton.shell.commands;

import ru.fizteh.fivt.students.yushkevichAnton.shell.IOCommand;
import ru.fizteh.fivt.students.yushkevichAnton.shell.MovableFile;

import java.io.*;

public class Rm extends IOCommand {
    public Rm(String[] arguments, MovableFile position) {
        super(arguments, position);
    }

    @Override
    public boolean execute() {
        if (arguments.length != 1) {
            System.err.println("Incorrect syntax. You should use it like:");
            System.err.println("rm <file | directory>");
            return false;
        }

        File file = new File(arguments[0]);
        if (!file.isAbsolute()) {
            file = new File(position.getFile().getAbsolutePath() + File.separator + arguments[0]);
        }

        return delete(file);
    }

    private boolean delete(File file) {
        if (!file.exists()) {
            System.err.println("File not found " + file.getPath() + ".");
            return false;
        }

        boolean ok = true;

        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                ok &= delete(child);
            }
        }

        return file.delete() && ok;
    }
}