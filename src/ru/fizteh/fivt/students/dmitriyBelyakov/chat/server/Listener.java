package ru.fizteh.fivt.students.dmitriyBelyakov.chat.server;

import ru.fizteh.fivt.students.dmitriyBelyakov.chat.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

class Listener implements Runnable {
    private final int port;
    private ServerSocket socket;
    private static final int timeOut = 5000;
    private Thread myThread;
    private ArrayList<User> users;

    public Listener(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        socket = new ServerSocket(port);
        users = new ArrayList<User>();
        socket.setSoTimeout(timeOut);
        myThread = new Thread(this);
        myThread.start();
    }

    @Override
    public void run() {
        while(!myThread.isInterrupted()) {
            try {
                Socket sock = socket.accept();
                System.out.println("Ooops...");
                System.out.flush();
                users.add(new User(sock, this));
            } catch (SocketTimeoutException e) {
            } catch (Exception e) {
                System.out.println(e.getCause());
                stop();
            }
        }
        System.out.println("Exit...");
    }

    synchronized void sendAll(Message message) {
        System.out.println("Send all...");
        for(User u: users) {
            u.sendMessage(message);
        }
    }

    synchronized void deleteUser(User user) {
        users.remove(user);
    }

    public void stop() {
        for(User user: users) {
            user.close();
        }
        myThread.interrupt();
    }

    public String list() {
        StringBuilder builder = new StringBuilder();
        for(User u: users) {
            System.out.println("User");
            builder.append(u.name());
        }
        return builder.toString();
    }
}