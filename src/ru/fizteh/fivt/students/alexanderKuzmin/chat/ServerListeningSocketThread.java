package ru.fizteh.fivt.students.alexanderKuzmin.chat;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerListeningSocketThread extends Thread {
    private int port;
    private Server server;
    private ServerSocket serverSocket;
    private boolean worker = true;

    ServerListeningSocketThread(int port, Server server) {
        this.port = port;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("The chat launched!");
            while (worker) {
                server.acceptConnection(serverSocket);
            }
        } catch (Throwable e) {
            System.err.println(e.getMessage());
        }
    }

    public void close() throws IOException {
        worker = false;
        serverSocket.close();
    }
}