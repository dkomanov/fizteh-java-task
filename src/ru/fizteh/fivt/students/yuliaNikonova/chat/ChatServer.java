package ru.fizteh.fivt.students.yuliaNikonova.chat;

import java.io.IOException;
import java.util.Scanner;

public class ChatServer {
    private static boolean work = true;
    private static int port = -1;
    private static Server server;

    public static void main(String args[]) {
        server = null;
        String command = "";
        Scanner in = new Scanner(System.in);
        boolean start = true;
        while (work) {
            command = in.nextLine();
            String[] com = command.split("\\s+");
            if (com.length < 1) {
                System.err.println("Error: empty command");
                continue;
            }
            if (com[0].equals("/list")) {
                if (com.length != 1) {
                    System.err.println("Usage: /list");
                } else if (port == -1 || (server == null)) {
                    System.err.println("Server doesn't listen any port");
                } else {
                    server.list();
                }
            } else if (com[0].equals("/listen")) {
                if (com.length != 2) {
                    System.err.println("Usage: /listen port");
                } else if (port != -1) {
                    System.err.println("Server can't listen more than one port, it's listening port " + port + " now");
                } else {
                    try {
                        port = Integer.parseInt(com[1]);
                    } catch (Exception e) {
                        System.err.println("Error: " + e.getMessage() + " " + com[1]);
                        continue;
                    }

                    if (port < 0 || port > 65535) {
                        System.err.println("Error: wrong number of port " + port);
                    } else {
                        server = new Server(port);
                    }
                }
            } else if (com[0].equals("/stop")) {
                if (com.length != 1) {
                    System.err.println("Usage: /stop");
                } else if (port == -1 || (server == null)) {
                    System.err.println("Nothing to stop: server doesn't listen any port");
                } else {
                    server.stopListen();
                    try {
                        server.join();

                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        // e.printStackTrace();
                    }
                    port = -1;
                    server = null;
                }
            } else if (com[0].equals("/exit")) {
                if (com.length != 1) {
                    System.err.println("Usage: /exit");
                }
                if (port != -1 && (server != null)) {
                    server.exitChat();
                }
                System.exit(0);
            } else if (com[0].equals("/kill")) {
                if (com.length != 2) {
                    System.err.println("Usage: /kill user");
                } else if (port == -1 || (server == null)) {
                    System.err.println("Server doesn't listen any port");
                } else {
                    String user = com[1];
                    server.kill(user);
                }
            } else if (com[0].equals("/send")) {
                if (com.length < 3) {
                    System.err.println("Usage: /send user message");
                } else if (port == -1 || server == null) {
                    System.err.println("Server doesn't listen any port");
                } else {
                    String user = com[1];
                    server.send(user, getMessage(com, 2));
                }

            } else if (com[0].equals("/sendall")) {
                if (com.length < 2) {
                    System.err.println("Usage: /sendall message");
                } else if (port == -1 || server == null) {
                    System.err.println("Server doesn't listen any port");
                } else {
                    server.sendToAll(MessageUtils.message("server", getMessage(com, 1)));
                }

            } else {
                System.err.println("unknown command: " + command);
            }
        }
    }

    private static String getMessage(String[] com, int start) {
        StringBuilder message = new StringBuilder();
        for (int i = start; i < com.length; i++) {
            message.append(com[i]);
            message.append(" ");
        }
        return message.toString();
    }
}