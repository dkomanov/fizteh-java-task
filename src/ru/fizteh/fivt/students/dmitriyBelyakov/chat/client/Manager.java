package ru.fizteh.fivt.students.dmitriyBelyakov.chat.client;

import ru.fizteh.fivt.students.dmitriyBelyakov.chat.Message;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageBuilder;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageType;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Manager {
    private List<ServerWorker> servers;
    private List<ServerWorker> forDelete;
    private ServerWorker currentWorker;
    private boolean notDelete;
    private String name;

    Manager(String name) {
        this.name = name;
        currentWorker = null;
        notDelete = false;
        servers = Collections.synchronizedList(new ArrayList<ServerWorker>());
        forDelete = Collections.synchronizedList(new ArrayList<ServerWorker>());
    }

    synchronized void newConnection(String host, int port) {
        try {
            Socket socket = new Socket(host, port);
            currentWorker = new ServerWorker(host + ":" + port, socket, this);
            sendMessage(new Message(MessageType.HELLO, name, ""));
            servers.add(currentWorker);
        } catch (Throwable t) {
            System.err.println("Cannot connect to " + host + ":" + port + ".");
        }
    }

    public void sendMessage(Message message) {
        try {
            if (currentWorker != null) {
                currentWorker.socket.getOutputStream().write(MessageBuilder.getMessageBytes(message));
            }
        } catch (Throwable e) {
            if (currentWorker != null) {
                currentWorker.close(true, false);
            }
        }
    }

    synchronized public void deleteServer(ServerWorker server) {
        if (notDelete) {
            forDelete.add(server);
        } else {
            if (servers.contains(server)) {
                System.out.println("Close connection with " + server.name());
            }
            servers.remove(server);
        }
    }

    public String list() {
        StringBuilder builder = new StringBuilder();
        notDelete = true;
        try {
            for (ServerWorker w : servers) {
                builder.append(w.name());
                builder.append(System.lineSeparator());
            }
        } finally {
            notDelete = false;
        }
        return builder.toString();
    }

    void clear() {
        try {
            notDelete = true;
            for (ServerWorker w : servers) {
                w.close(false, true);
            }
        } finally {
            notDelete = false;
        }
        deleteFromList();
        ArrayList<ServerWorker> tmp = new ArrayList<>(servers);
        for (ServerWorker w : tmp) {
            try {
                w.join();
            } catch (Throwable t) {
            }
        }
    }

    public void whereAmI() {
        if (currentWorker != null) {
            System.out.println(currentWorker.name());
        }
    }

    public void disconnect() {
        if (currentWorker != null) {
            currentWorker.close(false, true);
            if (servers.size() > 0) {
                currentWorker = servers.get(0);
            } else {
                currentWorker = null;
            }
        }
    }

    void deleteFromList() {
        for (ServerWorker w : forDelete) {
            deleteServer(w);
        }
        forDelete.clear();
    }

    void use(String name) {
        notDelete = true;
        try {
            for (ServerWorker w : servers) {
                if (w.name().equals(name)) {
                    currentWorker = w;
                    break;
                }
            }
        } finally {
            notDelete = false;
        }
    }
}