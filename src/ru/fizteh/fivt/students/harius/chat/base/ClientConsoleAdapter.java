/*
 * ClientConsoleAdapter.java
 * Dec 15, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat.base;

public abstract class ClientConsoleAdapter implements ConsoleObserver {
    @Override
    public void processConsole(String input) {
        if (input.startsWith("/connect")) {
            int space = input.indexOf(':');
            if (space == -1) {
                error("Please provide address in format host:port");
                return;
            }
            String host = input.substring(8, space).trim();
            String port = input.substring(space + 1).trim();
            try {
                int portN = Integer.parseInt(port);
                connect(host, portN);
            } catch (NumberFormatException notNum) {
                error("Illegal port number: " + port);
            }
        } else if (input.equals("/disconnect")) {
            disconnect();
        } else if (input.equals("/whereami")) {
            whereami();
        } else if (input.equals("/list")) {
            list();
        } else if (input.startsWith("/use")) {
            String server = input.substring(4).trim();
            use(server);
        } else if (input.equals("/exit")) {
            exit();
        } else if (!input.startsWith("/")) {
            sendMessage(input);
        } else {
            other(input);
        }
    }

    public abstract void connect(String host, int port);
    public abstract void disconnect();
    public abstract void whereami();
    public abstract void list();
    public abstract void use(String server);
    public abstract void exit();
    public abstract void sendMessage(String message);
    public abstract void other(String input);
    public abstract void error(String error);
}