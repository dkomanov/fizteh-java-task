package ru.fizteh.fivt.students.frolovNikolay.chat.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import ru.fizteh.fivt.students.frolovNikolay.chat.MsgHandler;
import ru.fizteh.fivt.students.frolovNikolay.chat.ChatUtils;

import ru.fizteh.fivt.students.frolovNikolay.Closer;

public class Server {

    private static String serverName = "<server>";
    private int port = -1;
    private HashMap<SocketChannel, String> clientsNameMapping = new HashMap<SocketChannel, String>();
    private HashMap<SocketChannel, List<Byte>> clientsBuffer = new HashMap<SocketChannel, List<Byte>>();
    private HashMap<SocketChannel, Integer> badTries = new HashMap<SocketChannel, Integer>();
    private ServerSocketChannel serverSocket;
    private Selector multiplexor;
    private List<SocketChannel> noNameClients = new ArrayList<SocketChannel>();

    public Server() {
        try {
            serverSocket = ServerSocketChannel.open();
            serverSocket.configureBlocking(false);
            multiplexor = Selector.open();
        } catch (Throwable smthbad) {
        }
    }

    private boolean isMsgCorrect(String name, String msg, SocketChannel from) throws Throwable {
        if (name == null || msg == null) {
            ChatUtils.sendMsg(MsgHandler.error("Incorrect msg"), from);
            System.err.println("Incorrect msg from: " + clientsNameMapping.get(from));
            return false;
        } else {
            return true;
        }
    }

    private void sendAll(String msg) throws Throwable {
        for (SocketChannel iter : clientsNameMapping.keySet()) {
            ChatUtils.sendMsg(MsgHandler.message(serverName, msg), iter);
        }
    }

    private void disconnectClient(SocketChannel clientSock) throws Throwable {
        String nickName = null;
        if (clientsNameMapping.containsKey(clientSock)) {
            nickName = clientsNameMapping.get(clientSock);
            clientsNameMapping.remove(clientSock);
        } else {
            noNameClients.remove(clientSock);
        }
        clientsBuffer.remove(clientSock);
        badTries.remove(clientSock);
        Closer.close(clientSock);
        if (nickName != null) {
            sendAll(nickName + " has left the chat");
            System.out.println(nickName + " has left the chat");
        }
    }

    private void serveClients() throws Throwable {
        if (multiplexor.selectNow() == 0) {
            return;
        }
        Set<SelectionKey> selectedKeys = multiplexor.selectedKeys();
        for (SelectionKey key : selectedKeys) {
            if (ChatUtils.hasKey(key, SelectionKey.OP_ACCEPT)) { 
                SocketChannel clientSock = serverSocket.accept();
                if (clientSock != null) {
                    clientSock.configureBlocking(false);
                    clientSock.register(multiplexor, SelectionKey.OP_READ);
                    noNameClients.add(clientSock);
                    clientsBuffer.put(clientSock, new ArrayList<Byte>());
                    badTries.put(clientSock, new Integer(0));
                }                    
            } else if (ChatUtils.hasKey(key, SelectionKey.OP_READ)) {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                SocketChannel clientSock = (SocketChannel) key.channel();
                int state;
                while ((state = clientSock.read(buffer)) > 0) {
                    byte[] piece = buffer.array();
                    for (int i = 0; i < state; ++i) {
                        clientsBuffer.get(clientSock).add(piece[i]);
                    }
                }
                if (state != -1) {
                    switch (clientsBuffer.get(clientSock).get(0)) {
                    
                        // Hello message
                        case 1: {
                            String nickName = MsgHandler.parseHelloAndError(clientsBuffer.get(clientSock));
                            boolean badNick = false;
                            if (nickName == null || nickName.isEmpty()) {
                                badNick = true;
                                ChatUtils.sendMsg(MsgHandler.error("Incorrect msg"), clientSock);
                            } else if (nickName.length() > 50) {
                                badNick = true;
                                ChatUtils.sendMsg(MsgHandler.error("Too long nickname(can have a maximum 50 characters)"), clientSock);
                            } else if (nickName.equals("<server>") || clientsNameMapping.containsKey(nickName)) {
                                badNick = true;
                                ChatUtils.sendMsg(MsgHandler.error("Someone already has this nickname"), clientSock);
                            }
                            if (badNick) {
                                ChatUtils.sendMsg(MsgHandler.bye(), clientSock);
                                Closer.close(clientSock);
                            } else {
                                ChatUtils.sendMsg(MsgHandler.message(serverName, "Welcome, " + nickName), clientSock);
                                sendAll(nickName + " connected");
                                clientsNameMapping.put(clientSock, nickName);
                                System.out.println(nickName + " connected");
                            }
                            noNameClients.remove(clientSock);
                            break;
                        }

                        // Ordinary message
                        case 2: {
                            String nickName = null;
                            if (clientsNameMapping.containsKey(clientSock)) {
                                nickName = clientsNameMapping.get(clientSock);
                            } else {
                                System.err.println("Someone unauthorized is trying to sengMsg");
                                ChatUtils.sendMsg(MsgHandler.error("You haven't authorized"), clientSock);
                                continue;
                            }
                            String[] parsedMsg = MsgHandler.parseMessage(clientsBuffer.get(clientSock));
                            if (parsedMsg == null) {
                                badTries.put(clientSock, new Integer(badTries.get(clientSock) + 1));
                                if (badTries.get(clientSock) == 10000) {
                                    disconnectClient(clientSock);
                                }
                                break;
                            }
                            if (!isMsgCorrect(parsedMsg[0], parsedMsg[1], clientSock)) {
                                continue;
                            }
                            if (!nickName.equals(parsedMsg[0])) {
                                System.out.println(nickName + " is trying to writer as " + parsedMsg[0]);
                                ChatUtils.sendMsg(MsgHandler.error("You must use your own nickname"), clientSock);
                            }
                            for (SocketChannel outputSock : clientsNameMapping.keySet()) {
                                if (!outputSock.equals(clientSock)) {
                                    ChatUtils.sendMsg(MsgHandler.message(nickName, parsedMsg[1]), outputSock);
                                }
                            }
                            break;
                        }

                        // Bye message
                        case 3: {
                            disconnectClient(clientSock);
                            break;
                        }

                        // Error message
                        case 127: {
                            String nickName = null;
                            if (clientsNameMapping.containsKey(clientSock)) {
                                nickName = clientsNameMapping.get(clientSock);
                            } else {
                                System.err.println("Someone unauthorized is trying to sengMsg");
                                ChatUtils.sendMsg(MsgHandler.error("You haven't authorized"), clientSock);
                                continue;
                            }
                            String errorMsg = MsgHandler.parseHelloAndError(clientsBuffer.get(clientSock));
                            System.out.println(nickName + " sended error message: " + errorMsg);
                            disconnectClient(clientSock);
                            break;
                        }

                        // Unknown message
                        default: {
                            String nickName = null;
                            if (clientsNameMapping.containsKey(clientSock)) {
                                nickName = clientsNameMapping.get(clientSock);
                                System.out.println("Unknown type message from: " + nickName);
                                ChatUtils.sendMsg(MsgHandler.error("Incorrect message"), clientSock);
                            } else {
                                System.err.println("Someone unauthorized is trying to sengMsg");
                                ChatUtils.sendMsg(MsgHandler.error("You haven't authorized"), clientSock);
                            }
                            disconnectClient(clientSock);
                        }
                    }
                } else {
                    disconnectClient(clientSock);
                }
            }
        }
        selectedKeys.clear();
    }

    private void listen(int port) throws Throwable {
        if (this.port == -1) {
            if (port < 0 || port > 65535) {
                System.err.println("listen: incorrect port");
            } else {
                if (!serverSocket.isOpen()) {
                    serverSocket = ServerSocketChannel.open();
                    serverSocket.configureBlocking(false);
                }
                serverSocket.socket().bind(new InetSocketAddress(port));
                serverSocket.register(multiplexor, SelectionKey.OP_ACCEPT);
                this.port = port;
            }
        } else {
            System.err.println("listen: already listen this port");
        }
    }

    private void stop() throws Throwable {
        port = -1;
        for (SocketChannel client : clientsNameMapping.keySet()) {
            if (client.isConnected()) {
                ChatUtils.sendMsg(MsgHandler.bye(), client);
                Closer.close(client);
            }
        }
        Closer.close(serverSocket);
        clientsNameMapping.clear();
        clientsBuffer.clear();
        badTries.clear();
    }

    private SocketChannel getSocketByName(String userName) {
        SocketChannel result = null;
        for (Entry<SocketChannel, String> iter : clientsNameMapping.entrySet()) {
            if (iter.getValue().equals(userName)) {
                result = iter.getKey();
                break;
            }
        }
        return result;
    }

    public void run() throws Throwable {
        String command = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            if (reader.ready()) {
                command = reader.readLine();
                if (command.matches("/listen\\s+.+")) {
                    command = command.replaceFirst("/listen\\s+", "");
                    int port;
                    try {
                        port = Integer.parseInt(command);
                    } catch (Throwable incorrectEnter) {
                        System.err.println("listen: incorrect port");
                        continue;
                    }
                    listen(port);
                } else if (command.equals("/stop")) {
                    if (port != -1) {
                        stop();
                    }
                } else if (command.equals("/list")) {
                    if (clientsNameMapping.values().isEmpty()) {
                        System.out.println("Nobody connected");
                    } else {
                        System.out.println("Clients:");
                        for (String clientName : clientsNameMapping.values()) {
                            System.out.println(clientName);
                        }
                    }
                } else if (command.matches("/send\\s+.+")) {
                    command = command.replaceFirst("/send\\s+", "");
                    int firstWhiteSpace = command.indexOf(' ');
                    if (firstWhiteSpace == -1) {
                        System.err.println("send: empty message");
                        continue;
                    }
                    String userName = command.substring(0, firstWhiteSpace);
                    SocketChannel to = getSocketByName(userName);
                    if (to != null) {
                        String msg = command.substring(firstWhiteSpace).trim();
                        ChatUtils.sendMsg(MsgHandler.message(serverName, msg), to);
                    } else {
                        System.err.println("send: no such client as " + userName);
                    }
                } else if (command.matches("/sendall\\s+.+")) {
                    String msg = command.replaceFirst("/sendall\\s+", "").trim();
                    sendAll(msg);
                } else if (command.matches("/kill\\s+.+")) {
                    String userName = command.replaceAll("/kill\\s+", "").trim();
                    SocketChannel userSocket = getSocketByName(userName);
                    if (userSocket == null) {
                        System.err.println("kill: no such client as " + userName);
                    } else {
                        disconnectClient(userSocket);
                    }
                } else if (command.equals("/exit")) {
                    if (port != -1) {
                        stop();
                    }
                    Closer.close(multiplexor);
                    break;
                } else {
                    System.err.println("Unknown command: " + command);
                }
            }
            serveClients();
        }
    }
}