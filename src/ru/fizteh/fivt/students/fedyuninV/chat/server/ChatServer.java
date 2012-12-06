package ru.fizteh.fivt.students.fedyuninV.chat.server;

import ru.fizteh.fivt.students.fedyuninV.CommandLine;
import ru.fizteh.fivt.students.fedyuninV.CommandLineParser;
import ru.fizteh.fivt.students.fedyuninV.chat.message.Message;
import ru.fizteh.fivt.students.fedyuninV.chat.message.MessageType;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class ChatServer implements CommandLine {
    Server server = null;

    private void printUsage() {
        System.err.println("Incorrect usage of command");
    }

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        CommandLineParser commandLineParser = new CommandLineParser(chatServer, System.in, "/exit");
        commandLineParser.run();
    }

    @Override
    public void execute(String command, String[] args) {
        if (command.equals("/listen")) {
            Integer portNum = null;
            if (args == null  ||  args.length != 1) {
                printUsage();
                return;
            }
            try {
                Integer.parseInt(args[0]);
            } catch (Exception ex) {
                printUsage();
                return;
            }
            if (portNum == null  ||  portNum < 0  ||  portNum >= 65000) {
                printUsage();
                return;
            }
            if (server != null) {
                server.stop();
            }
            server = new Server(portNum);
            server.start();
        } else if (command.equals("/stop")) {
            if (args != null  &&  args.length != 0) {
                printUsage();
                return;
            }
            if (server != null) {
                server.stop();
            } else {
                System.out.println("You need to start listening port.");
            }
        } else if (command.equals("/list")) {
            if (args != null  &&  args.length != 0) {
                printUsage();
                return;
            }
            if (server != null) {
                server.list();
            } else {
                System.out.println("You need to start listening port.");
            }
        } else if (command.equals("/send")) {
            if (args == null  ||  args.length != 2) {
                printUsage();
                return;
            }
            if (server != null) {
                Message message = new Message(MessageType.MESSAGE);
                message.setName("server");
                message.setText(args[1]);
                server.send(message, args[0]);
            } else {
                System.out.println("You need to start listening port.");
            }
        } else if (command.equals("/sendAll")) {
            if (args == null  ||  args.length != 1) {
                printUsage();
                return;
            }
            if (server != null) {
                server.sendAll(MessageType.MESSAGE, args[0]);
            } else {
                System.out.println("You need to start listening port.");
            }
        } else if (command.equals("/kill")) {
            if (args == null  ||  args.length != 1) {
                printUsage();
                return;
            }
            if (server != null) {
                server.kill(args[0]);
            } else {
                System.out.println("You need to start listening port.");
            }
        } else if (command.equals("/exit")) {
            if (server != null) {
                server.stop();
            }
        }
    }
}
