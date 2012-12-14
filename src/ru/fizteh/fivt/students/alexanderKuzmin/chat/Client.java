package ru.fizteh.fivt.students.alexanderKuzmin.chat;

import java.util.ArrayList;
import java.util.Scanner;
import ru.fizteh.fivt.students.alexanderKuzmin.Closers;

public class Client {
    private String clientName;
    private ArrayList<UserWorker> servers;
    private UserWorker currentWorker = null;
    private Scanner scanner;

    public Client(String name) {
        clientName = new String(name);
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            Closers.printErrAndExit("Incorrect argument. Please input only NickName.");
        }
        if (args[0].isEmpty() || args[0].matches(".*\\s.*")) {  Closers.printErrAndExit("Incorrect name."); }
        Client serv = new Client(args[0]);
        serv.startWork();
    }

    private void startWork() {
        String consoleInput;
        servers = new ArrayList<UserWorker>();
        scanner = new Scanner(System.in);
        while ((consoleInput = scanner.nextLine()) != null) {
            try {
                if (consoleInput.matches("/connect\\s+.+:\\d+")) {
                    connectToServer(consoleInput.replaceAll(
                            "(/connect\\s+)|(:.+)", ""), Integer
                            .parseInt(consoleInput.replaceAll(
                                    "/connect\\s+.+:", "")));
                } else if (consoleInput.equals("/exit")) {
                    goodExit();
                } else if (consoleInput.equals("/disconnect")) {
                    disconnect();
                } else if (consoleInput.equals("/whereami")) {
                    whereAmI();
                } else if (consoleInput.equals("/list")) {
                    printList();
                } else if (consoleInput.matches("/use\\s\\d+")) {
                    useChatRoom(Integer.parseInt(consoleInput.replaceFirst(
                            "/use\\s", "")));
                } else if (consoleInput.length() > 0) {
                    if (consoleInput.charAt(0) == '/') {
                        System.err.println("Unknown command.");
                    } else if (consoleInput.length() > 0) {
                        sendMessage(consoleInput);
                    }
                }
            } catch (Throwable e) {
                Closers.printErrAndNoExit("Incorrect command.");
                continue;
            }
        }

    }

    private void sendMessage(String message) {
        if (currentWorker == null) {
            System.err.println("Please, connect to server.");
        } else {
            currentWorker.sendMessage(message);
        }
    }

    private void useChatRoom(int newServerName) {
        synchronized (servers) {
            try {
                if (newServerName >= 1 && newServerName <= servers.size()) {
                    if (currentWorker != null) {
                        currentWorker.close();
                    }
                    currentWorker = servers.get(--newServerName);
                    currentWorker.start();

                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                return;
            }
        }
    }

    private void printList() {
        synchronized (servers) {
            if (servers.isEmpty()) {
                System.out.println("No servers.");
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < servers.size(); ++i) {
                    sb.append(i + 1).append(" - ").append(servers.get(i))
                            .append("\n");
                }
                System.out.print(sb.toString());
            }
        }
    }

    private void whereAmI() {
        if (currentWorker == null) {
            System.out.println("You are not connected.");
        } else {
            System.out.println("You are in the room of " + currentWorker);
        }
    }

    public void disconnect() {
        synchronized (servers) {
            if (currentWorker != null && servers.contains(currentWorker)) {
                currentWorker.disconnect();
            }
        }
    }

    private void disconnectAllServers() {
        while (!servers.isEmpty()) {
            currentWorker = servers.get(0);
            currentWorker.disconnect();
        }
    }

    private void goodExit() {
        disconnectAllServers();
        scanner.close();
        System.out.print("Good bye!");
        System.exit(0);
    }

    private void connectToServer(String host, int port) {
        UserWorker server;
        try {
            server = new UserWorker(host, port, clientName, this);
        } catch (Exception e) {
            Closers.printErrAndNoExit(e.getMessage());
            return;
        }
        servers.add(server);
        currentWorker = server;

        System.out.println("Successfully connected to " + server);
    }

    public void deleteFromTable() {
        if (currentWorker != null) {
            synchronized (servers) {
                servers.remove(currentWorker);
                System.out
                        .println("You are disconnected from " + currentWorker);
                currentWorker = null;
            }
        }
    }

}
