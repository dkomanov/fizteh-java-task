package ru.fizteh.fivt.students.yushkevichAnton.shell.commands;

import ru.fizteh.fivt.students.yushkevichAnton.shell.IOCommand;
import ru.fizteh.fivt.students.yushkevichAnton.shell.MovableFile;

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