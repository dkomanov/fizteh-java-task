package ru.fizteh.fivt.students.verytable.chat;

import ru.fizteh.fivt.students.verytable.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;

public class Server implements Runnable {
    private static int curPort;
    private static ServerSocketChannel curServerSocketChannel;
    private static Map<String, SocketChannel> clients;
    private static Map<SocketChannel, Queue<Byte>> forClientsMessages;
    private static Set<SocketChannel> connectionSocketChannels = new HashSet<SocketChannel>();
    private static boolean isListening = false;
    private final ByteBuffer buffer = ByteBuffer.allocate(512);

    public Server(int port) {
        this.curPort = port;
        this.clients = new HashMap<String, SocketChannel>();
        this.forClientsMessages = new HashMap<SocketChannel, Queue<Byte>>();
        new Thread(this).start();
    }

    public void run() {
        try {
            curServerSocketChannel = ServerSocketChannel.open();
            curServerSocketChannel.configureBlocking(false);
            ServerSocket ss = curServerSocketChannel.socket();
            InetSocketAddress isa = new InetSocketAddress(curPort);
            ss.bind(isa);
            Selector selector = Selector.open();
            curServerSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            Server.isListening = true;
            System.out.println("Listening on port " + curPort);
            while (true) {
                int num = selector.select();
                if (num == 0) {
                    continue;
                }
                Set keys = selector.selectedKeys();
                Iterator it = keys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = (SelectionKey) it.next();
                    if ((key.readyOps() & SelectionKey.OP_ACCEPT) ==
                            SelectionKey.OP_ACCEPT) {
                        Socket s = ss.accept();
                        System.out.println("Got connection from " + s);
                        SocketChannel sc = s.getChannel();
                        connectionSocketChannels.add(sc);
                        forClientsMessages.put(sc, new LinkedList<Byte>());
                        sc.configureBlocking(false);
                        sc.register(selector, SelectionKey.OP_READ);
                    } else if ((key.readyOps() & SelectionKey.OP_READ) ==
                            SelectionKey.OP_READ) {
                        SocketChannel sc = null;
                        try {
                            sc = (SocketChannel) key.channel();
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
                        } catch (IOException ie) {
                            key.cancel();
                            String userToKill = getKey(sc);
                            if (userToKill != null) {
                                IOUtils.closeFile(userToKill, sc);
                                clients.remove(userToKill);
                                System.out.println(userToKill + " was closed.");
                            }
                        }
                    }
                }
                keys.clear();
            }
        } catch (IOException ie) {
            System.err.println(ie);
        }
    }

    static String getKey(SocketChannel sc) {
        Iterator mapIt = clients.entrySet().iterator();
        Map.Entry me;
        while (mapIt.hasNext()) {
            me = (Map.Entry) mapIt.next();
            if (me.getValue() == sc) {
                return (String) me.getKey();
            }
        }
        return null;
    }

    private boolean processInput(SocketChannel sc) throws IOException {
        System.out.println("Processing input:");
        buffer.clear();
        int len = 0;
        try {
            len = sc.read(buffer);
        } catch (Exception ex) {
            String badGuy = getKey(sc);
            if (sc != null && len > 0) {
                send(sc, MessageUtils.error("Bad message was send from you."));
                kill(badGuy, false);
                return true;
            }
        }
        if (len < 0) {
            System.err.println("Emergency client exit.");
            String exitedUser = getKey(sc);
            if (exitedUser != null) {
                kill(exitedUser, false);
                return false;
            }
        }
        buffer.flip();

        if (buffer.limit() == 0) {
            return false;
        }

        for (int i = 0; i < len; ++i) {
            forClientsMessages.get(sc).add(buffer.array()[i]);
        }

        Byte[] bm = new Byte[forClientsMessages.get(sc).size()];
        byte[] byteMessage = MessageUtils.toPrimitive(forClientsMessages.get(sc).toArray(bm));
        ArrayList<String> message;
        message = getMessage(sc, byteMessage);
        if (message == null) {
            return true;
        }

        switch (byteMessage[0]) {
            case 1:
                String nick = message.get(0);
                if (nick.replace("\\s", "").equals("")) {
                    send(sc, MessageUtils.error("Nick must be visible."));
                    connectionSocketChannels.remove(sc);
                    return true;
                }
                if (Server.clients.containsKey(message.get(0))) {
                    send(sc, MessageUtils.error("Such nick is already exists."));
                    connectionSocketChannels.remove(sc);
                    try {
                        sc.close();
                    } catch (Exception ex) {
                        System.err.println(ex.getMessage());
                    }
                } else {
                    nick = message.get(0);
                    if (4 > nick.length() || nick.length() > 16) {
                        send(sc, MessageUtils.error("Nick length must be in [4, 16]."));
                        connectionSocketChannels.remove(sc);
                    } else {
                        System.out.println("nick got: " + nick);
                        send(sc, MessageUtils.message("<server>", "You have "
                                + "successfully registered."));
                        Server.clients.put(nick, sc);
                        connectionSocketChannels.remove(sc);
                    }
                }
                break;
            case 2:
                String sender = message.get(0);
                if (!clients.containsKey(sender)) {
                    System.err.println("No such user!");
                    send(sc, MessageUtils.error("You are disconnected for bad behaviour."));
                    kill(sender, false);
                    return true;
                }
                System.out.println("message from: " + sender);
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < message.size(); ++i) {
                    sb.append(message.get(i));
                }
                System.out.println(sb.toString());
                Iterator it = clients.entrySet().iterator();
                Map.Entry me;
                while (it.hasNext()) {
                    me = (Map.Entry) it.next();
                    if (!(me.getKey()).equals(sender)) {
                        send((SocketChannel) me.getValue(),
                                MessageUtils.message(sender, sb.toString()));
                    }
                }
                break;
            case 3:
                kill(getKey(sc), false);
                System.out.println("one exited.");
                break;
            case 127:
                sender = getKey(sc);
                sb = new StringBuilder("");
                System.out.println("Error from " + sender + ": " + sb.toString());
                it = clients.entrySet().iterator();
                while (it.hasNext()) {
                    me = (Map.Entry) it.next();
                    if (!(me.getKey()).equals(sender)) {
                        send((SocketChannel) me.getValue(),
                                MessageUtils.error(sb.toString()));
                    }
                }
                exit();
                break;
            default:
                System.out.println("Wrong message type got from " + getKey(sc));
                kill(getKey(sc), true);
        }
        System.out.println("Processed " + buffer.limit() + " from " + sc);
        byte[] b = {2, 1, 0, 0, 0, 2, 'h', 'i'};
        ByteBuffer bb = ByteBuffer.wrap(b);
        sc.write(bb);
        return true;
    }

    synchronized static void stop() {
        try {
            if (curServerSocketChannel != null) {
                curServerSocketChannel.close();
            }
            Iterator it = clients.entrySet().iterator();
            Map.Entry me;
            while (it.hasNext()) {
                me = (Map.Entry) it.next();
                kill((String) me.getKey(), true);
            }
        } catch (Exception ex) {
            System.out.println("error in stop: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
        Server.isListening = false;
    }

    synchronized static void list() {
        Iterator it = clients.entrySet().iterator();
        Map.Entry me;
        while (it.hasNext()) {
            me = (Map.Entry) it.next();
            System.out.println(me.getKey() + " " + me.getValue());
        }
    }

    synchronized static void send(SocketChannel sc, byte[] message) {
        try {
            if (sc.isConnected()) {
                ByteBuffer bf = ByteBuffer.wrap(message);
                sc.write(bf);
            }
        } catch (Exception ex) {
            System.err.println("Error in sending." + ex.getMessage());
            System.exit(1);
        }
    }

    synchronized static void sendAll(byte[] message) {
        Iterator it = clients.entrySet().iterator();
        Map.Entry me;
        while (it.hasNext()) {
            me = (Map.Entry) it.next();
            send((SocketChannel) me.getValue(), message);
        }
    }

    synchronized static void kill(String clientToKill, boolean mustSendByeMessage) {
        try {
            SocketChannel sc = clients.get(clientToKill);
            if (sc != null) {
                if (mustSendByeMessage) {
                    send(sc, MessageUtils.bye());
                }
                sc.close();
                forClientsMessages.remove(clients.get(clientToKill));
                clients.remove(clientToKill);
                sendAll(MessageUtils.message("<server>",
                        clientToKill + " exit from the chat."));
                return;
            }
        } catch (Exception ex) {
            System.out.println("error in killing: " + ex.getMessage());
            System.exit(1);
        }
        System.err.println("No such user.");
    }

    synchronized static void exit() {
        stop();
        System.out.println("Exit successfully.");
        System.exit(0);
    }

    static public ArrayList<String> getMessage(SocketChannel sc, byte[] byteMessage) {
        ArrayList<String> message = new ArrayList<String>();
        ByteBuffer buffer = ByteBuffer.wrap(byteMessage);

        if (forClientsMessages.get(sc).size() < 1) {
            return null;
        }
        int messageType = buffer.get();
        if (messageType != 1 && messageType != 2 && messageType != 3 && messageType != 127) {
            System.err.println("Bad type");
            kill(getKey(sc), false);
            return null;
        }
        if (forClientsMessages.get(sc).size() < 2) {
            return null;
        }
        int messageCount = buffer.get();
        if (messageCount < 1) {
            System.err.println("Bad messages cnt.");
            kill(getKey(sc), false);
            return null;
        }

        int len = 2;
        for (int i = 0; i < messageCount; ++i) {
            if (forClientsMessages.get(sc).size() < len + 4) {
                return null;
            }
            int length = buffer.getInt();
            len += 4;
            if (length < 0 && len > 512) {
                System.err.println("array size < 0 || array size > 512");
                kill(getKey(sc), false);
                return null;
            }
            byte[] tmp = new byte[length];
            if (forClientsMessages.get(sc).size() < len + length) {
                return null;
            }
            if (tmp.length < 512) {
                buffer.get(tmp);
                len += length;
            } else {
                System.out.println("Client tried to send very big message.");
                return null;
            }
            message.add(new String(tmp, Charset.forName("UTF-8")));
        }
        for (int i = 0; i < len; ++i) {
            forClientsMessages.get(sc).remove();
        }
        return message;
    }

    static public void main(String args[]) throws Exception {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        StringTokenizer tokenizer;
        String curCommand;
        String str = null;

        while (true) {
            try {
                str = br.readLine();
            } catch (IOException ex) {
                System.err.println("Input canceled: " + ex.getMessage());
                System.exit(1);
            }
            tokenizer = new StringTokenizer(str, " \t");
            curCommand = tokenizer.nextToken();
            if (curCommand.equals("/listen")) {
                if (tokenizer.countTokens() != 1) {
                    System.err.println("Listen usage: /listen portNumber.");
                } else {
                    if (!isListening) {
                        new Server(Integer.parseInt(tokenizer.nextToken()));
                    } else {
                        System.err.println("Server is already listening.");
                    }
                }
            } else if (curCommand.equals("/stop")) {
                if (tokenizer.countTokens() != 0) {
                    System.err.println("Stop usage: /stop");
                } else {
                    if (isListening) {
                        stop();
                    } else {
                        System.err.println("Nothing to stop.");
                    }
                }
            } else if (curCommand.equals("/list")) {
                if (tokenizer.countTokens() != 0) {
                    System.err.println("List usage: /list");
                }
                list();
            } else if (curCommand.equals("/send")) {
                if (tokenizer.countTokens() < 2) {
                    System.err.println("Send usage: /send userName message");
                } else {
                    String userName = tokenizer.nextToken();
                    StringBuilder sb = new StringBuilder();
                    while (tokenizer.hasMoreTokens()) {
                        sb.append(tokenizer.nextToken());
                        sb.append(" ");
                    }
                    SocketChannel addresseeSC = clients.get(userName);
                    if (addresseeSC != null) {
                        send(clients.get(userName),
                                MessageUtils.message("<server>", sb.toString()));
                    } else {
                        System.err.println("No such client: " + userName);
                    }
                }
            } else if (curCommand.equals("/sendall")) {
                if (tokenizer.countTokens() < 1) {
                    System.err.println("SendAll usage: /sendall message.");
                } else {
                    StringBuilder sb = new StringBuilder();
                    while (tokenizer.hasMoreTokens()) {
                        sb.append(tokenizer.nextToken());
                        sb.append(" ");
                    }
                    sendAll(MessageUtils.message("<server>", sb.toString()));
                }
            } else if (curCommand.equals("/kill")) {
                if (tokenizer.countTokens() != 1) {
                    System.err.println("Kill usage: /kill user.");
                } else {
                    kill(tokenizer.nextToken(), true);
                }
            } else if (curCommand.equals("/exit")) {
                if (tokenizer.countTokens() != 0) {
                    System.err.println("Exit usage: /exit");
                } else {
                    exit();
                }
            } else {
                System.err.println("Unknown command. Available commands:\n"
                        + "/stop, /list, /send, /sendall, "
                        + "/kill, exit.");
            }
        }
    }
}
