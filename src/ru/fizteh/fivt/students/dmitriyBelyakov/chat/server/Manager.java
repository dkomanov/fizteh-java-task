package ru.fizteh.fivt.students.dmitriyBelyakov.chat.server;

import ru.fizteh.fivt.students.dmitriyBelyakov.chat.Message;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.HashSet;
import java.util.Collections;
import java.util.ArrayList;

class Manager implements Runnable {
    private final int           port;
    private ServerSocket        socket;
    private static final int    timeOut = 5000;
    private Thread              myThread;
    private List<User>          users;
    private final String        serverName = "server";
    HashSet<String>             names;
    private boolean             notDelete;
    private List<User>          forDelete;

    public Manager(int port) {
        this.port = port;
        names = new HashSet<>();
        notDelete = false;
        users = Collections.synchronizedList(new ArrayList<User>());
        forDelete = Collections.synchronizedList(new ArrayList<User>());
    }

    public void start() throws IOException {
        socket = new ServerSocket(port);
        socket.setSoTimeout(timeOut);
        myThread = new Thread(this);
        myThread.start();
    }

    @Override
    public void run() {
        while (!myThread.isInterrupted()) {
            try {
                Socket sock = socket.accept();
                users.add(new User(sock, this));
            } catch (SocketTimeoutException e) {
            } catch (Throwable e) {
                stop();
            }
        }
    }

    synchronized void sendAll(Message message, User from) {
        try {
            notDelete = true;
            for (User u : users) {
                if (u != from) {
                    u.sendMessage(message);
                }
            }
        } catch (Throwable t) {
            System.out.println(t.getClass().getName());
        } finally {
            notDelete = false;
        }
        deleteFromList();
    }

    synchronized void deleteUser(User us) {
        if (notDelete) {
            forDelete.add(us);
        } else {
            users.remove(us);
            if (us.isAuthorized()) {
                names.remove(us.name());
            }
        }
    }

    synchronized void stop() {
        try {
            notDelete = true;
            for (User user : users) {
                user.close(false, true);
            }
        } finally {
            notDelete = false;
        }
        deleteFromList();
        myThread.interrupt();
    }

    synchronized void sendFromServer(String text) {
        sendAll(new Message(MessageType.MESSAGE, serverName, text), null);
    }

    synchronized void sendFromServer(String text, String user) {
        try {
            notDelete = true;
            for (User u : users) {
                if (u.name().equals(user)) {
                    u.sendMessage(new Message(MessageType.MESSAGE, serverName, text));
                    break;
                }
            }
        } finally {
            notDelete = false;
        }
        deleteFromList();
    }

    public String list() {
        StringBuilder builder = new StringBuilder();
        for (String name : names) {
            builder.append(name);
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }

    synchronized void kill(String user) {
        try {
            notDelete = true;
            for (User u : users) {
                if (u.name().equals(user)) {
                    u.close(false, true);
                    break;
                }
            }
        } finally {
            notDelete = false;
        }
        deleteFromList();
    }

    synchronized void deleteFromList() {
        for (User u : forDelete) {
            deleteUser(u);
        }
        forDelete.clear();
    }

    void join() throws InterruptedException {
        ArrayList<User> tmp = new ArrayList<>(users);
        for (User u : tmp) {
            u.join();
        }
        myThread.join();
    }
}