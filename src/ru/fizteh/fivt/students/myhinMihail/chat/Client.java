package ru.fizteh.fivt.students.myhinMihail.ñhat;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.Map.Entry;

import ru.fizteh.fivt.students.myhinMihail.Utils;

public class Client {
    public static BufferedReader reader = null;
    public static Map<String, Utils.Pair<SocketChannel, Selector> > servers = null;
    public static String currentServer = null;
    public static boolean connected = false;
    
    public static boolean checkMessage(String s1, String s2) {
        if (s1 == null || s1 == null) {
            System.out.println("Bad message from <server>");
            return false;
        }
        
        return true;
    }
    
    public static void connect(StringTokenizer st, String nick) {
        try {
            if (st.hasMoreTokens()) {
                String adres = st.nextToken();
                String[] adr =  adres.split(":");
                if (adr.length == 2) {

                    if (servers.containsKey(adr[0])) {
                        System.out.println("connect: already connected");
                    } else {
                        int port = -1;
                        try {
                            port = Integer.parseInt(adr[1]);
                        } catch (NumberFormatException nfe) {
                            System.err.println("connect: incorrect port name");
                            return;
                        }
                        
                        if (port < 0 || port > 65535) {
                            System.err.println("connect: port name is out of range 0 - 65535");
                            return;
                        }
                        
                        Utils.Pair<SocketChannel, Selector> pair = 
                                new Utils.Pair<SocketChannel, Selector>(SocketChannel.open(), Selector.open());
                        pair.first.connect(new InetSocketAddress(adr[0], port));
                        pair.first.configureBlocking(false);
                        pair.first.register(pair.second, SelectionKey.OP_READ);
                        servers.put(adres, pair);
                        currentServer = adres;
                        sendMessage(pair.first, MessageUtils.hello(nick));
                        connected = true;
                    }
                } else {
                    System.err.println("connect: input host:port");
                }
            } else {
                System.err.println("connect: input host:port");
            }
        } catch (Exception expt) {
            Utils.printErrorAndExit(expt.getMessage());
        }
    }

    public static void disconnect(boolean byServer) {
        sendMessage(servers.get(currentServer).first, MessageUtils.bye());
        Utils.tryClose(servers.get(currentServer).first);
        Utils.tryClose(servers.get(currentServer).second);
        servers.remove(currentServer);
        if (!byServer) {
            System.out.println("disconnect: successfully disconnected from " + currentServer);
        } else {
            System.out.println("You were disconnected from server");
        }
        
        if (servers.isEmpty()) {
            connected = false;
            if (!byServer) {
                System.out.println("disconnect: you are not connected now");
            }
        } else {
            currentServer = servers.keySet().iterator().next();
            System.out.println("disconnect: now your server is " + currentServer);
        }
    }

    public static void readCommand(String nick) {
        try {
            
            if (!reader.ready()) {
                return;
            }
            
            String inLine = reader.readLine();
            StringTokenizer st = new StringTokenizer(inLine, " ");
            if (st.hasMoreTokens()) {
                switch (st.nextToken()) {
                    case "/connect": 
                        connect(st, nick);
                        break;
                    
                    case "/disconnect":
                        if (connected) {
                            disconnect(false);
                        } else {
                            System.err.println("disconnect: you are not connected");
                        }
                        break;
                    
                    case "/whereami":
                        if (connected) {
                            System.out.println(currentServer);
                        } else {
                            System.err.println("whereami: you are not connected");
                        }
                        break;
                    
                    case "/list":
                        if (connected) {
                            for (Entry<String, Utils.Pair<SocketChannel, Selector> > server : servers.entrySet()) {
                                System.out.println(server.getKey());
                            }
                        } else {
                            System.err.println("list: you are not connected");
                        }
                        break;
                    
                    case "/use":
                        if (st.hasMoreTokens()) {
                            String server = st.nextToken();
                            if (servers.containsKey(server)) {
                                currentServer = server;
                            } else {
                                System.err.println("use: you are not connected to this server");
                            }
                        } else {
                            System.err.println("use: input host name");
                        }
                        break;
                    
                    case "/exit":
                        for (Entry<String, Utils.Pair<SocketChannel, Selector> > server : servers.entrySet()) {
                            sendMessage(server.getValue().first, MessageUtils.bye());
                            Utils.tryClose(server.getValue().first);
                            Utils.tryClose(server.getValue().second);
                        }
                    
                        servers.clear();
                        System.exit(0);
                        break;
                    
                    default:
                        if (connected) {
                            sendMessage(servers.get(currentServer).first, MessageUtils.message(nick, inLine));
                        } else {
                            System.out.println("You are not connected to any server, use \"/connect host:port\"");
                        }
                        break;
                }
            } else {
                System.err.println("Empty input");
            }
        } catch (Exception expt) {
            Utils.printErrorAndExit(expt.getMessage());
        }
    }

    public static void checkServer() {
        try {
            
            if (!connected || servers.get(currentServer).second.selectNow() == 0) {
                return;
            }
            
            Set<SelectionKey> keys = servers.get(currentServer).second.selectedKeys();

            for (SelectionKey key : keys) {

                if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                    ByteBuffer bytes = ByteBuffer.allocate(4096);

                    if (((SocketChannel)key.channel()).read(bytes) == -1) {
                        disconnect(true);
                    } else {
                        byte[] message = bytes.array();
                        switch (message[0]) {
                            case 2:
                                Utils.Pair<String, String> pair = MessageUtils.parseMessage(message);
                                if (!checkMessage(pair.first, pair.second)) {
                                    continue;
                                }
                                
                                System.out.println(pair.first + ": " + pair.second);
                                break;
                            
                            case 3: 
                                disconnect(true);
                                break;
                            
                            case 127:
                                Utils.Pair<String, String> pair2 = MessageUtils.parseMessage(message);
                                if (!checkMessage(pair2.first, "")) {
                                    continue;
                                }
                                
                                System.out.println("Error: " + pair2.first);
                                break;
                                
                            default:
                                //ignore
                                break;
                        }
                    }
                    
                }
            }
            keys.clear();
        } catch (Exception expt) {
            Utils.printErrorAndExit(expt.getMessage());
        }
    }

    public static void sendMessage(SocketChannel sc, byte[] message) {
        try {
            if(sc != null) {
                sc.write(ByteBuffer.wrap(message));
            } else {
                System.err.println("Error while sending: SocketChannel is corrupted");
            }
        } catch (Exception expt) {
            Utils.printErrorAndExit(expt.getMessage());
        }
    }
    
    public static void main(String[] args) {
        try {
            reader = new BufferedReader(new InputStreamReader(System.in));
            servers = new LinkedHashMap<String, Utils.Pair<SocketChannel, Selector> >();
            currentServer = "";

            if (args.length == 0) {
                Utils.printErrorAndExit("Error: no nick in arguments");
            }

            while (true) {
                readCommand(args[0]);
                checkServer();
            }
            
        } catch (Exception expt) {
            Utils.printErrorAndExit(expt.getMessage());
        }
    }
    
}
