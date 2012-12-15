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

        }

        @Override
        public void stop() {

        }

        @Override
        public void list() {

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
                    display.warn("Message with wrong header was recceived");
                    caller.send(Packet.error("Invalid message type"));
                } else if (packet.isMessage()) {
                    if (data.isEmpty()) {
                        display.warn("Message without a name was received");
                        caller.send(Packet.error("Where is your nickname?"));
                    } else {
                        for (NetworkObservable client : clients) {
                            if (client != caller) {
                                client.send(packet);
                            }
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

        @Override
        public void processClosed(String why) {
            
        }
    };

    private Runnable registrationProcessor
        = new Runnable() {

        @Override
        public void run() {
            
        }
    };

    public Server(DisplayBase display) {
        this.display = display;
        display.setObserver(inputProcessor);
    }
}