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
            ServerSocketChannel ssc = ServerSocketChannel.open();
            // для принятия новых клиетов
            ssc.configureBlocking(false);
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
            System.out.println("Something bad happened: " + e.getMessage());
            System.exit(1);
        }
    }

    public static void listen(WrapperPrimitive<Integer> port, Selector selector,
            ServerSocketChannel ssc, Map<String, SocketChannel> clients, StringTokenizer st) {
        try {
            if (port.t == -1) { // сервер может слушать в любой момент
                // времени только один порт
                if (st.hasMoreTokens()) {
                    String portNumber = st.nextToken();
                    port.t = Integer.parseInt(portNumber);
                    InetSocketAddress isa = new InetSocketAddress("localhost",
                            port.t);
                    ssc.socket().bind(isa);
                    System.out.println("Server at: " + isa.getAddress());
                    ssc.register(selector, SelectionKey.OP_ACCEPT);
                    System.out.println("Listening on port: " + port.t);
                } else {
                    System.err.println("Usage: /listen portNumber");
                    System.exit(1);
                }
            } else {
                System.err.println("Only one port is available!");
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void stop(ServerSocketChannel ssc, Map<String, SocketChannel> clients,
            WrapperPrimitive<Integer> port) {
        try {
            if (port.t != -1) {
                port.t = -1;
                try {
                    if (ssc != null) {
                        ssc.socket().close();
                    }
                } catch (Exception e) {
                    System.err.println("Could not close the current channel: " + e.getMessage());
                    System.exit(1);
                }
                Iterator iter = clients.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry pair = (Map.Entry)iter.next();
                    SocketChannel cur = (SocketChannel)pair.getValue();
                    if (cur != null && cur.isConnected()) {
                        sendMessage(cur, MessageUtils.bye(), clients);
                        try {
                            if (cur != null) {
                                cur.close();
                            }
                        } catch (Exception e) {
                            System.err.println("Could not close the current channel: "
                                    + e.getMessage());
                            System.exit(1);
                        }
                    }
                }
            } else {
                System.err.println("Nothing to stop");
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void kill(Map<String, SocketChannel> clients, StringTokenizer st) {
        try {
            if (st.hasMoreTokens()) {
                String name = st.nextToken();
                if (!clients.containsKey(name)) {
                    // проверяем есть ли такой клиент вообще
                    System.err.println(name + ": there is no such client");
                    System.exit(1);
                } else {
                    SocketChannel clientToClose = clients.get(name);
                    clients.remove(name);
                    try {
                        if (clientToClose != null) {
                            clientToClose.close();
                        }
                    } catch (Exception e) {
                        System.err.println("Bad closing: " + e.getMessage());
                        System.exit(1);
                    }
                    Iterator it = clients.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        SocketChannel cur = (SocketChannel)pair.getValue();
                        String ms = name + " is offline";
                        sendMessage(cur, MessageUtils.message("server", ms), clients);
                    }
                    System.out.println(name + " is offline");
                }
            } else {
                System.err.println("Usage: /send clientName");
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void handlerConsole(BufferedReader buf, WrapperPrimitive<Integer> port,
            Selector selector, ServerSocketChannel ssc,
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
                            System.err.println(name + ": there is no such client");
                            System.exit(1);
                        }
                    } else {
                        System.err.println("Usage: /send clientName");
                        System.exit(1);
                    }
                } else if (cmd.equals("/sendall")) {
                    StringBuilder sb = new StringBuilder();
                    while (st.hasMoreTokens()) {
                        sb.append(st.nextToken());
                        sb.append(" ");
                    }
                    Iterator iter = clients.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry pair = (Map.Entry)iter.next();
                        sendMessage((SocketChannel)pair.getValue(),
                                MessageUtils.message("server", sb.toString()), clients);
                    }
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
                        System.out.println("Bad closing selector: " + e.getMessage());
                        System.exit(1);
                    }
                    clients.clear();
                    System.exit(0);
                } else {
                    System.err.println(cmd + ": bad command");
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void handlerClients(Selector selector, 
            Map<String, SocketChannel> clients, List<SocketChannel> withoutName,
            ServerSocketChannel ssc) {
        try {
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator iter = keys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = (SelectionKey)iter.next();
                if ((key.readyOps() & SelectionKey.OP_ACCEPT) ==
                        SelectionKey.OP_ACCEPT) {
                    // получили новое соединение
                    SocketChannel sc = ssc.accept();
                    if (sc == null) {
                        System.err.println("Bad accepting");
                        System.exit(1);
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
                            try {
                                if (sc != null) {
                                    sc.close();
                                }
                            } catch (Exception e) {
                                System.err.println("Bad closing: " + e.getMessage());
                                System.exit(1);
                            }
                        } else {
                            // если ник уникален то добавляем нового клиента
                            System.out.println(nick + " is online");
                            StringBuilder sb = new StringBuilder("Online Clients:");
                            Iterator it = clients.entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry pair = (Map.Entry)it.next();
                                sb.append("\n");
                                sb.append((String)pair.getKey());
                                SocketChannel cur = (SocketChannel)pair.getValue();
                                String ms = nick + " is online";
                                sendMessage(cur, MessageUtils.message("server", ms), clients);
                                // отправляем пользователям сообщение о
                                // появлении нового клиента
                            }
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
                            if (!((String)pair.getKey()).equals(nick)) {
                                sendMessage((SocketChannel)pair.getValue(),
                                        MessageUtils.message(nick, sb.toString()), clients);
                            }
                        }
                    } else if (message[0] == 3) {
                        // bye от клиента
                        closeClient(clients, sc);
                    } else if (message[0] == 127) {
                        // пришла какая-то ошибка
                        Iterator it = clients.entrySet().iterator();
                        String nick = "";
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry)it.next();
                            if (!(((SocketChannel)pair.getValue()).equals(sc))) {
                                nick = (String)pair.getKey();
                                break;
                            }
                        }
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
            System.err.println(e.getMessage());
            System.exit(1);
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
            System.err.println("Bad sending message!" + e.getMessage());
            System.exit(1);
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
            System.err.println("Bad geting message!" + e.getMessage());
            System.exit(1);
        }
    }

    public static void closeClient(Map<String, SocketChannel> clients, SocketChannel sc) {
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
        clients.remove(nick);
        it = clients.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            SocketChannel cur = (SocketChannel)pair.getValue();
            String ms = nick + " is offline";
            sendMessage(cur, MessageUtils.message("server", ms), clients);
        }
        System.out.println(nick + " is offline");
        try {
            if (sc != null) {
                sc.close();
            }
        } catch (Exception e) {
            System.err.println("Bad closing: " + e.getMessage());
            System.exit(1);
        }
    }
}
