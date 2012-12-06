package ru.fizteh.fivt.students.yuliaNikonova.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class ChatClient {
    private static ConcurrentHashMap<String, Client> servers;

    public static void main(String[] args) {
        servers = new ConcurrentHashMap<String, Client>();
        String curServer = "";
        if (args.length != 1) {
            System.out.println("Usage: ChatClient username");
            System.exit(1);
        }
        String userName = args[0];
        Client curClient = null;
        // new Client("127.0.0.1", 8888, userName, servers);
        Scanner console = new Scanner(System.in);
        String command = "";
        while ((command = console.nextLine()) != null) {
            String[] com = command.split("\\s+");
            if (com[0].equals("/connect")) {
                if (com.length != 2) {
                    System.err.println("Usage: /connect host:port");
                } else {
                    if (servers.containsKey(com[1])) {
                        System.err.println("you are already connected to " + com[1]);
                    } else {
                        String[] hostPort = com[1].split(":");
                        if (hostPort.length != 2) {
                            System.err.println("Usage: /connect host:port");
                            break;
                        }
                        int port = -1;
                        try {
                            port = Integer.parseInt(hostPort[1]);
                        } catch (Exception e) {
                            System.err.println("Wrong port: " + hostPort[1]);
                            break;
                        }
                        if (port < 0 || port > 65535) {
                            System.err.println("Wrong port: " + hostPort[1]);
                            break;
                        }
                        try {
                            Client client = new Client(hostPort[0], port, userName, servers);
                            if (curClient != null) {
                                curClient.pause();
                            }
                            curClient = client;
                            curServer = com[1];
                        } catch (Exception e) {
                            System.err.println(e.getMessage());
                        }
                    }
                }
            } else if (com[0].equals("/disconnect")) {
                if (com.length != 1) {
                    System.err.println("Usage: /disconnect");
                } else {
                    if (curClient == null) {
                        System.err.println("You are not connected to any server");
                    } else {
                        curClient.disconnect();
                        curClient = null;
                        curServer = "";
                    }
                }

            } else if (com[0].equals("/list")) {
                if (com.length != 1) {
                    System.err.println("Usage: /list");
                } else {
                    if (servers.keySet().isEmpty()) {
                        System.out.println("You are not connected to any server");
                    } else {
                        for (String server : servers.keySet()) {
                            System.out.println(server);
                        }
                    }
                }
            } else if (com[0].equals("/whereami")) {
                if (com.length != 1) {
                    System.err.println("Usage: /whereami");
                } else {
                    if (curServer.isEmpty()) {
                        System.out.println("You are not in chatroom of any server");
                    } else {
                        System.out.println(curServer);
                    }
                }
            } else if (com[0].equals("/use")) {
                if (com.length != 2) {
                    System.err.println("Usage: /use host:port");
                } else {
                    if (servers.containsKey(com[1])) {
                        try {
                            if (curClient != null) {
                                curClient.pause();
                            }

                            curServer = com[1];

                            servers.get(com[1]).begin();
                            curClient = servers.get(com[1]);
                        } catch (Exception e) {
                            System.err.println("Can't use this server: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        System.err.println("You are not connected to this server");
                    }
                }

            } else if (com[0].equals("/exit")) {
                if (com.length != 1) {
                    System.err.println("Usage: /exit");
                } else {
                    for (Client client : servers.values()) {
                        client.disconnect();
                    }
                    servers.clear();
                    System.exit(1);
                }
            } else {
                if (command.charAt(0) == '/') {
                    System.err.println("Unknown command: " + command);
                } else {
                    curClient.sendMessage(getMessage(com));
                }
            }
        }
    }

    private static String getMessage(String[] com) {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < com.length; i++) {
            message.append(com[i]);
            message.append(" ");
        }
        return message.toString();
    }
}
