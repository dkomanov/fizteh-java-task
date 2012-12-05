package ru.fizteh.fivt.students.yushkevichAnton.chat.client;

import java.io.*;
import java.util.*;

public class Client {
    public static void main(String[] args) {
        new Client().run(args);
    }

    private String nickName = null;
    private ArrayList<ServerConnection> servers = new ArrayList<ServerConnection>();
    private ServerConnection currentServer = null;

    private void run(String[] args) {
        if (args.length != 1) {
            System.err.println("Wrong syntax");
            System.exit(1);
        }

        nickName = args[0];

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String command = null;
            try {
                command = input.readLine();
            } catch (IOException e) {
                System.err.println("Cannot read from cin");
                System.exit(1);
            }
            processCommand(command);
        }
    }

    private void processCommand(String command) {
        if (command.length() > 0 && command.charAt(0) == '/') {
            if (command.matches("/connect\\s.+:\\d+")) {
                connect(command.split(" ")[1]);
            } else if (command.matches("/disconnect")) {
                disconnect();
            } else if (command.matches("/whereami")) {
                whereAmI();
            } else if (command.matches("/list")) {
                list();
            } else if (command.matches("/use\\s\\d+")) {
                use(command.split(" ")[1]);
            } else if (command.matches("/exit")) {
                exit();
            } else {
                System.err.println("Wrong syntax");
            }
        } else {
            post(command);
        }
    }

    private void connect(String arg) {
        ServerConnection server;

        try {
            String[] split = arg.split(":");
            if (split.length != 2) {
                throw new Exception("Wrong syntax");
            }
            String address = split[0];
            int port = Integer.parseInt(split[1]);
            server = new ServerConnection(address, port, nickName, this);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }

        servers.add(server);
        currentServer = server;

        System.out.println("Successfully connected to " + server);
    }

    boolean isCurrentServer(ServerConnection server) {
        return currentServer == server;
    }

    void disconnect() {
        if (currentServer != null) {
            currentServer.disconnect();
            currentServer = null;
        }
    }

    void validateServers() {
        if (!currentServer.isAlive()) {
            currentServer = null;
        }
        ArrayList<ServerConnection> newServers = new ArrayList<ServerConnection>();
        for (ServerConnection server : servers) {
            if (server.isAlive()) {
                newServers.add(server);
            }
        }
        servers = newServers;
    }

    private void whereAmI() {
        if (currentServer == null) {
            System.out.println("You are nowhere, mwhahahaha");
        } else {
            System.out.println("You are at " + currentServer);
        }
    }

    private void list() {
        for (int i = 0; i < servers.size(); i++) {
            System.out.println(i + ") " + servers.get(i));
        }
    }

    private void use(String s) {
        int index;
        try {
            index = Integer.parseInt(s);
            if (index < 0 || index >= servers.size()) {
                throw new IllegalAccessException("Wrong index");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;

        }

        currentServer = servers.get(index);
    }

    private void exit() {
        for (ServerConnection server : servers) {
            server.disconnect();
        }
        System.exit(0);
    }

    private void post(String message) {
        if (currentServer == null) {
            System.err.println("You are not connected to a server");
        } else {
            currentServer.post(message);
        }
    }
}
