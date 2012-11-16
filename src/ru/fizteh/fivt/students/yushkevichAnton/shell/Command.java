package misc.shell;

public abstract class Command implements Executable {
    protected String[] arguments;

    public Command(String[] arguments) {
        this.arguments = arguments;
    }
}