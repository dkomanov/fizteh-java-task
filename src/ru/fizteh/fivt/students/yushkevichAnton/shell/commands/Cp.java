package ru.fizteh.fivt.students.yushkevichAnton.shell.commands;

import ru.fizteh.fivt.students.yushkevichAnton.shell.IOCommand;
import ru.fizteh.fivt.students.yushkevichAnton.shell.MovableFile;

import java.io.*;

public class Cp extends IOCommand {
    public Cp(String[] arguments, MovableFile position) {
        super(arguments, position);
    }

    @Override
    public boolean execute() {
        if (arguments.length < 2) {
            System.err.println("Incorrect syntax. You should use it like:");
            System.err.println("cp <from> <to>");
            return false;
        }

        File from = new File(arguments[0]);
        if (!from.isAbsolute()) {
            from = new File(position.getFile().getPath() + File.separator + arguments[0]);
        }

        File to = new File(arguments[1]);
        if (!to.isAbsolute()) {
            to = new File(position.getFile().getPath() + File.separator + arguments[1]);
        }

        return copy(from, to);
    }

    private boolean copy(File from, File to) {
        boolean ok = true;
        if (!from.exists()) {
            System.err.println("Could not find " + from.getPath() + ".");
            return false;
        }

        if (from.isFile()) {
            ok &= atomicCopy(from, to);
        } else {
            if (!to.mkdirs()) {
                System.err.println("Could not create " + to.getPath() + ".");
                return false;
            }
            String[] files = from.list();
            for (String s : files) {
                ok &= copy(new File(from.getPath() + File.separator + s), new File(to.getPath() + File.separator + s));
            }
        }
        return ok;
    }

    private boolean atomicCopy(File from, File to) {
        InputStreamReader inputStreamReader;
        try {
            inputStreamReader = new InputStreamReader(new FileInputStream(from));
        } catch (FileNotFoundException e) {
            System.err.println("Could not find " + from.getPath() + ".");
            return false;
        }

        OutputStreamWriter outputStreamWriter;
        try {
            outputStreamWriter = new OutputStreamWriter(new FileOutputStream(to));
        } catch (FileNotFoundException e) {
            System.err.println("Could not find " + to.getPath() + ".");
            return false;
        }

        while (true) {
            int a = 0;
            try {
                a = inputStreamReader.read();
            } catch (IOException e) {
                System.err.println("Could not read from " + from.getPath() + ".");
                return false;
            }

            if (a == -1) {
                break;
            }

            try {
                outputStreamWriter.write(a);
            } catch (IOException e) {
                System.err.println("Could not write to " + to.getPath() + ".");
                return false;
            }
        }

        boolean ok = true;

        try {
            inputStreamReader.close();
        } catch (IOException e) {
            System.err.println("Could not close " + from.getPath() + ".");
            ok = false;
        }

        try {
            outputStreamWriter.close();
        } catch (IOException e) {
            System.err.println("Could not close " + to.getPath() + ".");
            ok = false;
        }
        return ok;
    }
}