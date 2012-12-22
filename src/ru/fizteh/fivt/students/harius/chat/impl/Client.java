/*
 * Client.java
 * Dec 15, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat.impl;

import ru.fizteh.fivt.students.harius.chat.base.*;
import ru.fizteh.fivt.students.harius.chat.io.Packet;
import java.util.*;
import java.net.*;
import java.io.IOException;

public final class Client {
    private final DisplayBase display;

    private final List<NetworkObservable> servers
        = Collections.synchronizedList(new ArrayList<NetworkObservable>());

    private int current = -1;

    private final String name;

    private final Object lock = new Object();

    private ClientConsoleAdapter inputProcessor
        = new ClientConsoleAdapter() {

        @Override
        public void processClosed() {
            exit();
        }
        
        @Override
        public void connect(String host, int port) {
            try {
                Socket connection = new Socket(host, port);
                NetworkObservable observable
                    = new NetworkService(connection);
                servers.add(observable);
                notifier.notifyServerAdded(observable.repr());
                observable.setObserver(networkProcessor);
                observable.send(Packet.hello(name));
                new Thread(observable).start();
                use((servers.size() - 1) + "");
            } catch (IOException badHost) {
                display.error("Bad server address");
            }
        }

        @Override
        public void disconnect() {
            NetworkObservable server = getCurrent();
            if (server == null) {
                display.error("Cannot disconnect, no active server");
            } else {
                try {
                    server.send(Packet.goodbye("I am disconnecting"));
                    server.close();
                } catch (IOException ioEx) {
                    display.error("i/o exception while disconnecting: " + ioEx.getMessage());
                }
            }
        }

        @Override
        public void whereami() {
            NetworkObservable server = getCurrent();
            if (server == null) {
                display.error("Cannot send message, no active server");
            } else {
                display.warn(server.repr());
            }
        }

        @Override
        public void list() {
            int index = 0;
            synchronized (servers) {
                for (NetworkObservable server : servers) {
                    display.warn(index + " " + server.repr());
                    ++index;
                }
            }
        }

        @Override
        public void use(String server) {
            try {
                int index = Integer.parseInt(server);
                if (index < -1 || index > servers.size()) {
                    display.error("Invalid server id");
                } else {
                    current = index;
                    notifier.notifyServerChanged(current);
                    synchronized(lock) {
                        lock.notifyAll();
                    }
                }
            } catch (NumberFormatException notNum) {
                display.error("Invalid server id");
            }
        }

        @Override
        public void exit() {
            try {
                List<NetworkObservable> copy
                    = new ArrayList<>(servers);
                for (NetworkObservable server : copy) {
                    server.send(Packet.goodbye("I am disconnecting"));
                    server.close();
                }
                display.close();
            } catch (IOException ioEx) {
                display.error("i/o exception while performing quit: " + ioEx.getMessage());
            }
        }

        @Override
        public void sendMessage(String message) {
            NetworkObservable server = getCurrent();
            if (server == null) {
                display.error("Cannot send message, no active server");
            } else {
                try {
                    Packet messagePacket = Packet.message(name, message);
                    if (display.needsReflect()) {
                        networkProcessor.processNetwork(messagePacket, getCurrent());
                    }
                    server.send(messagePacket);
                } catch (IOException ioEx) {
                    display.error("i/o exception while sending packet: " + ioEx.getMessage());
                    try {
                        server.close();
                    } catch (IOException anotherIoEx) {
                        display.error("and an error while closing the connection");
                    }
                }
            }
        }

        @Override
        public void other(String message) {
            display.error("Unrecognized command: " + message);
        }

        @Override
        public void error(String error) {
            display.error(error);
        }

        private NetworkObservable getCurrent() {
            if (current < 0 || current > servers.size()) {
                return null;
            }
            return servers.get(current);
        }
    };
    private NetworkObserver networkProcessor
        = new NetworkObserver() {

        @Override
        public void processNetwork(Packet packet, NetworkObservable caller) {
            try {
                List<String> data = packet.getData();
                if (!packet.isValid()) {
                    display.warn("Message with wrong header was received from " + caller.repr());
                    caller.send(Packet.error("Invalid message type"));
                    caller.close();
                } else if (packet.isMessage()) {
                    if (data.isEmpty()) {
                        display.warn("Message without a name was received from " + caller.repr());
                        caller.send(Packet.error("Message without a nickname was received"));
                        caller.close();
                    } else {
                        while (servers.contains(caller)
                            && (current == -1 || caller != servers.get(current))) {

                            try {
                                synchronized(lock) {
                                    lock.wait();
                                }
                            } catch (InterruptedException interrupted) {
                                break;
                            }
                        }
                        if (servers.contains(caller) && current != -1 && caller == servers.get(current)) {
                            String nick = data.get(0);
                            for (String msg : data.subList(1, data.size())) {
                                display.message(nick, msg);
                            }
                        }
                    }
                } else if (packet.isError()) {
                    display.warn("An error was received from " + caller.repr());
                    for (String error : data) {
                        display.warn(" " + error);
                    }
                    caller.close();
                } else if (packet.isHello()) {
                    display.warn("Connected to " + caller.repr());
                } else if (packet.isBye()) {
                    display.warn("Goodbye from " + caller.repr());
                    for (String message : packet.getData()) {
                        display.warn(" " + message);
                    }
                    caller.close();
                } else {
                    display.error("internal error: unhandled message type");
                }
            } catch (IOException ioEx) {
                display.error("i/o exception while processing packet: " + ioEx.getMessage());
            }
        }

        @Override
        public void processClosed(String why, NetworkObservable caller) {
            display.warn("Connection to " + caller.repr() + " closed:\n" + why);
            int index = servers.indexOf(caller);
            if (index == -1) {
                display.error("internal error: unknown server deleted: " + caller.repr());
            } else {
                servers.remove(index);
                notifier.notifyServerRemoved(index);
                if (current == index) {
                    inputProcessor.use("-1");
                } else if (current > index) {
                    inputProcessor.use((current - 1) + "");
                } else {
                    synchronized(lock) {
                        lock.notifyAll();
                    }
                }
            }
        }
    };
    public final ClientObservable notifier
        = new ClientObservable() {};

    public Client(DisplayBase display, String name) {
        this.display = display;
        this.name = name;
        display.setObserver(inputProcessor);
        notifier.setObserver(display);
        new Thread(display).start();
    }
}