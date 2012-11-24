package ru.fizteh.fivt.students.dmitriyBelyakov.chat.server;

import ru.fizteh.fivt.students.dmitriyBelyakov.chat.Message;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Manager implements Runnable {
    private final int port;
    private ServerSocket socket;
    private static final int timeOut = 5000;
    private Thread myThread;
    private List<User> users;
    private final String serverName = "server";
    HashSet<String> names;
    private final UserDeleteRegulator userDeleteRegulator;

    public Manager(int port) {
        this.port = port;
        names = new HashSet<>();
        names.add(serverName);
        users = Collections.synchronizedList(new ArrayList<User>());
        userDeleteRegulator = new UserDeleteRegulator(this);
    }

    public void showInServer(String text) {
        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date().getTime()) + "] " + text);
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
                User newUser = new User(sock, this);
                users.add(newUser);
                newUser.start();
            } catch (SocketTimeoutException e) {
            } catch (Throwable e) {
                stop();
            }
        }
    }

    synchronized void sendAll(Message message, User from) {
        try {
            userDeleteRegulator.lock();
            for (User u : users) {
                if (u != from) {
                    u.sendMessage(message);
                }
            }
        } catch (Throwable t) {
        } finally {
            userDeleteRegulator.unlock();
        }
    }

    synchronized public void deleteUser(User user) {
        userDeleteRegulator.delete(user);
    }

    synchronized public void delete(User user) {
        users.remove(user);
        if (user.isAuthorized()) {
            names.remove(user.name());
        }
    }

    synchronized void stop() {
        try {
            userDeleteRegulator.lock();
            for (User user : users) {
                user.close(false, true);
            }
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (Throwable t) {
        } finally {
            userDeleteRegulator.unlock();
        }
        myThread.interrupt();
    }

    synchronized void sendFromServer(String text) {
        sendAll(new Message(MessageType.MESSAGE, serverName, text), null);
    }

    synchronized void sendFromServer(String text, String user) {
        try {
            userDeleteRegulator.lock();
            for (User u : users) {
                if (u.name().equals(user)) {
                    u.sendMessage(new Message(MessageType.MESSAGE, serverName, text));
                    break;
                }
            }
        } finally {
            userDeleteRegulator.unlock();
        }
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
            userDeleteRegulator.lock();
            for (User u : users) {
                if (u.name().equals(user)) {
                    u.close(false, true);
                    break;
                }
            }
        } finally {
            userDeleteRegulator.unlock();
        }
    }

    void join() {
        ArrayList<User> tmp = new ArrayList<>(users);
        for (User u : tmp) {
            try {
                u.join();
            } catch (Throwable t) {
            }
        }
        tmp.clear();
        try {
            myThread.join();
        } catch (Throwable t) {
        }
    }
}
