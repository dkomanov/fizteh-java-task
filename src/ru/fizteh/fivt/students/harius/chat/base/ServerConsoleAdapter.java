package ru.fizteh.fivt.students.harius.chat.base;

public abstract class ServerConsoleAdapter implements ConsoleObserver {
    @Override
    public void processConsole(String input) {
        throw new RuntimeException("Not implemented yet");
    }

    public abstract void listen(int port);
    public abstract void stop();
    public abstract void list();
    public abstract void send(String user, String message);
    public abstract void sendall(String message);
    public abstract void kill(String user);
    public abstract void exit();
}