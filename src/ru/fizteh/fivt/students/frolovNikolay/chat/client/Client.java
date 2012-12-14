package ru.fizteh.fivt.students.frolovNikolay.chat.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import ru.fizteh.fivt.students.frolovNikolay.Closer;
import ru.fizteh.fivt.students.frolovNikolay.chat.MsgHandler;
import ru.fizteh.fivt.students.frolovNikolay.chat.ChatUtils;

public class Client {
    
    private class ServerPair {
        SocketChannel socket;
        Selector multiplexor;
        
        public ServerPair(SocketChannel socket, Selector multiplexor) {
            this.socket = socket;
            this.multiplexor = multiplexor;
        }
    }
    
    private String nickName;
    private String currentServer;
    private SortedMap<String, ServerPair> servers;

    public Client(String nickName) {
        this.nickName = nickName;
        servers = new TreeMap<String, ServerPair>();
        currentServer = null;
    }
    
    private void disconnect(String serverName) throws Throwable {
        if (servers.containsKey(serverName)) {
            ChatUtils.sendMsg(MsgHandler.bye(), servers.get(serverName).socket);
            Closer.close(servers.get(serverName).socket);
            Closer.close(servers.get(serverName).multiplexor);
            servers.remove(serverName);
            System.out.println("you disconnected from " + serverName);
            if (serverName.equals(currentServer)) {
                if (servers.isEmpty()) {
                    currentServer = null;
                    System.out.println("Now you aren't connected to anyserver");
                } else {
                    currentServer = servers.firstKey();
                    System.out.println("Now you are connected to " + currentServer);
                }
            }
        }
    }
    
    private void checkServer() throws Throwable {
        if (currentServer == null || servers.get(currentServer).multiplexor.selectNow() == 0) {
            return;
        }
        Set<SelectionKey> keys = servers.get(currentServer).multiplexor.selectedKeys();
        for (SelectionKey key : keys) {
            if (ChatUtils.hasKey(key, SelectionKey.OP_READ)) {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                SocketChannel serverSocket = (SocketChannel) key.channel();
                if (serverSocket.read(buffer) == -1) {
                    disconnect(currentServer);
                } else {
                    byte[] msg = buffer.array();
                    switch (msg[0]) {
                        
                        // ordinary message
                        case 2: {
                            String[] nickAndMsg = MsgHandler.parseMessage(msg);
                            if (nickAndMsg[0] == null || nickAndMsg[1] == null) {
                                System.err.println("Error: bad message from server");
                            } else {
                                System.out.println(nickAndMsg[0] + ": " + nickAndMsg[1]);
                            }
                            break;
                        }
                        
                        // bye message
                        case 3: {
                            disconnect(currentServer);
                            break;
                        }
                        
                        // error message
                        case 127: {
                            String errorMsg = MsgHandler.parseHelloAndError(msg);
                            System.err.println("Error: " + errorMsg);
                            break;
                        }
                        
                        // someone's trying to do evil things. Just ignore him.
                        default: {
                            
                        }
                    }
                }
            }
        }
        keys.clear();
    }
    
    private void connect(String serverAddress) throws Throwable {
        String[] address = serverAddress.split(":");
        if (address.length != 2) {
            System.err.println("Incorrect server address: " + serverAddress);
        } else if (servers.containsKey(address[0])) {
            System.err.println("You are already connected to: " + address[0]);
        } else {
            int port = -1;
            try {
                port = Integer.parseInt(address[1]);
            } catch (Throwable error) {
                System.err.println("connect: incorrect port");
                return;
            }
            if (port < 0 || port > 65535) {
                System.err.println("connect: incorrect port");
            }
            ServerPair addedServer = new ServerPair(SocketChannel.open(), Selector.open());
            try {
                addedServer.socket.connect(new InetSocketAddress(address[0], port));
            } catch (Throwable error) {
                System.err.println("Error: can't connect to this server");
                return;
            }
            addedServer.socket.configureBlocking(false);
            addedServer.socket.register(addedServer.multiplexor, SelectionKey.OP_READ);
            servers.put(serverAddress, addedServer);
            currentServer = serverAddress;
            ChatUtils.sendMsg(MsgHandler.hello(nickName), addedServer.socket);
            System.out.println("You are connected to: " + serverAddress);
        }
    }
    
    public void run() throws Throwable {
        String command = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            if (reader.ready()) {
                command = reader.readLine();
                if (command.matches("/connect\\s+.+")) {
                    connect(command.replaceFirst("/connect\\s+", "").trim());
                } else if (command.equals("/disconnect")) {
                    if (currentServer != null) {
                        disconnect(currentServer);
                    } else {
                        System.out.println("You aren't connected to any server");
                    }
                } else if (command.equals("/whereami")) {
                    if (currentServer != null) {
                        System.out.println("You on server: " + currentServer);
                    } else {
                        System.out.println("You aren't connected to any server");
                    }
                } else if (command.equals("/list")) {
                    if (currentServer != null) {
                        System.out.println("You are connected to servers:");
                        for (String iter : servers.keySet()) {
                            System.out.println(iter);
                        }
                    } else {
                        System.out.println("You aren't connected to any server");
                    }
                } else if (command.matches("/use\\s+.+")) {
                    String newServer = command.replaceFirst("/use\\s+", "").trim();
                    if (servers.containsKey(newServer)) {
                        currentServer = newServer;
                    } else {
                        System.err.println("use: you aren't connected to this server");
                    }
                } else if (command.equals("/exit")) {
                    for (ServerPair iter : servers.values()) {
                        ChatUtils.sendMsg(MsgHandler.bye(), iter.socket);
                        Closer.close(iter.socket);
                        Closer.close(iter.multiplexor);
                    }
                    servers.clear();
                    currentServer = null;
                    break;
                } else if (!command.isEmpty() && command.charAt(0) == '/') {
                    System.err.println("Unknown command: " + command);
                } else {
                    if (currentServer != null) {
                        ChatUtils.sendMsg(MsgHandler.message(nickName, command), servers.get(currentServer).socket);
                    }
                }
            }
            checkServer();
        }
    }
}