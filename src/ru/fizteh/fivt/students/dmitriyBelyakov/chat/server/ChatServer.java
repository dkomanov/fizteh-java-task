package ru.fizteh.fivt.students.dmitriyBelyakov.chat.server;

import java.util.Scanner;

public class ChatServer {
    private final static String serverName = "<server>";

    public static void main(String[] args) {
        String command;
        Scanner scanner = new Scanner(System.in);
        Listener listener = null;
        while ((command = scanner.nextLine()) != null) {
            try {
                if (command.matches("/listen[ ]+[0-9]+")) {
                    command = command.replaceFirst("/listen[ ]+", "");
                    try {
                        int port = Integer.parseInt(command);
                        listener = new Listener(port);
                        listener.start();
                        System.out.println("Listen port " + port);
                    } catch (Throwable t) {
                        if (t.getMessage() != null) {
                            System.err.println("Error: " + t.getMessage() + ".");
                        } else {
                            System.err.println("Error: unknown.");
                        }
                        System.exit(1);
                    }
                } else if (command.equals("/stop")) {
                    if (listener != null) {
                        listener.stop();
                        try {
                            listener.join();
                        } catch (Throwable t) {
                        }
                        listener = null;
                        System.out.println("Server stopped.");
                    }
                } else if (command.equals("/list")) {
                    if (listener != null) {
                        System.out.print(listener.list());
                    }
                } else if (command.matches("/send[ ]+.+")) {
                    if (listener == null) {
                        continue;
                    }
                    String user = command.replaceFirst("/send[ ]+", "");
                    if (user.equals("")) {
                        System.out.println("Error: unknown user.");
                        continue;
                    }
                    command = scanner.nextLine();
                    if (command == null) {
                        System.err.println("Error: cannot read message.");
                        if (listener != null) {
                            listener.stop();
                        }
                        listener.join();
                        System.exit(1);
                    }
                    listener.sendFromServer(command, user);
                } else if (command.equals("/sendall")) {
                    if (listener == null) {
                        continue;
                    }
                    command = scanner.nextLine();
                    listener.sendFromServer(command);
                } else if (command.matches("/kill[ ]+.+")) {
                    if (listener == null) {
                        continue;
                    }
                    command = command.replaceFirst("/kill[ ]+", "");
                    if (command.equals("")) {
                        System.err.println("Error: user not found.");
                        continue;
                    }
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
            } catch (Throwable t) {
                if (t.getMessage() != null) {
                    System.err.println("Error: " + t.getMessage() + ".");
                } else {
                    System.err.println("Error: unknown.");
                    System.err.println(t.getClass().getName());
                }
            }
        }
    }
}