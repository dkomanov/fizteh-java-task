package ru.fizteh.fivt.students.almazNasibullin.chat.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
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
import ru.fizteh.fivt.students.almazNasibullin.chat.MessageUtils;
import ru.fizteh.fivt.students.almazNasibullin.WrapperPrimitive;

/**
 * 21.10.12
 * @author almaz
 */

public class Client {

    public static void main(String[] args) {
        try {
            BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
            Map<String, Integer> servers = new TreeMap<String, Integer>();
            // хранит ip-address сервера и номер ячейки в массивах selectors
            // и channels, в которых хранятся соответствующий Selector и SocketChannel
            WrapperPrimitive<String> curServer = new WrapperPrimitive<String>("");
            // ip-address текущего сервера
            WrapperPrimitive<Integer> curServerNumber = new WrapperPrimitive<Integer>(-1);
            // номер текущего сервера
            WrapperPrimitive<Boolean> connected = new WrapperPrimitive<Boolean>(false);
            // подключен или нет клиент к какому-нибудь серверу в данный момент
            List<SocketChannel>  channels = new ArrayList<SocketChannel>();
            // список SocketChannel каждого сервера
            List<Selector> selectors = new ArrayList<Selector>();
            // список Selector каждого сервера
            String nick = ""; // ник клиента

            if (args.length == 0) {
                IOUtils.printErrorAndExit("Put your nick");
            } else {
                nick = args[0];
            }

            for (;;) {
                if (buf.ready()) {
                    handlerConsole(buf, servers, curServer,curServerNumber,
                            connected, nick, channels, selectors);
                }
                if (connected.t) {
                    int num = selectors.get(curServerNumber.t).selectNow();
                    if (num == 0) {
                        continue;
                    }
                    handlerServer(servers, curServer, curServerNumber,connected,
                            channels, selectors);
                }
            }
        } catch (Exception e) {
            IOUtils.printErrorAndExit(e.getMessage());
        }
    }

    public static void closeChannel(SocketChannel sc) {
        try {
            if (sc != null) {
                sc.close();
            }
        } catch (Exception e) {
            IOUtils.printErrorAndExit("Bad closing: " + e.getMessage());
        }
    }

    public static void closeSelector(Selector selector) {
        try {
            if (selector != null) {
                selector.close();
            }
        } catch (Exception e) {
            IOUtils.printErrorAndExit("Bad closing: " + e.getMessage());
        }
    }

    public static void connect(Map<String, Integer> servers, 
            WrapperPrimitive<String> curServer, WrapperPrimitive<Integer> curServerNumber,
            StringTokenizer st, WrapperPrimitive<Boolean> connected,
            String nick, List<SocketChannel> channels, List<Selector> selectors) {
        // при вызове команды сonnect клиент переходит в чат-комнату нового сервера
        try {
            if (st.hasMoreTokens()) {
                String cur = st.nextToken();
                int pos = cur.indexOf(":");
                if (pos != -1) {
                    String host = cur.substring(0, pos);
                    String portNumber = cur.substring(pos + 1, cur.length());
                    if (servers.containsKey(host)) {
                        System.out.println("You are already connected to this server");
                    } else {
                        int port = Integer.parseInt(portNumber);
                        try {
                            channels.add(SocketChannel.open());
                            // создаем новый канал для нового сервера
                            selectors.add(Selector.open());
                            // создаем новый selector для нового сервера
                            channels.get(channels.size() - 1).
                                    connect(new InetSocketAddress(host, port));
                            channels.get(channels.size() - 1).
                                    configureBlocking(false);
                            // делаем канал неблокирующим для использования Selector
                            channels.get(channels.size() - 1).register
                                    (selectors.get(selectors.size() - 1), SelectionKey.OP_READ);
                            servers.put(host, channels.size() - 1);
                            curServer.t = host;
                            connected.t = true;
                            curServerNumber.t = selectors.size() - 1;
                            sendMessage(channels.get(curServerNumber.t),
                                    MessageUtils.hello(nick));
                        } catch (Exception e) {
                            IOUtils.printErrorAndExit(e.getMessage());
                        }
                    }
                } else {
                    IOUtils.printErrorAndExit("Usage: /connect host:port");
                }
            } else {
                IOUtils.printErrorAndExit("Usage: /connect host port");
            }
        } catch (Exception e) {
            IOUtils.printErrorAndExit("Bad connecting: " + e.getMessage());
        }
    }

    public static void disconnect(Map<String, Integer> servers, 
            WrapperPrimitive<String> curServer, WrapperPrimitive<Integer> curServerNumber,
            WrapperPrimitive<Boolean> connected, List<SocketChannel> channels,
            List<Selector> selectors) {
        sendMessage(channels.get(curServerNumber.t), MessageUtils.bye());
        closeChannel(channels.get(curServerNumber.t));
        closeSelector(selectors.get(curServerNumber.t));
        servers.remove(curServer.t);
        connected.t = false;
        curServerNumber.t = -1;
        System.out.println("You are disconnected from " + curServer.t);
    }

    public static void handlerConsole(BufferedReader buf, Map<String,Integer> servers,
            WrapperPrimitive<String> curServer, WrapperPrimitive<Integer> curServerNumber,
            WrapperPrimitive<Boolean> connected, String nick,
            List<SocketChannel> channels, List<Selector> selectors) {
        try {
            String str = buf.readLine();
            StringTokenizer st = new StringTokenizer(str, " \t");
            if (st.hasMoreTokens()) {
                String cmd = st.nextToken();
                if (cmd.equals("/connect")) {
                    connect(servers, curServer, curServerNumber, st, connected,
                            nick, channels, selectors);
                } else if (cmd.equals("/disconnect")) {
                    if (connected.t) {
                        disconnect(servers, curServer, curServerNumber, connected,
                                channels, selectors);
                    } else {
                        System.out.println("You are not connected");
                    }
                } else if (cmd.equals("/whereami")) {
                    if (connected.t) {
                        System.out.println(curServer.t);
                    } else {
                        System.out.println("You are not connected");
                    }
                } else if (cmd.equals("/list")) {
                    Iterator it = servers.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        System.out.println((String)pair.getKey());
                    }
                } else if (cmd.equals("/use")) {
                    if (st.hasMoreTokens()) {
                        String host = st.nextToken();
                        if (servers.containsKey(host)) {
                            curServer.t = host;
                            curServerNumber.t = servers.get(curServer.t);
                        } else {
                            IOUtils.printErrorAndExit(host + ": there is no such server");
                        }
                    } else {
                        IOUtils.printErrorAndExit("Usage: /use hostName");
                    }
                } else if (cmd.equals("/exit")) {
                    Iterator it = servers.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        sendMessage(channels.get((Integer)pair.getValue()),
                                MessageUtils.bye());
                        closeChannel(channels.get((Integer)pair.getValue()));
                        closeSelector(selectors.get((Integer)pair.getValue()));
                    }
                    servers.clear();
                    channels.clear();
                    selectors.clear();
                    System.exit(0);
                } else {
                    // отправка сообщения в чат
                    if (connected.t) {
                        sendMessage(channels.get(curServerNumber.t),
                                MessageUtils.message(nick, str));
                    } else {
                        System.out.println("You are not connected. You can't "  +
                                "send messages.");
                    }
                }
            }
        } catch (Exception e) {
            IOUtils.printErrorAndExit(e.getMessage());
        }
    }

    public static void handlerServer(Map<String, Integer> servers,
            WrapperPrimitive<String> curServer, WrapperPrimitive<Integer> curServerNumber,
            WrapperPrimitive<Boolean> connected, List<SocketChannel> channels,
            List<Selector> selectors)  {
        try {
            Set<SelectionKey> keys = selectors.get(curServerNumber.t).selectedKeys();
            Iterator iter = keys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = (SelectionKey)iter.next();
                if ((key.readyOps() & SelectionKey.OP_READ) ==
                        SelectionKey.OP_READ) {
                    // в SocketChannel пришло новое сообщение
                    SocketChannel sc = (SocketChannel)key.channel();
                    ByteBuffer mes = ByteBuffer.allocate(512);
                    getMessage(sc, mes, curServer, curServerNumber, servers,
                            connected,  channels, selectors);
                    byte[] message = mes.array();
                    if (message[0] == 2) {
                        // обычное сообщение
                        List<String> l = MessageUtils.dispatch(message);
                        StringBuilder sb = new StringBuilder(l.get(0) + ": ");
                        for (int i = 1; i < l.size(); ++i) {
                            sb.append(l.get(i));
                        }
                        System.out.println(sb.toString());
                    } else if (message[0] == 3) {
                        // bye от сервера
                        disconnect(servers, curServer, curServerNumber, connected,
                                channels, selectors);
                    } else if (message[0] == 127) {
                        // ошибка от сервера
                        List<String> l = MessageUtils.dispatch(message);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < l.size(); ++i) {
                            sb.append(l.get(i));
                        }
                        System.out.println("Error: " + sb.toString());
                    }
                }
            }
            keys.clear();
        } catch (Exception e) {
            IOUtils.printErrorAndExit(e.getMessage());
        }
    }

    public static void sendMessage(SocketChannel sc, byte[] message) {
        try {
            if(sc != null) {
                ByteBuffer bf = ByteBuffer.wrap(message);
                sc.write(bf);
            } else {
                IOUtils.printErrorAndExit("Bad SocketChannel");
            }
        } catch (Exception e) {
            IOUtils.printErrorAndExit("Bad sending message!" + e.getMessage());
        }
    }

    public static void getMessage(SocketChannel sc, ByteBuffer message,
            WrapperPrimitive<String> curServer, WrapperPrimitive<Integer> curServerNumber,
            Map<String, Integer> servers, WrapperPrimitive<Boolean> connected,
            List<SocketChannel> channels, List<Selector> selectors) {
        try {
            int count = sc.read(message);
            if (count == -1) {
                // проверка на случай экстренного выхода сервера
                disconnect(servers, curServer, curServerNumber, connected,
                        channels, selectors);
            }
        } catch (Exception e) {
            IOUtils.printErrorAndExit("Bad geting message!" + e.getMessage());
        }
    }
}
