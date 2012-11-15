package misc.shell.commands;

import misc.shell.Command;

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