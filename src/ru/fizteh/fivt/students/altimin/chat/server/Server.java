package ru.fizteh.fivt.students.altimin.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * User: altimin
 * Date: 12/8/12
 * Time: 2:11 PM
 */
public class Server {
    private ConnectionManager manager;
    ServerSocket socket;

    Server() {
        manager = new ConnectionManager();
    }

    public void start(int port) {
        if (socket != null) {
            System.out.print("Server is already running");
            return;
        }
        try {
            socket = new ServerSocket(port);
            manager.start(socket);
            print("Server started");
        } catch (IOException e) {
            print("Failed to start server: failed to create a socket");
        }
    }

    public void stop() {
        manager.stop();
        try {
            socket.close();
        } catch (Exception e) {
        }
        socket = null;
        print("Server stopped");
    }

    public void print(String s) {
        synchronized (System.out) {
            System.out.println(s);
        }
    }

    public void exit() {
        stop();
        System.exit(0);
    }

    public void send(String msg, String user) {
        manager.send(msg, "<server>", user);
    }

    public void send(String msg) {
        manager.send(msg, "<server>", null);
    }

    public void kill(String user) {
        boolean ok = manager.killUser(user);
        if (!ok) {
            print("No such user " + user);
        }
    }

    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (true) {
                String string = reader.readLine();
                if (string.startsWith("/listen ")) {
                    String portStr = string.substring(8);
                    try {
                        int port = Integer.parseInt(portStr);
                        start(port);
                    } catch (NumberFormatException e) {
                        print(portStr + " isn't a valid port number");
                    }
                } else if (string.equals("/listen")) {
                    print("Parameters expected");
                } else if (string.equals("/stop")){
                    stop();
                } else if (string.equals("/list")) {
                    print("users:" + manager.listUsers());
                } else if (string.startsWith("/send ")) {
                    int firstSpacePos = string.indexOf(' ');
                    int spacePos = string.indexOf(' ', firstSpacePos + 1);
                    if (spacePos == -1) {
                        print("usage: /send nick message");
                    }
                    String nick = string.substring(firstSpacePos + 1, spacePos);
                    String msg = string.substring(spacePos + 1);
                    send(msg, nick);
                } else if (string.equals("/send")) {
                    print("Parameters expected");
                } else if (string.startsWith("/sendall ")) {
                    String msg = string.substring(9);
                    send(msg);
                } else if (string.equals("/sendall")) {
                    print("Parameters expected");
                } else if (string.startsWith("/kill ")) {
                    String user = string.substring(6);
                    kill(user);
                } else if (string.equals("/kill")) {
                    print("Parameters expected");
                } else if (string.equals("/exit")) {
                    stop();
                    System.exit(1);
                } else {
                    print("Unknown command " + string);
                }

            }
        } catch (IOException e) {
            print("Got exception while reading from console");
            stop();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }

}
