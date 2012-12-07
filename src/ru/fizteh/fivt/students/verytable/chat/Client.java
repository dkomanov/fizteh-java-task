package ru.fizteh.fivt.students.verytable.chat;

import ru.fizteh.fivt.students.verytable.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;

public class Client implements Runnable {


    private final ByteBuffer buffer = ByteBuffer.allocate(512);
    private static String curHost;
    private static int curPort;
    private static String clientName;
    private static boolean isConnected = false;
    private static SocketChannel curSocketChannel;
    private static Map<String, SocketChannel> servers = new HashMap<String, SocketChannel>();

    public Client(String host, int port, String name) {

        this.curHost = host;
        this.curPort = port;
        this.clientName = name;
        this.isConnected = true;
        new Thread(this).start();
    }

    public void run() {

        Selector selector = null;
        try {
            curSocketChannel = SocketChannel.open();
            InetSocketAddress isa = new InetSocketAddress(curHost, curPort);
            curSocketChannel.connect(isa);
            curSocketChannel.configureBlocking(false);
            selector = Selector.open();
            curSocketChannel.register(selector, SelectionKey.OP_READ);
            servers.put(curHost, curSocketChannel);
            send(curSocketChannel, MessageUtils.hello(clientName));
        } catch (Exception ex) {
            System.err.println("Error in getting connection: " + ex.getMessage());
            System.exit(1);
        }

        try {
            while (true) {
                int num = selector.select();
                if (num == 0) {
                    continue;
                }
                Set keys = selector.selectedKeys();
                Iterator it = keys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = (SelectionKey) it.next();
                    if ((key.readyOps() & SelectionKey.OP_READ) ==
                            SelectionKey.OP_READ) {
                        SocketChannel sc = null;
                        try {
                            sc = (SocketChannel) key.channel();
                            if (sc.equals(curSocketChannel)) {
                                boolean ok = processInput(sc);
                                if (!ok) {
                                    key.cancel();
                                    Socket s = null;
                                    try {
                                        s = sc.socket();
                                        s.close();
                                    } catch (IOException ie) {
                                        System.err.println("Error closing socket "
                                                + s + ": " + ie);
                                        System.exit(1);
                                    }
                                }
                            }
                        } catch (IOException ie) {
                            key.cancel();
                            String serverToQuitFrom = getKey(sc);
                            if (serverToQuitFrom != null) {
                                IOUtils.closeFile(serverToQuitFrom, sc);
                                servers.remove(serverToQuitFrom);
                                System.out.println(serverToQuitFrom + " was closed.");
                            }
                        }
                    }
                }
                keys.clear();
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }

    static String getKey(SocketChannel sc) {
        Iterator mapIt = servers.entrySet().iterator();
        Map.Entry me;
        while (mapIt.hasNext()) {
            me = (Map.Entry) mapIt.next();
            if (me.getValue() == sc) {
                return (String) me.getKey();
            }
        }
        return null;
    }

    private boolean processInput(SocketChannel sc) throws Exception {
        buffer.clear();
        sc.read(buffer);
        buffer.flip();

        if (buffer.limit() == 0) {
            return false;
        }
        byte[] byteMessage = buffer.array();
        switch (byteMessage[0]) {
            case 2:
                ArrayList<String> message = getMessage(byteMessage);
                if (message == null) {
                    System.out.println("I received too large message.");
                    disconnect(true);
                    return true;
                }
                String sender = message.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < message.size(); ++i) {
                    sb.append(message.get(i));
                }
                System.out.println(sender + ": " + sb.toString());
                break;
            case 3:
                disconnect(false);
                break;
            case 127:
                message = getMessage(byteMessage);
                sender = message.get(0);
                sb = new StringBuilder();
                for (int i = 1; i < message.size(); ++i) {
                    sb.append(message.get(i));
                }
                System.out.println("error message: " + sender + sb.toString());
                disconnect(false);
                break;
            default:
                disconnect(false);
        }
        return true;
    }

    static void disconnect(boolean mustSendByeMessage) {
        try {
            if (mustSendByeMessage) {
                send(curSocketChannel, MessageUtils.bye());
            }
            if (curSocketChannel != null) {
                curSocketChannel.close();
                servers.remove(curHost);
                isConnected = false;
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        System.out.println("You have successfully disconnected from the server");
    }

    static void list() {
        Iterator it = servers.entrySet().iterator();
        Map.Entry me;
        while (it.hasNext()) {
            me = (Map.Entry) it.next();
            System.out.println(me.getKey() + " "
                    + me.getValue());
        }
    }

    static void use(String hostToUse) {
        SocketChannel sc = servers.get(hostToUse);
        if (sc != null) {
            curSocketChannel = sc;
        } else {
            System.err.println("You haven't connected to this server yet.");
        }
    }

    static void exit() {
        try {
            Iterator it = servers.entrySet().iterator();
            Map.Entry me;
            while (it.hasNext()) {
                me = (Map.Entry) it.next();
                SocketChannel scToClose = (SocketChannel) me.getValue();
                if (scToClose != null) {
                    send(scToClose, MessageUtils.bye());
                    scToClose.close();
                }
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        servers.clear();
        System.out.println("Exit successfully.");
        System.exit(0);
    }

    static void send(SocketChannel sc, byte[] message) {
        try {
            if (Client.isConnected) {
                ByteBuffer bf = ByteBuffer.wrap(message);
                sc.write(bf);
            }
        } catch (Exception ex) {
            System.err.println("Error in sending." + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
    }

    static public ArrayList<String> getMessage(byte[] byteMessage) {
        ArrayList<String> message = new ArrayList<String>();
        ByteBuffer buffer = ByteBuffer.wrap(byteMessage);
        int messageType = buffer.get();
        int messageCount = buffer.get();
        if (messageCount == -1) {
            disconnect(false);
            System.err.println("error in message reading");
            System.exit(1);
        }
        for (int i = 0; i < messageCount; ++i) {
            int length = buffer.getInt();
            byte[] tmp = new byte[length];
            if (tmp.length < 512) {
                buffer.get(tmp);
            } else {
                System.err.println("To large message was received from server.");
                return null;
            }
            message.add(new String(tmp, Charset.forName("UTF-8")));
        }
        return message;
    }

    static public void main(String args[]) throws Exception {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        StringTokenizer tokenizer;
        String curCommand;
        String str = "";
        String nickname = "";

        if (args.length == 0) {
            System.err.println("Usage: nickname\n");
            System.exit(1);
        } else {
            nickname = args[0];
        }

        while (true) {
            curCommand = "";
            try {
                str = br.readLine();
            } catch (Exception ex) {
                System.err.println("Input canceled: " + ex.getMessage());
                System.exit(1);
            }
            tokenizer = new StringTokenizer(str, " \t");
            if (tokenizer.hasMoreTokens()) {
                curCommand = tokenizer.nextToken();
            }
            if (curCommand.equals("/connect")) {
                if (tokenizer.countTokens() != 1) {
                    System.err.println("Connect usage: /connect host:portNumber");
                } else {
                    String hostPort = tokenizer.nextToken();
                    int hostPortSeparator = hostPort.indexOf(':');
                    if (hostPortSeparator == -1) {
                        System.err.println("Connect usage: /connect host:portNumber");
                        continue;
                    }
                    String host = hostPort.substring(0, hostPort.indexOf(':'));
                    int port;
                    try {
                        port = Integer.parseInt(hostPort.substring(hostPort.indexOf(':') + 1));
                    } catch (Exception ex) {
                        System.err.println("Invalid hostPort: " + hostPort);
                        continue;
                    }
                    System.out.println(host + ":" + port);
                    if (servers.containsKey(host)) {
                        System.out.println("You are already connected to this host");
                    } else {
                        new Client(host, port, nickname);
                    }
                }
            } else if (curCommand.equals("/disconnect")) {
                if (tokenizer.countTokens() != 0) {
                    System.err.println("Disconnect usage: /disconnect");
                } else {
                    disconnect(true);
                }
            } else if (curCommand.equals("/whereami")) {
                if (Client.isConnected) {
                    System.out.println("You are using this host: "
                            + Client.curHost);
                } else {
                    System.out.println("You haven't connected yet.");
                }
            } else if (curCommand.equals("/list")) {
                if (tokenizer.countTokens() != 0) {
                    System.err.println("List usage: /list");
                } else {
                    list();
                }
            } else if (curCommand.equals("/use")) {
                if (tokenizer.countTokens() != 1) {
                    System.err.println("Use usage: /use host");
                } else {
                    use(tokenizer.nextToken());
                }
            } else if (curCommand.equals("/exit")) {
                if (tokenizer.countTokens() != 0) {
                    System.err.println("Exit usage: /exit");
                } else {
                    exit();
                }
            } else {
                if (Client.isConnected) {
                    send(curSocketChannel, MessageUtils.message(nickname, str));
                } else {
                    System.err.println("You haven't got connection yet.");
                }
            }
        }
    }
}
