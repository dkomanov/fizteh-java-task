package ru.fizteh.fivt.students.dmitriyBelyakov.chat.server;

import ru.fizteh.fivt.students.dmitriyBelyakov.chat.Message;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Listener implements Runnable {
    private final int port;
    private ServerSocket socket;
    private static final int timeOut = 5000;
    private Thread myThread;
    volatile private List<User> users;
    private final Lock mutex = new ReentrantLock(true);
    private final String serverName = "<server>";
    HashSet<String> names;

    public Listener(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        socket = new ServerSocket(port);
        names = new HashSet<>();
        users = Collections.synchronizedList(new ArrayList<User>());
        socket.setSoTimeout(timeOut);
        myThread = new Thread(this);
        myThread.start();
    }

    @Override
    public void run() {
        while (!myThread.isInterrupted()) {
            try {
                Socket sock = socket.accept();
                try {
                    mutex.lock();
                    users.add(new User(sock, this));
                } finally {
                    mutex.unlock();
                }
            } catch (SocketTimeoutException e) {
            } catch (Exception e) {
                stop();
            }
        }
    }

    synchronized void sendAll(Message message, User from) {
        try {
            mutex.lock();
            for (User u : users) {
                if (u != from) {
                    u.sendMessage(message);
                }
            }
        } catch (Throwable t) {
        } finally {
            mutex.unlock();
        }
    }

    synchronized void deleteUser(User us) {
        try {
            mutex.lock();
            users.remove(us);
            names.remove(us.name());
        } catch (Throwable t) {
            stop();
        } finally {
            mutex.unlock();
        }
    }

    public void stop() {
        try {
            mutex.lock();
            for (User user : users) {
                user.close();
            }
        } finally {
            mutex.unlock();
        }
        myThread.interrupt();
    }

    void sendFromServer(String text) {
        System.out.println(text);
        sendAll(new Message(MessageType.MESSAGE, serverName, text), null);
    }

    void sendFromServer(String text, String user) {
        System.out.println("Ooops");
        try {
            mutex.lock();
            for(User u: users) {
                if(u.name().equals(user)) {
                    u.sendMessage(new Message(MessageType.MESSAGE, serverName, text));
                    break;
                }
            }
        } finally {
            mutex.unlock();
        }
    }

    public String list() {
        StringBuilder builder = new StringBuilder();
        for (String name: names) {
            builder.append(name);
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }

    void kill(String user) {
        try {
            mutex.lock();
            for(User u: users) {
                if(u.name().equals(user)) {
                    u.close();
                    break;
                }
            }
        } finally {
            mutex.unlock();
        }
    }

    void join() throws InterruptedException {
        myThread.join();
    }
}