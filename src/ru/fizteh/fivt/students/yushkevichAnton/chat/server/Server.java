package ru.fizteh.fivt.students.yushkevichAnton.chat.server;

import java.io.*;
import java.util.*;

public class Server {
    public static void main(String[] args) {
        new Server().run();
    }

    private void run() {
        takenNickNames.add("server");

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String command = null;
            try {
                command = input.readLine();
            } catch (IOException e) {
                System.err.println("Could not read from cin");
                System.exit(1);
            }
            processCommand(command);
        }
    }

    private ArrayList<ClientConnection> clients = new ArrayList<ClientConnection>();
    private HashSet<String> takenNickNames = new HashSet<String>();
    private ListeningThread listeningThread;

    private void processCommand(String command) {
        if (command.length() > 0 && command.charAt(0) == '/') {
            if (command.matches("/listen\\s\\d+")) {
                listen(Integer.parseInt(command.split("\\s")[1]));
            } else if (command.matches("/stop")) {
                stop();
            } else if (command.matches("/list")) {
                list();
            } else if (command.matches("/send\\s\\d+\\s.+")) {
                send(command.split("\\s")[1], command.replaceFirst("/send\\s\\d+\\s", ""));
            } else if (command.matches("/sendall\\s.+")) {
                announce("server", command.replaceFirst("/sendall\\s", ""));
            } else if (command.matches("/kill\\s\\d+")) {
                kill(command.split("\\s")[1]);
            } else if (command.matches("/exit")) {
                exit();
            } else {
                System.err.println("Wrong syntax");
            }
        } else {
            System.err.println("One does not simply chat from a chat server");
        }
    }

    private void listen(int port) {
        stop();

        System.out.println("Started listening port " + port);

        listeningThread = new ListeningThread(port, this);
        listeningThread.start();
    }

    private void stop() {
        if (listeningThread != null) {
            listeningThread.close();
            listeningThread.interrupt();
            listeningThread = null;

            System.out.println("Stopped listening");
        }
        for (ClientConnection client : clients) {
            client.disconnect("Server is shutting down");
        }
        validateClients();
    }

    private void list() {
        for (int i = 0; i < clients.size(); i++) {
            System.out.println(i + ") " + clients.get(i).getNickName());
        }
    }

    private void send(String addressee, String message) {
        try{
            int num = Integer.parseInt(addressee);
            if (num < 0 || num >= clients.size()) {
                throw new IllegalAccessException("Wrong number");
            }
            clients.get(num).sendMessage("server", message);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    void announce(String nickName, String message) {
        System.out.println("<" + nickName + "> " + message);
        for (ClientConnection client : clients) {
            client.sendMessage(nickName, message);
        }
    }

    private void kill(String addressee) {
        int num;
        try{
            num = Integer.parseInt(addressee);
            if (num < 0 || num >= clients.size()) {
                throw new IllegalAccessException("Wrong number");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }
        announce("server", clients.get(num).getNickName() + " was kicked");
        clients.get(num).disconnect("You were kicked");
    }

    private void exit() {
        stop();
        System.exit(0);
    }

    void addClient(ClientConnection clientConnection) {
        try {
            Thread.sleep(100); // wait to get nickname
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!clientConnection.isAlive()) return;
        if (clientConnection.getNickName() == null) {
            clientConnection.disconnect();
            return;
        }

        clients.add(clientConnection);
        takenNickNames.add(clientConnection.getNickName());

        announce("server", clientConnection.getNickName() + " has connected!");
    }

    boolean isCorrectNickName(String nickName) {
        return !takenNickNames.contains(nickName);
    }

    void validateClients() {
        ArrayList<ClientConnection> newClients = new ArrayList<ClientConnection>();
        for (ClientConnection client : clients) {
            if (client.isAlive()) {
                newClients.add(client);
            } else {
                takenNickNames.remove(client.getNickName());
            }
        }
        clients = newClients;
    }
}
