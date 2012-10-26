package ru.fizteh.fivt.students.dmitriyBelyakov.chat.server;

import ru.fizteh.fivt.students.dmitriyBelyakov.chat.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class Listener implements Runnable {
    private final int port;
    private ServerSocket socket;
    private static final int timeOut = 10000;
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

    public void run() {
        while(true) {
            try {
                Socket sock = socket.accept();
                users.add(new User(sock));
            } catch(Exception e) {
            }
        }
    }

    synchronized void sendAll(Message message) {

    }

    public void stop() {
        for(User user: users) {
            user.close();
        }
    }
}