package ru.fizteh.fivt.students.dmitriyBelyakov.chat.client;

/**
 * @author Dmitriy Belyakov
 */

import ru.fizteh.fivt.students.dmitriyBelyakov.chat.Message;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageBuilder;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

class Manager {
    private List<ServerWorker> servers;
    private ServerWorker currentWorker;
    private final String name;
    private ServerWorkerDeleteRegulator serverWorkerDeleteRegulator;

    Manager(String name) {
        this.name = name;
        currentWorker = null;
        servers = Collections.synchronizedList(new ArrayList<ServerWorker>());
        serverWorkerDeleteRegulator = new ServerWorkerDeleteRegulator(this);
    }

    synchronized void newConnection(String host, int port) {
        ServerWorker last = currentWorker;
        try {
            currentWorker = new ServerWorker(host, port, this);
            currentWorker.start();
            servers.add(currentWorker);
            if (last != null) {
                last.deactivate();
            }
            currentWorker.activate();
            sendMessage(new Message(MessageType.HELLO, name, ""));
        } catch (Throwable t) {
            if (currentWorker != null) {
                currentWorker.close(ServerWorker.ERROR, ServerWorker.NOT_SEND_MESSAGE);
            }
            System.err.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date().getTime()) +
                    "] Cannot connect to " + host + ":" + port + ".");
            currentWorker = last;
            if (currentWorker != null) {
                currentWorker.activate();
            }
        }
    }

    public void sendMessage(Message message) {
        try {
            if (currentWorker != null) {
                currentWorker.socket.getOutputStream().write(MessageBuilder.getMessageBytes(message));
            }
        } catch (Throwable e) {
            if (currentWorker != null) {
                currentWorker.close(ServerWorker.ERROR, ServerWorker.NOT_SEND_MESSAGE);
            }
            if (e.getMessage() != null) {
                System.err.println(e.getMessage());
            } else {
                System.err.println("Unknown error.");
            }
        }
    }

    synchronized public void deleteServer(ServerWorker server) {
        serverWorkerDeleteRegulator.delete(server);
    }

    synchronized public void delete(ServerWorker server) {
        if (servers.contains(server)) {
            if (server == currentWorker) {
                currentWorker = servers.size() > 0 ? servers.get(0) : null;
                if (currentWorker != null) {
                    currentWorker.activate();
                }
            }
            servers.remove(server);
            System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date().getTime())
                    + "] Closed connection with server '" + server.name() + "'.");
        }
    }

    public String list() {
        StringBuilder builder = new StringBuilder();
        try {
            serverWorkerDeleteRegulator.lock();
            for (ServerWorker w : servers) {
                builder.append(w.name());
                builder.append(System.lineSeparator());
            }
        } finally {
            serverWorkerDeleteRegulator.unlock();
        }
        return builder.toString();
    }

    void clear() {
        try {
            serverWorkerDeleteRegulator.lock();
            for (ServerWorker w : servers) {
                w.close(ServerWorker.BYE, ServerWorker.SEND_MESSAGE);
            }
        } finally {
            serverWorkerDeleteRegulator.unlock();
        }
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
            currentWorker.close(ServerWorker.BYE, ServerWorker.SEND_MESSAGE);
            if (servers.size() > 0) {
                currentWorker = servers.get(0);
            } else {
                currentWorker = null;
            }
        }
    }

    void use(String name) {
        serverWorkerDeleteRegulator.lock();
        try {
            for (ServerWorker w : servers) {
                if (w.name().equals(name)) {
                    currentWorker.deactivate();
                    currentWorker = w;
                    currentWorker.activate();
                    break;
                }
            }
        } finally {
            serverWorkerDeleteRegulator.unlock();
        }
    }
}
