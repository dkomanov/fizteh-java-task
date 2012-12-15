package ru.fizteh.fivt.students.harius.chat.impl;

import ru.fizteh.fivt.students.harius.chat.base.*;
import ru.fizteh.fivt.students.harius.chat.io.Packet;
import java.util.*;
import java.io.IOException;
import java.net.Socket;

public final class Server {
    private final DisplayBase display;

    private final List<NetworkObservable> clients
        = new ArrayList<>();

    private ServerConsoleAdapter inputProcessor
        = new ServerConsoleAdapter() {
        
        @Override
        public void listen(int port) {
            if (registrator != null) {
                display.warn("Already listening another port");
            }
            try {
                registrator = new Registrator(registrationProcessor, port);
                new Thread(registrator).start();
            } catch (IOException ioEx) {
                display.error("cannot bind server: " + ioEx.getMessage());
            }
        }

        @Override
        public void stop() {
            if (registrator == null) {
                display.warn("Not listening any port yet");
            } else {
                List<NetworkObservable> copy
                    = new ArrayList<>(clients);
                for (NetworkObservable client : copy) {
                    try {
                        client.send(Packet.goodbye("You are disconnected"));
                        client.close();
                    } catch (IOException ioEx) {
                        display.error("i/o error while disconnecting client: "
                            + ioEx.getMessage());
                    }
                }
                try {
                    registrator.close();
                    registrator = null;
                } catch (IOException ioEx) {
                    display.error("i/o error while stopping server: "
                        + ioEx.getMessage());
                }
            }
        }

        @Override
        public void list() {
            for (NetworkObservable client : clients) {
                display.warn(" " + client.name() + "@" + client.repr());
            }
        }

        @Override
        public void send(String user, String message) {
            NetworkObservable target = clientByName(user);
            if (target == null) {
                display.error("No such user");
            } else {
                try {
                    target.send(Packet.message("<server>", message));
                } catch (IOException ioEx) {
                    display.error("i/o error while sending packet to "
                        + target.repr() + ": " + ioEx.getMessage());
                    try {
                        target.close();
                    } catch (IOException anotherIoEx) {
                        display.error("and an error while closing the connection");
                    }
                }
            }
        }

        @Override
        public void sendall(String message) {
            for (NetworkObservable client : clients) {
                try {
                    client.send(Packet.message("<server>", message));
                } catch (IOException ioEx) {
                    display.error("i/o error while sending packet to "
                        + client.repr() + ": " + ioEx.getMessage());
                    try {
                        client.close();
                    } catch (IOException anotherIoEx) {
                        display.error("and an error while closing the connection");
                    }
                }
            }
        }

        @Override
        public void kill(String user) {
            NetworkObservable target = clientByName(user);
            if (target == null) {
                display.error("No such user");
            } else {
                try {
                    target.send(Packet.goodbye("You are disconnected"));
                    target.close();
                } catch (IOException ioEx) {
                    display.error("i/o error while disconnecting user: " + ioEx.getMessage());
                }
            }
        }

        @Override
        public void exit() {
            if (registrator != null) {
                stop();
            }
            try {
                display.close();
            } catch (IOException ioEx) {
                display.error("i/o exception while performing quit: " + ioEx.getMessage());
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
                    } else if (!data.get(0).equals(caller.name())) {
                        display.warn("Message with incorrect nick from " + caller.repr());
                        caller.send(Packet.error("Cheating hacker, it's not your nickname!"));
                        caller.close();
                    } else {
                        for (NetworkObservable client : clients) {
                            if (client != caller && client.name() != null) {
                                client.send(packet);
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
                    if (caller.name() != null) {
                        display.warn("Second hello-packet received from " + caller.repr());
                        caller.send(Packet.error("Hello-packet must contain your nickname"));
                        caller.close();                        
                    } else {
                        if (packet.getData().size() != 1) {
                            display.warn("Incorrect hello-packet received from " + caller.repr());
                            caller.send(Packet.error("Hello-packet must contain your nickname"));
                            caller.close();
                        } else {
                            String nick = packet.getData().get(0);
                            NetworkObservable test = clientByName(nick);
                            if (test != null) {
                                display.warn("Nickname is use already: " + nick);
                                caller.send(Packet.error("Nickname is already in use"));
                                caller.close();
                            } else if (!nick.matches("\\w{3,24}")) {
                                display.warn("Nickname is incorrect: " + nick);
                                caller.send(Packet.error("Nickname is incorrect"));
                                caller.close();
                            } else {
                                caller.setName(nick);
                                caller.send(Packet.message("<server>", "Welcome to the chat"));
                            }
                        }
                    }
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

    private Registrator registrator = null;

    private RegistratorObserver registrationProcessor
         = new RegistratorObserver() {

        @Override
        public void processRegistration(Socket socket) {
            try {
                NetworkObservable client = new NetworkService(socket);
                clients.add(client);
                client.setObserver(clientsProcessor);
                new Thread(client).start();
            } catch (IOException ioEx) {
                display.error("i/o error while creating new client: " + ioEx.getMessage());
            }
        }   
    };

    public Server(DisplayBase display) {
        this.display = display;
        display.setObserver(inputProcessor);
        new Thread(display).start();
    }

    private NetworkObservable clientByName(String name) {
        for (NetworkObservable client : clients) {
            if (client.name() != null && client.name().equals(name)) {
                return client;
            }
        }
        return null;
    }
}