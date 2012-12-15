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
                servers.add(observable);
            } catch (IOException badHost) {
                display.error("Bad server address");
            }
        }

        @Override
        public void disconnect() {
            NetworkObservable current = getCurrent();
            if (current == null) {
                display.error("Cannot disconnect, no active server");
            } else {
                try {
                    current.send(Packet.goodbye("I am disconnecting"));
                } catch (IOException ioEx) {
                    display.error("i/o exception: " + ioEx.getMessage());
                }
            }
        }

        @Override
        public void whereami() {
            NetworkObservable current = getCurrent();
            if (current == null) {
                display.error("Cannot send message, no active server");
            } else {
                display.warn(current.repr());
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
                for (NetworkObservable server : servers) {
                    server.send(Packet.goodbye("I am disconnecting"));
                    server.close();
                }
                display.close();
            } catch (IOException ioEx) {
                display.error("i/o exception: " + ioEx.getMessage());
            }
        }

        @Override
        public void sendMessage(String message) {
            NetworkObservable current = getCurrent();
            if (current == null) {
                display.error("Cannot send message, no active server");
            } else {
                try {
                    current.send(Packet.message(name, message));
                } catch (IOException ioEx) {
                    display.error("i/o exception: " + ioEx.getMessage());
                }
            }
        }

        @Override
        public void other(String message) {
            display.error("Unrecognized command: " + message);
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
                    display.warn("Message with wrong header was recceived");
                    caller.send(Packet.error("Invalid message type"));
                } else if (packet.isMessage()) {
                    if (data.isEmpty()) {
                        display.warn("Message without a name was received");
                        caller.send(Packet.error("Where is your nickname?"));
                    } else {
                        String nick = data.get(0);
                        for (String msg : data.subList(1, data.size())) {
                            display.message(nick, msg);
                        }
                    }
                } else if (packet.isError()) {
                    display.warn("An error was received");
                    for (String error : data) {
                        display.warn("\t" + error);
                    }
                }
            } catch (IOException ioEx) {
                display.error("i/o exception: " + ioEx.getMessage());
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