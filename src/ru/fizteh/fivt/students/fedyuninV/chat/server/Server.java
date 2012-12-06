package ru.fizteh.fivt.students.fedyuninV.chat.server;


import ru.fizteh.fivt.students.fedyuninV.chat.message.Message;
import ru.fizteh.fivt.students.fedyuninV.chat.message.MessageType;

import java.net.ServerSocket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class Server implements Runnable{

    private ServerSocket serverSocket;
    private Set <UserWorker> unauthorizedUsers = new HashSet<>();
    private Map <String, UserWorker> usersOnline = new HashMap<>();
    private final Thread serverThread;

    public Server() {
        serverThread = new Thread(this);
        usersOnline.put("server", null);
    }

    private void kill(String... userNames) {
        synchronized (usersOnline) {
            for(String userName: userNames) {
                if (usersOnline.containsKey(userName)) {
                    if (userName.equals("server")) {
                        System.out.println("If you want to stop server, type \"/stop\"");
                    } else {
                        usersOnline.remove(userName).kill();
                    }
                } else {
                    System.out.println("No user with name " + userName);
                }
            }
        }
    }

    public void list() {
        Set<String> userNames = null;
        synchronized (usersOnline) {
            userNames = usersOnline.keySet();
        }
        for (String userName: userNames) {
            System.out.println(userName);
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

    public void send(Message message, String address) {
    }

    public void sendAll(MessageType type, String text) {
    }

    public void kill(String userName) {
    }

    public void run() {
    }
}
