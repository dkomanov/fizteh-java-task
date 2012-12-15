package ru.fizteh.fivt.students.harius.chat.impl;

import ru.fizteh.fivt.students.harius.chat.base.*;
import ru.fizteh.fivt.students.harius.chat.io.Packet;
import java.util.*;
import java.io.IOException;

public final class Server {
    private final DisplayBase display;

    private final List<NetworkObservable> clients
        = new ArrayList<>();

    private ServerConsoleAdapter inputProcessor
        = new ServerConsoleAdapter() {
        
        @Override
        public void listen(int port) {
            if (registrationProcessor != null) {
                display.warn("Already listening another port");
            }
            try {
                registrationProcessor = new Registrator(port);
                new Thread(registrationProcessor).start();
            } catch (IOException ioEx) {
                display.error("cannot bind server: " + ioEx.getMessage());
            }
        }

        @Override
        public void stop() {
            if (registrationProcessor == null) {
                display.warn("Not listening any port yet");
            } else {
                try {
                    registrationProcessor.close();
                    registrationProcessor = null;
                } catch (IOException ioEx) {
                    display.error("i/o error while stopping server: " + ioEx.getMessage());
                }
            }
        }

        @Override
        public void list() {
            for (NetworkObservable client : clients) {
                display.warn(" " + client.repr());
            }
        }

        @Override
        public void send(String user, String message) {

        }

        @Override
        public void sendall(String message) {

        }

        @Override
        public void kill(String user) {

        }

        @Override
        public void exit() {

        }
    };

    private NetworkObserver clientsProcessor
        = new NetworkObserver() {

        @Override
        public void processNetwork(Packet packet, NetworkObservable caller) {
            try {
                List<String> data = packet.getData();
                if (!packet.isValid()) {
                    display.warn("Message with wrong header was recceived from " + caller.repr());
                    caller.send(Packet.error("Invalid message type"));
                    caller.close();
                } else if (packet.isMessage()) {
                    if (data.isEmpty()) {
                        display.warn("Message without a name was received from " + caller.repr());
                        caller.send(Packet.error("Where is your nickname?"));
                        caller.close();
                    } else {
                        for (NetworkObservable client : clients) {
                            if (client != caller) {
                                client.send(packet);
                            }
                        }
                    }
                } else if (packet.isError()) {
                    display.warn("An error was received from " + caller.repr());
                    for (String error : data) {
                        display.warn("\t" + error);
                    }
                    caller.close();
                }
            } catch (IOException ioEx) {
                display.error("i/o exception while packet processing: " + ioEx.getMessage());
            }
        }

        @Override
        public void processClosed(String why, NetworkObservable caller) {
            display.warn("Connection to " + caller.repr() + " closed:\n" + why);
            int index = clients.indexOf(caller);
            if (index == -1) {
                display.error("internal error: unknown client deleted: " + caller.repr());
            } else {
                clients.remove(index);
            }
        }
    };

    private Registrator registrationProcessor = null;

    public Server(DisplayBase display) {
        this.display = display;
        display.setObserver(inputProcessor);
        new Thread(display).start();
    }
}