package ru.fizteh.fivt.students.fedyuninV.chat;


import ru.fizteh.fivt.students.fedyuninV.CommandLine;
import ru.fizteh.fivt.students.fedyuninV.IOUtils;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class Server implements CommandLine, Runnable{

    private ServerSocket serverSocket;
    private volatile Map <String, UserWorker> users = new HashMap<>();
    private Object usersLock;
    private final Thread serverThread;

    public Server() {
        serverThread = new Thread(this);
    }

    private void printUsage() {
        System.err.println("Incorrect usage of command");
    }

    private void kill(String... userNames) {
        synchronized (users) {
            for(String userName: userNames) {
                UserWorker userWorker = users.remove(userName);
                if (userWorker == null) {
                    System.err.println("No user with name " + userName);
                } else {
                    userWorker.kill();
                }
            }
        }
    }

    public void start() {
        serverThread.start();
    }

    public void stop() {
        serverThread.interrupt();
        try {
            serverThread.join();
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public void execute(String command, String[] args) {
        if (command.equals("/listen")) {
            if (args.length != 1) {
                printUsage();
            } else {
                kill((String[]) users.keySet().toArray());
                IOUtils.tryClose(serverSocket);
                try {
                    serverSocket.bind(new InetSocketAddress(Integer.parseInt(args[0])));
                } catch (Exception ex) {
                    System.err.println("Unable to bind.");
                }
            }
        } else if (command.equals("/stop")) {
            stop();
        } else if (command.equals("/list")) {
            Set<String> userNames= users.keySet();
            for (String userName: userNames) {
                System.out.println(userName);
            }
        } else if (command.equals("/send")) {

        } else if (command.equals("/sendall")) {

        } else if (command.equals("/kill")) {
            for (String userName: args) {
                kill(userName);
            }
        } else if (command.equals("/exit")) {
            stop();
        } else {
            printUsage();
        }
    }

    public void run() {
    }
}
