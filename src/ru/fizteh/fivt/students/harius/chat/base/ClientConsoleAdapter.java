package ru.fizteh.fivt.students.harius.chat.base;

public abstract class ClientConsoleAdapter implements ConsoleObserver {
    @Override
    public void processConsole(String input) {
        throw new RuntimeException("Not implemented yet");
    }

    public abstract void connect(String host, int port);
    public abstract void disconnect();
    public abstract void whereami();
    public abstract void list();
    public abstract void use(String server);
    public abstract void exit();
    public abstract void sendMessage(String message);
}