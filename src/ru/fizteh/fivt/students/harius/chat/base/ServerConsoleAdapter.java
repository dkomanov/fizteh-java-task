package ru.fizteh.fivt.students.harius.chat.base;

public abstract class ServerConsoleAdapter implements ConsoleObserver {
    @Override
    public void processConsole(String input) {
        if (input.startsWith("/listen")) {
            String port = input.substring(7).trim();
            try {
                int portN = Integer.parseInt(port);
                listen(portN);
            } catch (NumberFormatException notNum) {
                error("Illegal port number: " + port);
            }
        } else if (input.equals("/stop")) {
            stop();
        } else if (input.equals("/list")) {
            list();
        } else if (input.startsWith("/sendall")) {
            String message = input.substring(8).trim();
            sendall(message);
        } else if (input.startsWith("/send")) {
            String tail = input.substring(5).trim();
            int nextSpace = tail.indexOf(' ');
            if (nextSpace == -1) {
                error("usage: /send username message");
                return;
            }
            String name = tail.substring(0, nextSpace);
            String message = tail.substring(nextSpace).trim();
            send(name, message);
        } else if (input.startsWith("/kill")) {
            String user = input.substring(5).trim();
            kill(user);
        } else if (input.equals("/exit")) {
            exit();
        } else {
            other(input);
        }
    }

    public abstract void listen(int port);
    public abstract void stop();
    public abstract void list();
    public abstract void send(String user, String message);
    public abstract void sendall(String message);
    public abstract void kill(String user);
    public abstract void exit();
    public abstract void other(String input);
    public abstract void error(String error);
}