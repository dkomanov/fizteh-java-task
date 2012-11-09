package ru.fizteh.fivt.students.dmitriyBelyakov.chat.server;

import java.util.Scanner;

public class ChatServer {
    private final static String serverName = "<server>";

    public static void main(String[] args) {
        String command;
        Scanner scanner = new Scanner(System.in);
        Manager manager = null;
        while ((command = scanner.nextLine()) != null) {
            try {
                if (command.matches("/listen\\s+\\d+")) {
                    command = command.replaceFirst("/listen\\s+", "");
                    if (manager != null) {
                        manager.stop();
                        manager.join();
                    }
                    int port = Integer.parseInt(command);
                    manager = new Manager(port);
                    manager.start();
                    System.out.println("Listen port " + port);
                } else if (command.equals("/stop")) {
                    if (manager != null) {
                        manager.stop();
                        manager.join();
                        manager = null;
                        System.out.println("Server stopped.");
                    }
                } else if (command.equals("/list")) {
                    if (manager != null) {
                        System.out.print(manager.list());
                    }
                } else if (command.matches("/send\\s+.+")) {
                    if (manager == null) {
                        continue;
                    }
                    String user = command.replaceFirst("/send\\s+", "");
                    if (user.equals("")) {
                        System.out.println("Error: unknown user.");
                        continue;
                    }
                    command = scanner.nextLine();
                    if (command == null) {
                        System.err.println("Error: cannot read message.");
                        if (manager != null) {
                            manager.stop();
                        }
                        manager.join();
                        System.exit(1);
                    }
                    manager.sendFromServer(command, user);
                } else if (command.equals("/sendall")) {
                    if (manager == null) {
                        continue;
                    }
                    command = scanner.nextLine();
                    manager.sendFromServer(command);
                } else if (command.matches("/kill\\s+.+")) {
                    if (manager == null) {
                        continue;
                    }
                    command = command.replaceFirst("/kill\\s+", "");
                    if (command.equals("")) {
                        System.err.println("Error: user not found.");
                        continue;
                    }
                    manager.kill(command);
                } else if (command.equals("/exit")) {
                    if (manager != null) {
                        manager.stop();
                        manager.join();
                    }
                    System.exit(0);
                } else {
                    System.err.println("Unknown command.");
                }
            } catch (Throwable t) {
                if (t.getMessage() != null) {
                    System.err.println("Error: " + t.getMessage() + ".");
                } else {
                    System.err.println("Error: unknown.");
                }
                System.exit(1);
            }
        }
        manager.stop();
        manager.join();
        System.exit(0);
    }
}