/*V 1.1, to understand that all is right*/
package misc.shell;

import java.io.*;
import java.util.*;

public class Action implements Executable {
    private ArrayList<Command> commands = new ArrayList<Command>();

    public void addCommand(Command command) {
        commands.add(command);
    }

    @Override
    public boolean execute() {
        for (Command command : commands) {
            if (!command.execute()) {
                return false;
            }
        }
        return true;
    }
}