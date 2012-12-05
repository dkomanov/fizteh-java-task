package ru.fizteh.fivt.students.yushkevichAnton.chat.server;

import java.io.*;
import java.net.ProtocolException;
import java.net.Socket;

class ClientConnection {
    private Server server;
    private String nickName;
    private CommunicationThread communicationThread;
    private boolean alive = true;

    boolean isAlive() {
        return alive;
    }

    void setNickName(String nickName) throws IOException {
        if (!server.isCorrectNickName(nickName)) {
            throw new ProtocolException("That nickname is already taken");
        }

        this.nickName = nickName;
    }

    String getNickName() {
        return nickName;
    }

    ClientConnection(Socket socket, Server server) {
        this.server = server;

        try {
            communicationThread = new CommunicationThread(socket, this);
            communicationThread.start();
        } catch (IOException e) {
            e.printStackTrace();
            disconnect(e.getMessage());
        }
    }

    void disconnect() {
        disconnect(null);
    }

    void disconnect(String message) {
        alive = false;

        if (message != null) {
            communicationThread.sendError(message);
        }

        communicationThread.sendBye();

        if (communicationThread != null) {
            communicationThread.interrupt();
        }

        server.validateClients();
    }

    void announce(String message) {
        server.announce(nickName, message);
    }

    void sendMessage(String nickName, String message) {
        try{
            communicationThread.sendMessage(nickName, message);
        } catch (IOException e) {
            disconnect();
        }
    }

    @Override
    public int hashCode() {
        return nickName.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        return this == object;
    }
}
