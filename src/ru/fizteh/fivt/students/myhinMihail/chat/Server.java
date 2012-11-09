package ru.fizteh.fivt.students.myhinMihail.ñhat;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.Map.Entry;

import ru.fizteh.fivt.students.myhinMihail.Utils;

public class Server {
    public static Map<String, SocketChannel> clients = null;
    public static BufferedReader reader = null;
    public static int[] port = new int[1];
    public static Selector selector = null;
    public static ServerSocketChannel serverSocket = null;
    
    public static boolean checkMessage(String s1, String s2, SocketChannel sc) {
        if (s1 == null || s1 == null) {
            sendMessage(sc, MessageUtils.message("<server>", "bad message"));
            System.out.println("Bad message from " + getNickFromSocket(sc));
            return false;
        }
        
        return true;
    }
    
    public static String getMessageFromTokens(StringTokenizer st) {
        StringBuilder sb = new StringBuilder();
        while (st.hasMoreTokens()) {
            sb.append(st.nextToken()).append(" ");
        }
        return sb.toString();
    }
    
    public static void listen(StringTokenizer st) {
        try {
            if (port[0] == -1) {
                if (st.hasMoreTokens()) {
                    
                    try {
                        port[0] = Integer.parseInt(st.nextToken());
                    } catch (NumberFormatException nfe) {
                        System.err.println("listen: incorrect port name");
                        return;
                    }
                    
                    if (port[0] < 0 || port[0] > 65535) {
                        System.err.println("listen: port name is out of range 0 - 65535");
                        port[0] = -1;
                        return;
                    }
                    
                    if (!serverSocket.isOpen()) {
                        serverSocket = ServerSocketChannel.open();
                        serverSocket.configureBlocking(false);
                    }
                    serverSocket.socket().bind(new InetSocketAddress(port[0]));
                    serverSocket.register(selector, SelectionKey.OP_ACCEPT);
                } else {
                    System.err.println("listen: no port name");
                }
            } else {
                System.err.println("Error: already listening port " + port[0]);
            }
        } catch (Exception expt) {
            Utils.printErrorAndExit(expt.getMessage());
        }
    }
    
    public static void stop() {
        try {
            if (port[0] != -1) {

                for (Entry<String, SocketChannel> client : clients.entrySet()) {
                    if (client.getValue().isConnected()) {
                        sendMessage(client.getValue(), MessageUtils.bye());
                        Utils.tryClose(client.getValue());
                    }
                }
                
                Utils.tryClose(serverSocket);
                clients.clear();
                port[0] = -1;
                
            } else {
                System.err.println("stop: already stopped");
            }
            
        } catch (Exception expt) {
            Utils.printErrorAndExit(expt.getMessage());
        }
    }
    
    public static void sendMessage(SocketChannel clientSocket, byte[] message) {
        try {
            if(clientSocket.isConnected()) {
                clientSocket.write(ByteBuffer.wrap(message));
            }
        } catch (Exception expt) {
            Utils.printErrorAndExit("Error: " + expt.getMessage());
        }
    }
    
    public static void notifyAllClients(String message) {
        for (Entry<String, SocketChannel> client : clients.entrySet()) {
            sendMessage(client.getValue(), MessageUtils.message("<server>", message));
        }
    }
    
    public static void kill(StringTokenizer st) {
        try {
            if (st.hasMoreTokens()) {
                String clientName = st.nextToken();
                
                if (clients.containsKey(clientName)) {
                    SocketChannel clientChannel = clients.get(clientName);
                    clients.remove(clientName);
                    sendMessage(clientChannel, MessageUtils.bye());
                    notifyAllClients(clientName + " leave this chatroom");
                    System.out.println(clientName + " leave this chatroom");
                    Utils.tryClose(clientChannel);
                } else {
                    System.err.println("kill: no such client");
                }
            } else {
                System.err.println("kill: no client name");
            }
        } catch (Exception expt) {
            Utils.printErrorAndExit(expt.getMessage());
        }
    }
    
    public static String getNickFromSocket(SocketChannel sc) {
        for (Entry<String, SocketChannel> client : clients.entrySet()) {
            if (client.getValue().equals(sc)) {
                return client.getKey();
            }
        }
        
        return null;
    }

    public static void disconnectClient(SocketChannel sc) {
        String clientName = getNickFromSocket(sc);
        if (clientName == null) {
            Utils.printErrorAndExit("Error: it could not happen");
        }
        
        sendMessage(sc, MessageUtils.bye());
        clients.remove(clientName);
        Utils.tryClose(sc);
        
        notifyAllClients(clientName + " leave this chatroom");
        System.out.println(clientName + " leave this chatroom");
    }
    
    public static void readCommand() {
        try {

            if (!reader.ready()) {
                return;
            }
            
            StringTokenizer tokens = new StringTokenizer(reader.readLine());
            
            if (tokens.hasMoreTokens()) {
                
                switch (tokens.nextToken()) {
                    case "/listen": 
                        listen(tokens);
                        break;
                        
                    case "/stop": 
                        stop();
                        break;
                        
                    case "/list": 
                        if (port[0] == -1) {
                            System.err.println("Error: start listening before");
                        }
                        
                        for (Entry<String, SocketChannel> client : clients.entrySet()) {
                            System.out.println(client.getKey());
                        }
                        
                        if ( clients.entrySet().isEmpty()) {
                            System.out.println("list: no clients online");
                        }
                        break;
                        
                    case "/send":
                        if (tokens.hasMoreTokens()) {
                            String name = tokens.nextToken();
                            if (clients.containsKey(name)) {
                                if (tokens.hasMoreTokens()) {
                                    sendMessage(clients.get(name), MessageUtils.message("<server>", getMessageFromTokens(tokens)));    
                                } else {
                                    System.err.println("send: message is empty");
                                }
                            } else {
                                System.err.println("send: no such client");
                            }
                        } else {
                            System.err.println("send: no client name");
                        }
                        break;
                        
                    case "/sendall":
                        if (tokens.hasMoreTokens()) {
                            notifyAllClients(getMessageFromTokens(tokens));
                        } else {
                            System.err.println("sendall: message is empty");
                        }
                        break;
                        
                    case "/kill":
                        kill(tokens);
                        break;
                        
                    case "/exit":
                        if (port[0] != -1) {
                            stop();
                        }
                        Utils.tryClose(selector);
                        System.exit(0);
                        break;
                        
                    default:
                        System.err.print("Error: unknown command");
                        break;
                }
                
            } else {
                System.err.println("Error: empty input");
            }
            
        } catch (Exception expt) {
            Utils.printErrorAndExit(expt.getMessage());
        }
    }
    
    public static void checkClients(List<SocketChannel> anonymousClients) {
        try {
            
            if (selector.selectNow() == 0) {
                return;
            }
            
            Set<SelectionKey> keys = selector.selectedKeys();
            for (SelectionKey key : keys) {
                
                if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
                    SocketChannel clientSocket = serverSocket.accept();
                    if (clientSocket == null) {
                        System.err.println("Error: can not accept");
                        continue;
                    }
                    anonymousClients.add(clientSocket);
                    clientSocket.configureBlocking(false);
                    clientSocket.register(selector, SelectionKey.OP_READ);
                } else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                    SocketChannel clientSocket = (SocketChannel)key.channel();
                    ByteBuffer bytes = ByteBuffer.allocate(4096);
                    
                    if (clientSocket.read(bytes)!= -1) {
                        byte[] message = bytes.array();
                        switch (message[0]) {
                            case 1: {
                                String nick = MessageUtils.parseMessage(message).first;
                                if (!checkMessage(nick, "", clientSocket)) {
                                    continue;
                                }
                                
                                if (clients.containsKey(nick) || nick.length() >  32) {
                                    if (nick.length() >  32) {
                                        sendMessage(clientSocket, MessageUtils.error("<server>: your nick is too long"));
                                    } else {
                                        sendMessage(clientSocket, MessageUtils.error("<server>: another user is using this nick"));
                                    }
                                    sendMessage(clientSocket, MessageUtils.bye());
                                    anonymousClients.remove(clientSocket);
                                    Utils.tryClose(clientSocket);
                                } else {
                                    System.out.println(nick + " enter this chatroom");
                                    notifyAllClients(nick + " enter this chatroom");
                                    
                                    sendMessage(clientSocket, MessageUtils.message("<server>", "Wellcome, your nick is " + nick));
                                    anonymousClients.remove(clientSocket);
                                    clients.put(nick, clientSocket);
                                }
                                break;
                            }
                        
                            case 2: {
                                String nick = getNickFromSocket(clientSocket);
                                if (nick == null) {
                                    Utils.printErrorAndExit("Error: it could not happen");
                                }
                                
                                Utils.Pair<String, String> pair = MessageUtils.parseMessage(message);
                                if (!checkMessage(pair.first, pair.second, clientSocket)) {
                                    continue;
                                }
                                
                                if (!pair.first.equals(nick)) {
                                    System.out.println("Cheater: " + nick);
                                    sendMessage(clientSocket, MessageUtils.message("<server>", "do not cheat"));
                                }

                                for (Entry<String, SocketChannel> client : clients.entrySet()) {
                                    if (!clientSocket.equals(client.getValue())) {
                                        sendMessage(client.getValue(), MessageUtils.message(nick, pair.second));
                                    }
                                }
                                break;
                            }
                        
                            case 3:
                                disconnectClient(clientSocket);
                                break;

                            case 127: {
                                String nick = getNickFromSocket(clientSocket);
                                
                                Utils.Pair<String, String> pair = MessageUtils.parseMessage(message);
                                if (!checkMessage(pair.first, pair.second, clientSocket)) {
                                    continue;
                                }
                                
                                if (!pair.first.equals(nick)) {
                                    System.out.println("Cheater: " + nick);
                                    sendMessage(clientSocket, MessageUtils.message("<server>", "do not cheat"));
                                }
                                System.out.println(nick + ": " + pair.second);
                                break;
                            } 
                        
                            default: 
                                System.out.println("Bad message from " + getNickFromSocket(clientSocket));
                                sendMessage(clientSocket, MessageUtils.message("<server>", "bad message"));
                                break;
                        }
                    } else {
                        disconnectClient(clientSocket);
                    }
                }
            }
            keys.clear();
        } catch (Exception expt) {
            expt.printStackTrace();
            Utils.printErrorAndExit(expt.getMessage());
        }
    }

    public static void main(String[] args) {
        port[0] = -1;
        
        try {
            reader = new BufferedReader(new InputStreamReader(System.in));
            clients = new LinkedHashMap<String, SocketChannel>();
            selector = Selector.open();
            List<SocketChannel> anonymousClients = new ArrayList<SocketChannel>();
            
            serverSocket = ServerSocketChannel.open();
            serverSocket.configureBlocking(false);

            while (true) {
                readCommand();
                checkClients(anonymousClients);
            }
            
        } catch (Exception expt) {
            Utils.printErrorAndExit("Error: " + expt.getMessage());
        }

    }

}
