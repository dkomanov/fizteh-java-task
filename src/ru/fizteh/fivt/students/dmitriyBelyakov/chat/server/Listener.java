package ru.fizteh.fivt.students.dmitriyBelyakov.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

class Listener implements Runnable {
    private final int port;
    private ServerSocket socket;
    private static final int timeOut = 10000;
    private Thread myThread;

    public Listener(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        socket = new ServerSocket(port);
        socket.setSoTimeout(timeOut);
        myThread = new Thread(this);
        myThread.start();
    }

    public void run() {

    }
}