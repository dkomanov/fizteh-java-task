package ru.fizteh.fivt.students.fedyuninV.chat.server;

import ru.fizteh.fivt.students.fedyuninV.CommandLineParser;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class ChatServer {
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
        CommandLineParser interactiveMode = new CommandLineParser(server, System.in, "/exit");
        interactiveMode.run();
    }
}
