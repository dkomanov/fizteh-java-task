package misc.shell.commands;

import misc.shell.IOCommand;
import misc.shell.MovableFile;

public class Pwd extends IOCommand {
    public Pwd(MovableFile position) {
        super(null, position);
    }

    @Override
    public boolean execute() {
        System.out.println(position.getFile().getAbsolutePath());
        return true;
    }
}