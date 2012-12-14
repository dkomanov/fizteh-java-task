package ru.fizteh.fivt.students.alexanderKuzmin.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import ru.fizteh.fivt.students.alexanderKuzmin.Closers;

public class Server {
    private final static String serverName = "server";
    private ServerListeningSocketThread serverListeningSocket = null;
    private ConcurrentHashMap<String, User> users;
    private Scanner scanner;

    public static void main(String[] args) throws Exception {
        Server serv = new Server();
        serv.startWork();
    }

    public void startWork() {
        String consoleInput;
        scanner = new Scanner(System.in);
        users = new ConcurrentHashMap<String, User>();
        while ((consoleInput = scanner.nextLine()) != null) {
            if (consoleInput.matches("/listen\\s\\d+")) {
                listenPort(consoleInput);
            } else if (consoleInput.equals("/stop")) {
                stopListening();
                System.out.print("Listening of the port ended.");
            } else if (consoleInput.equals("/exit")) {
                goodExit();
            } else if (consoleInput.equals("/list")) {
                printUsersList();
            } else if (consoleInput.matches("/send\\s+.+")) {
                send(consoleInput.replaceFirst("/send\\s", ""));
            } else if (consoleInput.matches("/sendall\\s.+")) {
                announcement(serverName,
                        consoleInput.replaceFirst("/sendall\\s", ""));
            } else if (consoleInput.matches("/kill\\s+.+")) {
                kill(users.get(consoleInput.replaceFirst("/kill\\s+", "")),
                        "You are kicked by server.");
            }
        }
    }

    private void send(String consoleInput) {
        String receiverName = consoleInput.split("\\s")[0];
        if (!users.containsKey(receiverName)) {
            Closers.printErrAndNoExit("No such user.");
            return;
        }
        synchronized (users) {
            users.get(consoleInput.split("\\s")[0]).sendMessage(serverName,
                    consoleInput.replaceFirst("\\w+\\s", ""));
        }
    }

    private void goodExit() {
        stopListening();
        scanner.close();
        System.out.print("Good bye!");
        System.exit(0);
    }

    private void printUsersList() {
        synchronized (users) {
            if (users.isEmpty()) {
                System.out.println("No users.");
            }
            for (String user : users.keySet()) {
                System.out.println(user);
            }
        }
    }

    private void stopListening() {
        if (serverListeningSocket != null) {
            try {
                serverListeningSocket.close();
                serverListeningSocket.interrupt();
                serverListeningSocket = null;
            } catch (IOException e) {
                badExit(e.getMessage());
            }
            synchronized (users) {
                for (String user : users.keySet()) {
                    kill(users.get(user), "Server shutdown.");
                }
            }
            synchronized (users) {
                users.clear();
            }
        }
    }

    private void badExit(String message) {
        stopListening();
        scanner.close();
        Closers.printErrAndExit(message);
    }

    private void kill(User user, String message) {
        if (!user.isInit()) {
            return;
        }
        user.kill(message);
    }

    private void listenPort(String consoleInput) {
        stopListening();
        serverListeningSocket = new ServerListeningSocketThread(
                Integer.parseInt(consoleInput.split("\\s")[1]), this);
        serverListeningSocket.start();
    }

    public void acceptConnection(ServerSocket serverSocket) {
        Socket acceptSocket = null;
        try {
            acceptSocket = serverSocket.accept();
        } catch (IOException e) {
            if (!serverSocket.isClosed()) {
                Closers.printErrAndNoExit(e.getMessage());
            }
            return;
        }
        User newUser = new User(acceptSocket, this);
        try {
            newUser.start();
        } catch (Throwable e) {
            Closers.printErrAndNoExit(e.getMessage());
            users.remove(newUser.getName());
            newUser.kill(null);
        }
    }

    public boolean haveName(String name) {
        return users.containsKey(name);
    }

    void announcement(String nickName, String message) {
        System.out.println("<" + nickName + ">: " + message);
        synchronized (users) {
            for (String user : users.keySet()) {
                users.get(user).sendMessage(nickName, message);
            }
        }
    }

    public void addUserInTable(String name, User user) {
        synchronized (users) {
            users.put(name, user);
        }
        System.out.println(name + " is connected.");
    }

    public void deleteFromTable(String name, User user) {
        try {
            if (user != null && users.contains(user)) {
                synchronized (users) {
                    if (users.remove(user.getName()) != null) {
                        System.out.println(name + " is disconnected.");
                    }
                }

            }
        } catch (Throwable e) {

        }
    }
}