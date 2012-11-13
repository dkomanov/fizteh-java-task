package misc.shell;

import java.io.*;
import java.util.*;

public class Action implements Executable {
    private ArrayList<Command> commands = new ArrayList<Command>();

    public void addCommand(Command command) {
        commands.add(command);
    }

    @Override
    public void execute() {
        for (Command command : commands) {
            command.execute();
        }
    }
}