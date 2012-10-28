package ru.fizteh.fivt.students.dmitriyBelyakov.chat.server;

import java.util.Scanner;

public class ChatServer {
    private final static String serverName = "<server>";

    public static void main(String[] args) {
        String command;
        Scanner scanner = new Scanner(System.in);
        Listener listener = null;
        while ((command = scanner.nextLine()) != null) {
            if (command.matches("/listen[ ]*[0-9]*")) {
                command = command.replaceFirst("/listen[ ]*", "");
                int port = -1;
                try {
                    port = Integer.parseInt(command);
                    listener = new Listener(port);
                    listener.start();
                } catch (Exception e) {
                    System.exit(1);
                }
                System.out.println("Listen port " + port);
            } else if (command.equals("/stop")) {
                if (listener != null) {
                    listener.stop();
                    try {
                        listener.join();
                    } catch (Throwable t) {
                    }
                    System.out.println("Server stopped.");
                }
            } else if (command.equals("/list")) {
                if (listener != null) {
                    System.out.print(listener.list());
                }
            } else if (command.matches("/send[ ]+.*")) {
                String user = command.replaceFirst("/send[ ]*", "");
                command = scanner.nextLine();
                if(command == null) {
                    System.exit(1);
                }
                listener.sendFromServer(command, user);
            } else if (command.equals("/sendall")) {
                command = scanner.nextLine();
                listener.sendFromServer(command);
            } else if (command.matches("/kill[ ]+.*")) {
                command = command.replaceFirst("/kill[ ]*", "");
                listener.kill(command);
            } else if (command.equals("/exit")) {
                if (listener != null) {
                    listener.stop();
                    try {
                        listener.join();
                    } catch (Exception e) {
                        System.exit(1);
                    }
                }
                System.exit(0);
            } else {
                System.err.println("Unknown command.");
            }
        }
    }
}