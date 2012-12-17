package ru.fizteh.fivt.students.almazNasibullin.chat.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import ru.fizteh.fivt.students.almazNasibullin.IOUtils;
import ru.fizteh.fivt.students.almazNasibullin.chat.MessageType;
import ru.fizteh.fivt.students.almazNasibullin.chat.server.MessageUtils;

/**
 * 17.10.12
 * @author almaz
 */

public class Server {
    private static int port = -1;
    private static BufferedReader buf;
    private static Selector selector;
    // хранит ник и SocketChannel клиента
    private static Map<String, SocketChannel> clients = new TreeMap<String, SocketChannel>();
    private static List<Client> clientMessages = new ArrayList<Client>();
    /* хранит SocketChannel клиентов, у которых не утвержден ник,
    * т.е. возможно, что ник клиента совпадает с уже имеющимся
    */
    private static List<SocketChannel> withoutName = new ArrayList<SocketChannel>();
    private static ServerSocketChannel ssc;

    public static void main(String[] args) {
        try {
            selector = Selector.open();
            buf = new BufferedReader(new InputStreamReader(System.in));
            ssc = ServerSocketChannel.open();
            // для того, чтобы принимать новых клиетов
            ssc.configureBlocking(false);
            
            for (;;) {
                if (buf.ready()) {
                    handlerConsole();
                }
                int num = selector.selectNow();
                if (num == 0) {
                    continue;
                }
                handlerClients();
            }
        } catch (Exception e) {
            IOUtils.printErrorAndExit("Something bad happened: " + e.getMessage());
        }
    }

    public static void listen(StringTokenizer st) {
        try {
            if (port == -1) { // сервер может слушать в любой момент
                // времени только один порт
                if (st.hasMoreTokens()) {
                    String portNumber = st.nextToken();
                    port = Integer.parseInt(portNumber);
                    InetSocketAddress isa = new InetSocketAddress(
                            port);
                    if (!ssc.isOpen()) {
                        ssc = ServerSocketChannel.open();
                        ssc.configureBlocking(false);
                    }
                    ssc.socket().bind(isa);
                    ssc.register(selector,
                            SelectionKey.OP_ACCEPT);
                    System.out.println("Listening on port: " + port);
                } else {
                    IOUtils.printErrorAndExit("Usage: /listen portNumber");
                }
            } else {
                IOUtils.printErrorAndExit("Only one port is available!");
            }
        } catch (Exception e) {
            IOUtils.printErrorAndExit("Listening: " + e.getMessage());
        }
    }

    public static void stop() {
        try {
            if (port != -1) {
                port = -1;
                IOUtils.closeOrExit(ssc);
                Iterator iter = clients.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry pair = (Map.Entry)iter.next();
                    SocketChannel cur = (SocketChannel)pair.getValue();
                    if (cur != null && cur.isConnected()) {
                        sendMessage(cur, MessageUtils.bye());
                        IOUtils.closeOrExit(cur);
                    }
                }
                clients.clear();
                clientMessages.clear();
            } else {
                IOUtils.printErrorAndExit("Nothing to stop");
            }
        } catch (Exception e) {
            IOUtils.printErrorAndExit(e.getMessage());
        }
    }

    public static void kill(StringTokenizer st) {
        try {
            if (st.hasMoreTokens()) {
                String name = st.nextToken();
                if (!clients.containsKey(name)) {
                    // проверяем есть ли такой клиент вообще
                    IOUtils.printErrorAndExit(name + ": there is no such client");
                } else {
                    SocketChannel clientToClose = clients.get(name);
                    clients.remove(name);
                    for (Client c : clientMessages) {
                        if (c.sc.equals(clientToClose)) {
                            clientMessages.remove(c);
                            break;
                        }
                    }
                    withoutName.remove(clientToClose);
                    sendMessage(clientToClose, MessageUtils.bye());
                    IOUtils.closeOrExit(clientToClose);
                    sendMessageAll(name + " is offline", "server");
                    System.out.println(name + " is offline");
                }
            } else {
                IOUtils.printErrorAndExit("Usage: /send clientName");
            }
        } catch (Exception e) {
            IOUtils.printErrorAndExit(e.getMessage());
        }
    }

    public static void handlerConsole() {
        try {
            String str = buf.readLine();
            StringTokenizer st = new StringTokenizer(str, " \t");
            if (st.hasMoreTokens()) {
                String cmd = st.nextToken();
                if (cmd.equals("/listen")) {
                    listen(st);
                } else if (cmd.equals("/stop")) {
                    stop();
                } else if (cmd.equals("/list")) {
                    Iterator iter = clients.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry pair = (Map.Entry)iter.next();
                        System.out.println(pair.getKey());
                    }
                } else if (cmd.equals("/send")) {
                    if (st.hasMoreTokens()) {
                        String name = st.nextToken();
                        if (clients.containsKey(name)) {
                            // проверяем есть ли такой клиент вообще
                            StringBuilder sb = new StringBuilder();
                            while (st.hasMoreTokens()) {
                                sb.append(st.nextToken());
                                sb.append(" ");
                            }
                            sendMessage(clients.get(name),
                                    MessageUtils.message("server", sb.toString()));
                        } else {
                            IOUtils.printErrorAndExit(name + ": there is no such client");
                        }
                    } else {
                        IOUtils.printErrorAndExit("Usage: /send clientName");
                    }
                } else if (cmd.equals("/sendall")) {
                    StringBuilder sb = new StringBuilder();
                    while (st.hasMoreTokens()) {
                        sb.append(st.nextToken());
                        sb.append(" ");
                    }
                    sendMessageAll( sb.toString(), "server");
                } else if (cmd.equals("/kill")) {
                    kill(st);
                } else if (cmd.equals("/exit")) {
                    if (port != -1) {
                        stop();
                    }
                    try {
                        if (selector != null) {
                            selector.close();
                        }
                    } catch (Exception e) {
                        IOUtils.printErrorAndExit("Bad closing selector: " + e.getMessage());
                    }
                    clients.clear();
                    clientMessages.clear();
                    System.exit(0);
                } else {
                    IOUtils.printErrorAndExit(cmd + ": bad command");
                }
            }
        } catch (Exception e) {
            IOUtils.printErrorAndExit("handlerConsole: " + e.getMessage());
        }
    }

    public static void handlerClients() {
        try {
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator iter = keys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = (SelectionKey)iter.next();
                if ((key.readyOps() & SelectionKey.OP_ACCEPT) ==
                        SelectionKey.OP_ACCEPT) {
                    // получили новое соединение
                    //SocketChannel sc = ssc.get(0).accept();
                    SocketChannel sc = ssc.accept();
                    if (sc == null) {
                        IOUtils.printErrorAndExit("Bad accepting");
                    }
                    withoutName.add(sc);
                    clientMessages.add(new Client(sc, new byte[0]));
                    sc.configureBlocking(false);
                    sc.register(selector, SelectionKey.OP_READ);
                } else if ((key.readyOps() & SelectionKey.OP_READ) ==
                        SelectionKey.OP_READ) {
                    // в какой-то SocketChannel пришло сообщение
                    SocketChannel sc = (SocketChannel)key.channel();
                    boolean crash = getMessage(sc);
                    if (!crash) {
                        try {
                            List<List<String>> mess = MessageUtils.getMessages(clientMessages,
                                    sc);
                            if (mess.isEmpty()) {
                                continue;
                            }
                            for (List<String> l : mess) {
                                if (l.get(0).equals("HELLO")) {
                                    String nick = MessageUtils.getNickname(l);
                                    if (clients.containsKey(nick)) {
                                        // проверяем на уникальность ника
                                        sendMessage(sc, MessageUtils.error("server: This nick"
                                                + " already exists! Try to connect with "
                                                + "another nick!"));
                                        withoutName.remove(sc);
                                        for (Client c : clientMessages) {
                                            if (c.sc.equals(sc)) {
                                                clientMessages.remove(c);
                                                break;
                                            }
                                        }
                                        IOUtils.closeOrExit(sc);
                                    } else {
                                        // если ник уникален то добавляем нового клиента
                                        if (nick.length() >= 3 && nick.length() <=20) {
                                            System.out.println(nick + " is online");
                                            StringBuilder sb = new StringBuilder("Online Clients:");
                                            Iterator it = clients.entrySet().iterator();
                                            while (it.hasNext()) {
                                                Map.Entry pair = (Map.Entry)it.next();
                                                sb.append("\n").append((String)pair.getKey());
                                            }
                                            sendMessageAll(nick + " is online", "server");
                                            String msg = "";
                                            if (clients.isEmpty()) {
                                                msg = "You are first client!";
                                            } else {
                                                msg = sb.toString();
                                            }
                                            // сообщение новому клиенту с никами уже имеющихся
                                            sendMessage(sc, MessageUtils.message("server", msg));
                                            clients.put(nick, sc);
                                        } else {
                                            sendMessage(sc, MessageUtils.error("Nick "
                                                + "should be in length between 3 and 20"));
                                            sendMessage(sc, MessageUtils.bye());
                                            closeClient(sc);
                                        }
                                        withoutName.remove(sc);
                                    }
                                } else if (l.get(0).equals("MESSAGE")) {
                                    String nick = l.get(1);
                                    if (!clients.containsKey(nick)) {
                                        System.out.println("Client didn't send a nickname");
                                        closeClient(sc);
                                    } else if (!clients.get(nick).equals(sc)) {
                                        System.out.println("Client writes with another nickname");
                                        closeClient(sc);
                                    } else {
                                        StringBuilder sb = new StringBuilder();
                                        for (int i = 2; i < l.size(); ++i) {
                                            sb.append(l.get(i));
                                        }
                                        Iterator it = clients.entrySet().iterator();
                                        while (it.hasNext()) {
                                            Map.Entry pair = (Map.Entry)it.next();
                                            SocketChannel cur = (SocketChannel)pair.getValue();
                                            if (!sc.equals(cur)) {
                                                sendMessage(cur, MessageUtils.message(nick,
                                                    sb.toString()));
                                            }
                                        }
                                    }
                                } else if (l.get(0).equals("BYE")) {
                                    closeClient(sc);
                                } else if (l.get(0).equals("ERROR")) {
                                    System.out.println("error message");
                                    closeClient(sc);
                                } else {
                                    if (!withoutName.contains(sc)) {
                                        sendMessage(sc, MessageUtils.error("You sent "
                                                + "bad type of message"));
                                        sendMessage(sc, MessageUtils.bye());
                                    }
                                    closeClient(sc);
                                }
                            }
                        } catch (RuntimeException re) {
                            System.out.println("RuntimeException: " + re.getMessage());
                            closeClient(sc);
                        }
                    }
                }
            }
            keys.clear();
        } catch (Exception e) {
            IOUtils.printErrorAndExit(e.getMessage());
        }
    }

    public static void sendMessageAll(String message, String from) {
        Iterator it = clients.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            SocketChannel cur = (SocketChannel)pair.getValue();
            sendMessage(cur, MessageUtils.message(from, message));
        }
    }

    public static void sendMessage(SocketChannel sc, byte[] message) {
        try {
            if(sc.isConnected()) {
                ByteBuffer bf = ByteBuffer.wrap(message);
                sc.write(bf);
            }
        } catch (Exception e) {
            IOUtils.printErrorAndExit("Bad sending message!" + e.getMessage());
        }
    }

    public static boolean getMessage(SocketChannel sc) {
        try {
            ByteBuffer message = ByteBuffer.allocate(10000);
            int count = sc.read(message);
            if (count == 10000) {
                closeClient(sc);
                System.out.println("Big message");
                return true;
            }
            if (count == 0) {
                return true;
            }
            if (count == -1) {
                // проверка на случай экстренного выхода клиента
                closeClient(sc);
                return true;
            }
            byte[] bytes = new byte[0];
            for (Client c : clientMessages) {
                if (c.sc.equals(sc)) {
                    bytes = c.bytes;
                    clientMessages.remove(c);
                    break;
                }
            }
            byte[] mess = new byte[bytes.length + count];

            for (int i = 0; i < bytes.length; ++i) {
                mess[i] = bytes[i];
            }
            for (int i = bytes.length; i < bytes.length + count; ++i) {
                mess[i] = message.get(i - bytes.length);
            }
            clientMessages.add(new Client(sc, mess));
        } catch (Exception e) {
            IOUtils.printErrorAndExit("Bad geting message!" + e.getMessage());
        }
        return false;
    }

    public static String findNick(SocketChannel sc) {
        Iterator it = clients.entrySet().iterator();
        String nick = "";
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            SocketChannel cur = (SocketChannel)pair.getValue();
            if (cur.equals(sc)) {
                nick = (String)pair.getKey();
                break;
            }
        }
        return nick;
    }

    public static void closeClient(SocketChannel sc) {
        String nick = findNick( sc);
        sendMessage(sc, MessageUtils.bye());
        IOUtils.closeOrExit(sc);
        if (!nick.equals("")) {
            clients.remove(nick);
            sendMessageAll(nick + " is offline", "server");
            System.out.println(nick + " is offline");
        }
        withoutName.remove(sc);
        for (Client c : clientMessages) {
            if (c.sc.equals(sc)) {
                clientMessages.remove(c);
                break;
            }
        }
        IOUtils.closeOrExit(sc);
    }
}
