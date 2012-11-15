package misc.shell.commands;

import misc.shell.Command;

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