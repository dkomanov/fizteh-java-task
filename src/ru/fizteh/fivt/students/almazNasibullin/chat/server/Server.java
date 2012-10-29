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

/**
 * 17.10.12
 * @author almaz
 */

public class Server {

    public static void main(String[] args) {
        try {
            WrapperPrimitive<Integer> port = new WrapperPrimitive<Integer>(-1);
            Selector selector = Selector.open();
            BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
            Map<String, SocketChannel> clients = new TreeMap<String, SocketChannel>();
            // хранит ник и SocketChannel клиента
            List<SocketChannel> withoutName = new ArrayList<SocketChannel>();
            /* хранит SocketChannel клиентов, у которых не утвержден ник,
             * т.е. возможно, что ник клиента совпадает с уже имеющимся
             */
            List<ServerSocketChannel> ssc = new ArrayList<ServerSocketChannel>();
            // для того, чтобы принимать новых клиетов
            ssc.add(ServerSocketChannel.open());
            ssc.get(0).configureBlocking(false);
            // указываем неблокирующий режим

            for (;;) {
                if (buf.ready()) {
                    handlerConsole(buf, port, selector, ssc, clients);
                }
                int num = selector.selectNow();
                if (num == 0) {
                    continue;
                }
                handlerClients(selector, clients, withoutName, ssc);
            }
        } catch (Exception e) {
            printErrorAndExit("Something bad happened: " + e.getMessage());
        }
    }

    public static void printErrorAndExit(String error) {
        System.err.println(error);
        System.exit(1);
    }

    public static void closeChannel(SocketChannel sc) {
        try {
            if (sc != null) {
                sc.close();
            }
        } catch (Exception e) {
            printErrorAndExit("Bad closing: " + e.getMessage());
        }
    }

    public static void listen(WrapperPrimitive<Integer> port, Selector selector,
            List<ServerSocketChannel> ssc, Map<String, SocketChannel> clients, StringTokenizer st) {
        try {
            if (port.t == -1) { // сервер может слушать в любой момент
                // времени только один порт
                if (st.hasMoreTokens()) {
                    String portNumber = st.nextToken();
                    port.t = Integer.parseInt(portNumber);
                    InetSocketAddress isa = new InetSocketAddress("localhost",
                            port.t);
                    if (!ssc.get(0).isOpen()) {
                        ssc.remove(0);
                        ssc.add(ServerSocketChannel.open());
                        ssc.get(0).configureBlocking(false);
                    }
                    ssc.get(0).socket().bind(isa);
                    System.out.println("Server at: " + isa.getAddress());
                    ssc.get(0).register(selector, SelectionKey.OP_ACCEPT);
                    System.out.println("Listening on port: " + port.t);
                } else {
                    printErrorAndExit("Usage: /listen portNumber");
                }
            } else {
                printErrorAndExit("Only one port is available!");
            }
        } catch (Exception e) {
            printErrorAndExit(e.getMessage());
        }
    }

    public static void stop(List<ServerSocketChannel> ssc, Map<String, SocketChannel> clients,
            WrapperPrimitive<Integer> port) {
        try {
            if (port.t != -1) {
                port.t = -1;
                try {
                    if (ssc.get(0) != null) {
                        ssc.get(0).close();
                    }
                } catch (Exception e) {
                    printErrorAndExit("Could not close the current channel: " + e.getMessage());
                }
                Iterator iter = clients.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry pair = (Map.Entry)iter.next();
                    SocketChannel cur = (SocketChannel)pair.getValue();
                    if (cur != null && cur.isConnected()) {
                        sendMessage(cur, MessageUtils.bye(), clients);
                        closeChannel(cur);
                    }
                }
                clients.clear();
            } else {
                printErrorAndExit("Nothing to stop");
            }
        } catch (Exception e) {
            printErrorAndExit(e.getMessage());
        }
    }

    public static void kill(Map<String, SocketChannel> clients, StringTokenizer st) {
        try {
            if (st.hasMoreTokens()) {
                String name = st.nextToken();
                if (!clients.containsKey(name)) {
                    // проверяем есть ли такой клиент вообще
                    printErrorAndExit(name + ": there is no such client");
                } else {
                    SocketChannel clientToClose = clients.get(name);
                    clients.remove(name);
                    closeChannel(clientToClose);
                    sendMessageAll(clients, name + " is offline", "server");
                    System.out.println(name + " is offline");
                }
            } else {
                printErrorAndExit("Usage: /send clientName");
            }
        } catch (Exception e) {
            printErrorAndExit(e.getMessage());
        }
    }

    public static void handlerConsole(BufferedReader buf, WrapperPrimitive<Integer> port,
            Selector selector, List<ServerSocketChannel> ssc,
            Map<String, SocketChannel> clients) {
        try {
            String str = buf.readLine();
            StringTokenizer st = new StringTokenizer(str, " \t");
            if (st.hasMoreTokens()) {
                String cmd = st.nextToken();
                if (cmd.equals("/listen")) {
                    listen(port, selector, ssc, clients, st);
                } else if (cmd.equals("/stop")) {
                    stop(ssc, clients, port);
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
                                    MessageUtils.message("server", sb.toString()), clients);
                        } else {
                            printErrorAndExit(name + ": there is no such client");
                        }
                    } else {
                        printErrorAndExit("Usage: /send clientName");
                    }
                } else if (cmd.equals("/sendall")) {
                    StringBuilder sb = new StringBuilder();
                    while (st.hasMoreTokens()) {
                        sb.append(st.nextToken());
                        sb.append(" ");
                    }
                    sendMessageAll(clients, sb.toString(), "server");
                } else if (cmd.equals("/kill")) {
                    kill(clients, st);
                } else if (cmd.equals("/exit")) {
                    if (port.t != -1) {
                        stop(ssc, clients, port);
                    }
                    try {
                        if (selector != null) {
                            selector.close();
                        }
                    } catch (Exception e) {
                        printErrorAndExit("Bad closing selector: " + e.getMessage());
                    }
                    clients.clear();
                    System.exit(0);
                } else {
                    printErrorAndExit(cmd + ": bad command");
                }
            }
        } catch (Exception e) {
            printErrorAndExit(e.getMessage());
        }
    }

    public static void handlerClients(Selector selector, 
            Map<String, SocketChannel> clients, List<SocketChannel> withoutName,
            List<ServerSocketChannel> ssc) {
        try {
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator iter = keys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = (SelectionKey)iter.next();
                if ((key.readyOps() & SelectionKey.OP_ACCEPT) ==
                        SelectionKey.OP_ACCEPT) {
                    // получили новое соединение
                    SocketChannel sc = ssc.get(0).accept();
                    if (sc == null) {
                        printErrorAndExit("Bad accepting");
                    }
                    withoutName.add(sc);
                    sc.configureBlocking(false);
                    sc.register(selector, SelectionKey.OP_READ);
                } else if ((key.readyOps() & SelectionKey.OP_READ) ==
                        SelectionKey.OP_READ) {
                    // в какой-то SocketChannel пришло сообщение
                    SocketChannel sc = (SocketChannel)key.channel();
                    ByteBuffer mes = ByteBuffer.allocate(512);
                    getMessage(sc, mes, clients);
                    byte[] message = mes.array();
                    if (message[0] == 1) {
                        // сообщение с ником
                        String nick = MessageUtils.getNickname(message);
                        if (clients.containsKey(nick)) {
                            // проверяем на уникальность ника
                            sendMessage(sc, MessageUtils.error("server: This nick"
                                    + " already exists! Try to connect with "
                                    + "another nick!"), clients);
                            sendMessage(sc, MessageUtils.bye(), clients);
                            withoutName.remove(sc);
                            closeChannel(sc);
                        } else {
                            // если ник уникален то добавляем нового клиента
                            System.out.println(nick + " is online");
                            StringBuilder sb = new StringBuilder("Online Clients:");
                            Iterator it = clients.entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry pair = (Map.Entry)it.next();
                                sb.append("\n");
                                sb.append((String)pair.getKey());
                            }
                            sendMessageAll(clients, nick + " is offline", "server");
                            String msg = "";
                            if (clients.isEmpty()) {
                                msg = "You are first client!";
                            } else {
                                msg = sb.toString();
                            }
                            sendMessage(sc, MessageUtils.message("server", msg), clients);
                            // сообщение новому клиенту с никами уже имеющихся
                            clients.put(nick, sc);
                            withoutName.remove(sc);
                        }
                    } else if (message[0] == 2) { 
                        // простое сообщение от клиента, которое нужно отправить
                        List<String> l = MessageUtils.dispatch(message);
                        String nick = l.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < l.size(); ++i) {
                            sb.append(l.get(i));
                        }
                        Iterator it = clients.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry)it.next();
                            SocketChannel cur = (SocketChannel)pair.getValue();
                            if (!sc.equals(cur)) {
                                sendMessage(cur, MessageUtils.message(nick,
                                        sb.toString()), clients);
                            }
                        }
                    } else if (message[0] == 3) {
                        // bye от клиента
                        closeClient(clients, sc);
                    } else if (message[0] == 127) {
                        // пришла какая-то ошибка
                        String nick = findNick(clients, sc);
                        List<String> l = MessageUtils.dispatch(message);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < l.size(); ++i) {
                            sb.append(l.get(i));
                        }
                        System.out.println("Error from " + nick + ": " + sb.toString());
                    }
                }
            }
            keys.clear();
        } catch (Exception e) {
            printErrorAndExit(e.getMessage());
        }
    }

    public static void sendMessageAll(Map<String, SocketChannel> clients, String message,
            String from) {
        Iterator it = clients.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            SocketChannel cur = (SocketChannel)pair.getValue();
            sendMessage(cur, MessageUtils.message(from, message), clients);
        }
    }

    public static void sendMessage(SocketChannel sc, byte[] message,
            Map<String, SocketChannel> clients) {
        try {
            if(sc.isConnected()) {
                ByteBuffer bf = ByteBuffer.wrap(message);
                sc.write(bf);
            }
        } catch (Exception e) {
            printErrorAndExit("Bad sending message!" + e.getMessage());
        }
    }

    public static void getMessage(SocketChannel sc, ByteBuffer message,
            Map<String, SocketChannel> clients) {
        try {
            int count = sc.read(message);
            if (count == -1) {
                // проверка на случай экстренного выхода клиента
                closeClient(clients, sc);
            }
        } catch (Exception e) {
            printErrorAndExit("Bad geting message!" + e.getMessage());
        }
    }

    public static String findNick(Map<String, SocketChannel> clients, SocketChannel sc) {
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

    public static void closeClient(Map<String, SocketChannel> clients, SocketChannel sc) {
        String nick = findNick(clients, sc);
        closeChannel(sc);
        clients.remove(nick);
        sendMessageAll(clients, nick + " is offline", "server");
        System.out.println(nick + " is offline");
    }
}
