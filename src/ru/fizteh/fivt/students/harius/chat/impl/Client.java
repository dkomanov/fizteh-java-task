package ru.fizteh.fivt.students.harius.chat.impl;

import ru.fizteh.fivt.students.harius.chat.base.*;
import ru.fizteh.fivt.students.harius.chat.io.Packet;
import java.util.*;
import java.net.*;
import java.io.IOException;

public final class Client {
    private final DisplayBase display;

    private final List<NetworkObservable> servers
        = new ArrayList<>();

    private int current = -1;

    private final String name;

    private ClientConsoleAdapter inputProcessor
        = new ClientConsoleAdapter() {
        
        @Override
        public void connect(String host, int port) {
            try {
                Socket connection = new Socket(host, port);
                NetworkObservable observable
                    = new NetworkService(connection);
                current = servers.size();
                servers.add(observable);
                observable.setObserver(networkProcessor);
                observable.send(Packet.hello(name));
                new Thread(observable).start();
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
            for (NetworkObservable server : servers) {
                display.warn(index + " " + server.repr());
                ++index;
            }
        }

        @Override
        public void use(String server) {
            try {
                int index = Integer.parseInt(server);
                if (index < 0 || index > servers.size()) {
                    display.error("Invalid server id");
                } else {
                    current = index;
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
                    server.send(Packet.message(name, message));
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
                        String nick = data.get(0);
                        for (String msg : data.subList(1, data.size())) {
                            display.message(nick, msg);
                        }
                    }
                } else if (packet.isError()) {
                    display.warn("An error was received from " + caller.repr());
                    for (String error : data) {
                        display.warn("\t" + error);
                    }
                    caller.close();
                } else if (packet.isHello()) {
                    display.warn("Connected to " + caller.repr());
                } else if (packet.isBye()) {
                    display.warn("Goodbye from " + caller.repr());
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
                if (current == index) {
                    current = -1;
                } else if (current > index) {
                    --current;
                }
            }
        }
    };

    public Client(DisplayBase display, String name) {
        this.display = display;
        this.name = name;
        display.setObserver(inputProcessor);
        new Thread(display).start();
    }
}