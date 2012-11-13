package misc.shell;

public abstract class IOCommand extends Command {
    private IOCommand(String[] arguments) {
        super(arguments);
    }

    protected MovableFile position;

    public IOCommand(String[] arguments, MovableFile position) {
        super(arguments);
        this.position = position;
    }
}