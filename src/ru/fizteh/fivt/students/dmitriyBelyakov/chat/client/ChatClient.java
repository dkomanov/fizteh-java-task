package ru.fizteh.fivt.students.dmitriyBelyakov.chat.client;


import ru.fizteh.fivt.students.dmitriyBelyakov.chat.Message;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageBuilder;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageType;

import java.io.BufferedReader;
import java.io.InputStreamReader;

class ChatClient {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Use: ChatClient <nickname>");
            System.exit(1);
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String str;
            Manager manager = new Manager(args[0]);
            while ((str = reader.readLine()) != null) {
                if (str.matches("/connect\\s+.+:\\d+")) {
                    String host = str.replaceAll("(/connect\\s+)|(:.+)", "");
                    String port = str.replaceAll("/connect\\s+.+:", "");
                    int portNum = Integer.parseInt(port);
                    manager.newConnection(host, portNum);
                } else if (str.equals("/whereami")) {
                    manager.whereAmI();
                } else if (str.equals("/disconnect")) {
                    manager.disconnect();
                } else if (str.equals("/list")) {
                    System.out.print(manager.list());
                } else if (str.equals("/exit")) {
                    manager.clear();
                    System.exit(0);
                } else if (str.matches("/use\\s+.+:\\d+")) {
                    str = str.replaceFirst("/use\\s+", "");
                    manager.use(str);
                } else if (!str.equals("") && str.charAt(0) == '/') {
                    System.err.println("Unknown command.");
                } else if (!str.equals("")) {
                    manager.sendMessage(new Message(MessageType.MESSAGE, args[0], str));
                }
            }
        } catch (Throwable t) {
            if (t.getMessage() != null) {
                System.out.println("Error: " + t.getMessage() + ".");
            } else {
                System.err.println(t.getClass().getName());//"Error: unknown.");
            }
        }
    }
}