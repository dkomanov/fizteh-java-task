package ru.fizteh.fivt.students.yushkevichAnton.chat.client;

import java.net.Socket;

class ServerConnection {
    private String nickName;
    private Client client;

    private boolean alive = true;

    private CommunicationThread communicationThread;

    ServerConnection(String address, int port, String nickName, Client client) {
        this.nickName = nickName;
        this.client = client;

        try {
            Socket socket = new Socket(address, port);

            communicationThread = new CommunicationThread(socket, this);
            communicationThread.start();
        } catch (Exception e) {
            disconnect();
        }
    }

    boolean isAlive() {
        return alive;
    }

    boolean isCurrentServer() {
        return client.isCurrentServer(this);
    }

    String getNickName() {
        return nickName;
    }

    void disconnect() {
        if (!alive) {
            return;
        }

        alive = false;

        try {
            communicationThread.bye();
        } catch (Exception e) {
        }

        communicationThread.flush();
        communicationThread.interrupt();

        System.out.println("You got disconnected from " + this);

        client.validateServers();
    }

    void post(String message) {
        try {
            communicationThread.post(message);
        } catch (Exception e) {
            disconnect();
        }
    }
}