package ru.fizteh.fivt.students.altimin.chat.client;


import ru.fizteh.fivt.chat.MessageType;
import ru.fizteh.fivt.students.altimin.chat.Message;
import ru.fizteh.fivt.students.altimin.chat.MessageReader;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * User: altimin
 * Date: 12/8/12
 * Time: 1:05 AM
 */

public class Client {

    private String name;

    public Client(String name) {
        this.name = name;
    }

    public static void println(String s) {
        synchronized (System.out) {
            System.out.println(s);
        }
    }

    private final Object activeServerSync = new Object();
    private String currentActiveServer = null;
    private Connection currentActiveConnection = null;

    private final Object activeServerListSync = new Object();
    List<Connection> activeServers = new ArrayList<Connection>();

    public class Connection {
        String serverName;
        OutputStream out;
        Socket socket;
        InputStream in;
        Thread thread;

        public Connection(String host, Integer port) throws IOException {
            serverName = host + ":" + port.toString();
            socket = new Socket(host, port);
            in = socket.getInputStream();
            out = socket.getOutputStream();
            thread = new ConnectionListener(in, serverName);
        }

        void close() {
            try {
                out.write((new Message(MessageType.BYE)).toByteArray());
            } catch (IOException e) {
            }
            try {
                in.close();
            } catch (Exception e) {
            }
            try {
                out.close();
            } catch (Exception e) {
            }
            try {
                socket.close();
            } catch (Exception e) {
            }
            thread.interrupt();
        }
    }

    public class ConnectionListener extends Thread {
        InputStream inputStream;
        String serverName;

        public ConnectionListener(InputStream inputStream, String serverName) {
            this.inputStream = inputStream;
            this.serverName = serverName;
        }

        @Override
        public void run() {
            MessageReader messageReader = new MessageReader(inputStream);
            while (true) {
                Message message;
                try {
                    message = messageReader.read();
                } catch (IOException e) {
                    Client.this.deleteServer(serverName);
                    break;
                }
                if (message.type == MessageType.ERROR) {
                    if (message.data.size() > 0) {
                        System.err.println("Server " + serverName + "reported error: " + message.data.get(0));
                    } else {
                        System.err.println("Server " + serverName + "reported error");
                    }
                    break;
                }
                if (message.type == MessageType.BYE) {
                    System.err.println("Server " + serverName + " closed connection: BYE message got");
                    break;
                }
                if (message.type != MessageType.MESSAGE || message.data.size() != 2) {
                    Client.this.deleteServer(serverName);
                    System.err.println("Server " + serverName + " returned unexpected message");
                    break;
                }
                boolean isActive = false;
                synchronized (Client.this.activeServerSync) {
                    isActive = currentActiveServer.equals(serverName);
                }
                if (isActive) {
                    println(message.data.get(0) + ": " + message.data.get(1));
                }
            }
        }
    }

    public void deleteServer(String serverName) {
        synchronized (activeServerListSync) {
            for (Connection con: activeServers) {
                if (con.serverName.equals(serverName)) {
                    con.close();
                    activeServers.remove(con);
                    break;
                }
            }
        }
        synchronized (activeServerSync) {
            if (currentActiveServer != null && currentActiveServer.equals(serverName)) {
                currentActiveConnection.close();
                currentActiveConnection = null;
                currentActiveServer = null;
            }
        }
    }

    public void listServers() {
        synchronized (activeServerListSync) {
            synchronized (System.out) {
                System.out.println("Active servers:");
                for (Connection connection: activeServers) {
                    System.out.println(connection.serverName);
                }
            }
        }
    }

    public void printLocation() {
        synchronized (activeServerSync) {
            if (currentActiveServer != null) {
                println("Current server: " + currentActiveServer);
            } else {
                println("No current server");
            }
        }
    }

    public void disconnect() {
        synchronized (activeServerSync) {
            if (currentActiveServer != null) {
                println("Disconnected from" + currentActiveServer);
                synchronized (activeServerListSync) {
                    activeServers.remove(currentActiveConnection);
                    currentActiveConnection.close();
                    currentActiveConnection = null;
                    currentActiveServer = null;
                }
            } else {
                println("No active server");
            }
        }
    }

    public void disconnectAll() {
        synchronized (activeServerListSync) {
            for (Connection con: activeServers) {
                con.close();
            }
        }
    }

    public void setActiveServer(String serverName) {
        synchronized (activeServerSync) {
            synchronized (activeServerListSync) {
                for (Connection con: activeServers) {
                    if (con.serverName.equals(serverName)) {
                        currentActiveServer = serverName;
                        currentActiveConnection = con;
                        return;
                    }
                }
                println("No such server " + serverName);
            }
        }
    }

    public void connect(String host, Integer port) {
        Connection connection;
        try {
            connection = new Connection(host, port);
            connection.out.write((new Message(MessageType.HELLO, name)).toByteArray());
        } catch (Exception e) {
            println("Failed to connect to server " + host + ":" + port.toString());
            return;
        }
        synchronized (activeServerListSync) {
            activeServers.add(connection);
        }
        synchronized (activeServerSync) {
            currentActiveConnection = connection;
            currentActiveServer = connection.serverName;
        }
        connection.thread.start();
        println("Connection to server " + host + ":" + port.toString() + " established");
    }

    public void sendMessage(String string) {
        if (currentActiveConnection == null) {
            println("No active connection");
            return;
        }
        try {
            currentActiveConnection.out.write(new Message(MessageType.MESSAGE, name, string).toByteArray());
        } catch (Exception e) {
            deleteServer(currentActiveConnection.serverName);
            println("Failed to send message");
        }
    }

    public class ConsoleHandler {
        public void run() {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String string;

            try {
                while ((string = reader.readLine()) != null) {
                    if (string.startsWith("/connect ")) {
                        String[] serverName = string.substring(9).split(":");
                        if (serverName.length != 2) {
                            println(string.substring(9) + " is not valid address");
                            continue;
                        }
                        String host = serverName[0];
                        int port;
                        try {
                            port = Integer.parseInt(serverName[1]);
                        } catch (RuntimeException e) {
                            println(serverName[1] + " is not valid port number");
                            break;
                        }
                        Client.this.connect(host, port);
                    } else if (string.equals("/list")) {
                        Client.this.listServers();
                    } else if (string.equals("/disconnect")) {
                        Client.this.disconnect();
                    } else if (string.equals("/whereami")) {
                        Client.this.printLocation();
                    } else if (string.startsWith("/use ")) {
                        Client.this.setActiveServer(string.substring(5));
                    } else if (string.startsWith("/exit")) {
                        Client.this.disconnectAll();
                    } else if (string.startsWith("/")) {
                        Client.this.disconnectAll();
                        System.err.println("No such command " + string);
                    } else {
                        Client.this.sendMessage(string);
                    }
                }
            } catch (IOException e) {
                Client.this.disconnectAll();
                System.err.println("Error while reading from console");
                System.exit(1);
            }
        }
    }

    public void run() {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.run();
    }


    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: client <login>");
            System.exit(1);
        }
        Client client = new Client(args[0]);
        client.run();
    }

    public static void log(String s) {
        synchronized (System.err) {
            System.err.println(s);
        }
    }

}
