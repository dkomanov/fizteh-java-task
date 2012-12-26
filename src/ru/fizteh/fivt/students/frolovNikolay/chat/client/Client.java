package ru.fizteh.fivt.students.frolovNikolay.chat.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import ru.fizteh.fivt.chat.MessageUtils;
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
    private SortedMap<String, List<Byte>> byteBuffer = new TreeMap<String, List<Byte>>();
    private int badTries = 0;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    
    public boolean isBufferReady() {
        boolean result = false;
        try {
            result = reader.ready();
        } catch (Throwable ignoringError) {}
        return result;
    }
    
    public void sendMessage() {
        try {
            String text = reader.readLine();
            if (currentServer != null) {
                ChatUtils.sendMsg(MsgHandler.message(nickName, text), servers.get(currentServer).socket);
            } else {
                System.out.println("You can't send messages because you are not connected to any server");
            }
        } catch (Throwable ignoringError) {
            System.out.println("Some troubles with sending message");
        }
    }
    
    public boolean isConnected() {
        return currentServer != null;
    }

    public Client(String nickName) {
        this.nickName = nickName;
        servers = new TreeMap<String, ServerPair>();
        currentServer = null;
    }
    
    public void use(String newCurrentServer) {
        currentServer = newCurrentServer;
    }
    
    public void disconnect() {
        try {
            ChatUtils.sendMsg(MsgHandler.bye(), servers.get(currentServer).socket);
            Closer.close(servers.get(currentServer).socket);
            Closer.close(servers.get(currentServer).multiplexor);
            servers.remove(currentServer);
            byteBuffer.remove(currentServer);
            currentServer = null;
        } catch (Throwable ignoringError) {}
    }
    
    public int selectedNumber() {
        if (currentServer == null) {
            return 0;
        } else {
            int result = 0;
            try {
                result = servers.get(currentServer).multiplexor.selectNow();
            } catch (Throwable ignoringError) {}
            return result;
        }
    }
    
    public boolean checkServer() {
        try {
            Set<SelectionKey> keys = servers.get(currentServer).multiplexor.selectedKeys();
            for (SelectionKey key : keys) {
                if (ChatUtils.hasKey(key, SelectionKey.OP_READ)) {
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    SocketChannel serverSocket = (SocketChannel) key.channel();
                    int state;
                    while ((state = serverSocket.read(buffer)) > 0) {
                        byte[] piece = buffer.array();
                        for (int i = 0; i < state; ++i) {
                            byteBuffer.get(currentServer).add(piece[i]);
                        }
                    }
                    if (state == -1) {
                        disconnect();
                        return false;
                    } else {
                        switch (byteBuffer.get(currentServer).get(0)) {
                            
                            // ordinary message
                            case 2: {
                                String[] nickAndMsg = MsgHandler.parseMessage(byteBuffer.get(currentServer));
                                if (nickAndMsg == null) {
                                    ++badTries;
                                    if (badTries > 1000) {
                                        return false;
                                    }
                                    break;
                                } else {
                                    badTries = 0;
                                }
                                if (nickAndMsg[0] == null || nickAndMsg[1] == null) {
                                    return false;
                                } else {
                                    System.out.println(nickAndMsg[0] + ": " + nickAndMsg[1]);
                                }
                                break;
                            }
                            
                            // bye message
                            case 3: {
                                disconnect();
                                return false;
                            }
                            
                            // error message
                            case 127: {
                                disconnect();
                                return false;
                            }
                            
                            // someone's trying to do evil things. Just shutdown client.
                            default: {
                                disconnect();
                                return false;
                            }
                        }
                    }
                }
            }
            keys.clear();
        } catch (Throwable ignoringException) {
            disconnect();
            return false;
        }
        return true;
    }
    
    public boolean connect(String serverAddress) {
        String[] address = serverAddress.split(":");
        if (address.length != 2) {
            return false;
        } else if (servers.containsKey(address[0])) {
            return false;
        } else {
            try {
                int port = -1;
                port = Integer.parseInt(address[1]);
                if (port < 0 || port > 65535) {
                    return false;
                }
                ServerPair addedServer = new ServerPair(SocketChannel.open(), Selector.open());
                addedServer.socket.connect(new InetSocketAddress(address[0], port));
                addedServer.socket.configureBlocking(false);
                addedServer.socket.register(addedServer.multiplexor, SelectionKey.OP_READ);
                byteBuffer.put(serverAddress, new ArrayList());
                servers.put(serverAddress, addedServer);
                currentServer = serverAddress;
                ChatUtils.sendMsg(MsgHandler.hello(nickName), addedServer.socket);
            } catch (Throwable ignoringError) {
                return false;
            }
        }
        return true;
    }
     
    public void run() throws Throwable {
        String command = null;
        while (true) {
            if (reader.ready()) {
                command = reader.readLine();
                if (command.matches("/connect\\s+.+")) {
                    connect(command.replaceFirst("/connect\\s+", "").trim());
                } else if (command.equals("/disconnect")) {
                    if (currentServer != null) {
                        disconnect();
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