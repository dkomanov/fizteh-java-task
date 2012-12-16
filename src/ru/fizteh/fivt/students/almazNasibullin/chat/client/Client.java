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
import ru.fizteh.fivt.students.almazNasibullin.chat.client.MessageUtils;

/**
 * 21.10.12
 * @author almaz
 */

public class Client {
    private static BufferedReader buf;
    // хранит ip-address сервера + номер порта и номер ячейки в массивах selectors
    // и channels, в которых хранятся соответствующий Selector и SocketChannel
    private static Map<String, Integer> servers = new TreeMap<String, Integer>();
    // ip-address + порт текущего сервера
    private static String curServer = "";
    // номер текущего сервера
    private static int curServerNumber = -1;
    // подключен или нет клиент к какому-нибудь серверу в данный момент
    private static boolean connected = false;
    // список SocketChannel каждого сервера
    private static List<SocketChannel>  channels = new ArrayList<SocketChannel>();
    // список Selector каждого сервера
    private static List<Selector> selectors = new ArrayList<Selector>();
    private static List<byte[]> messages = new ArrayList<byte[]>();
    // ник клиента
    private static String nick = "";

    public static void main(String[] args) {
        try {
            buf = new BufferedReader(new InputStreamReader(System.in));
            if (args.length != 1) {
                IOUtils.printErrorAndExit("Put your nick");
            } else {
                nick = args[0];
            }

            for (;;) {
                if (buf.ready()) {
                    handlerConsole();
                }
                if (connected) {
                    int num = selectors.get(curServerNumber).selectNow();
                    if (num == 0) {
                        continue;
                    }
                    handlerServer();
                }
            }
        } catch (Exception e) {
            IOUtils.printErrorAndExit("main: " + e.getMessage());
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

    public static void connect(StringTokenizer st) {
        // при вызове команды сonnect клиент переходит в чат-комнату нового сервера
        try {
            if (st.hasMoreTokens()) {
                String cur = st.nextToken();
                int pos = cur.indexOf(":");
                if (pos != -1) {
                    String host = cur.substring(0, pos);
                    String portNumber = cur.substring(pos + 1, cur.length());
                    if (servers.containsKey(cur)) {
                        System.out.println("You are already connected to this server");
                    } else {
                        int port = Integer.parseInt(portNumber);
                        try {
                            // создаем новый канал для нового сервера
                            channels.add(SocketChannel.open());
                            // создаем новый selector для нового сервера
                            selectors.add(Selector.open());
                            messages.add(new byte[0]);
                            channels.get(channels.size() - 1).
                                    connect(new InetSocketAddress(host, port));
                            // делаем канал неблокирующим для использования Selector
                            channels.get(channels.size() - 1).
                                    configureBlocking(false);
                            channels.get(channels.size() - 1).register
                                    (selectors.get(selectors.size() - 1), SelectionKey.OP_READ);
                            servers.put(cur, channels.size() - 1);
                            curServer = cur;
                            connected = true;
                            curServerNumber = selectors.size() - 1;
                            sendMessage(channels.get(curServerNumber),
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

    public static void disconnect() {
        sendMessage(channels.get(curServerNumber), MessageUtils.bye());
        IOUtils.closeOrExit(channels.get(curServerNumber));
        closeSelector(selectors.get(curServerNumber));
        servers.remove(curServer);
        connected = false;
        curServerNumber = -1;
        System.out.println("You are disconnected from " + curServer);
    }

    public static void handlerConsole() {
        try {
            String str = buf.readLine();
            StringTokenizer st = new StringTokenizer(str, " \t");
            if (st.hasMoreTokens()) {
                String cmd = st.nextToken();
                if (cmd.equals("/connect")) {
                    connect(st);
                } else if (cmd.equals("/disconnect")) {
                    if (connected) {
                        disconnect();
                    } else {
                        System.out.println("You are not connected");
                    }
                } else if (cmd.equals("/whereami")) {
                    if (connected) {
                        System.out.println(curServer);
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
                        String server = st.nextToken();
                        if (servers.containsKey(server)) {
                            connected = true;
                            curServer = server;
                            curServerNumber = servers.get(curServer);
                        } else {
                            IOUtils.printErrorAndExit(server + ": there is no such server");
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
                        IOUtils.closeOrExit(channels.get((Integer)pair.getValue()));
                        closeSelector(selectors.get((Integer)pair.getValue()));
                    }
                    servers.clear();
                    channels.clear();
                    selectors.clear();
                    messages.clear();
                    System.exit(0);
                } else {
                    // отправка сообщения в чат
                    if (connected) {
                        sendMessage(channels.get(curServerNumber),
                                MessageUtils.message(nick, str));
                    } else {
                        System.out.println("You are not connected. You can't "  +
                                "send messages.");
                    }
                }
            }
        } catch (Exception e) {
            IOUtils.printErrorAndExit("handlerConsole: " + e.getMessage());
        }
    }

    public static void handlerServer()  {
        try {
            Set<SelectionKey> keys = selectors.get(curServerNumber).selectedKeys();
            Iterator iter = keys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = (SelectionKey)iter.next();
                if ((key.readyOps() & SelectionKey.OP_READ) ==
                        SelectionKey.OP_READ) {
                    // в SocketChannel пришло новое сообщение
                    SocketChannel sc = (SocketChannel)key.channel();
                    boolean crash = getMessage(sc);
                    if (!crash) {
                        try {
                            List<List<String>> mess = MessageUtils.getMessages(messages,
                                    curServerNumber);
                            if (mess.isEmpty()) {
                                continue;
                            }
                            for (List<String> l : mess) {
                                if (l.get(0).equals("MESSAGE")) {
                                    StringBuilder sb = new StringBuilder(l.get(1) + ": ");
                                    for (int i = 2; i < l.size(); ++i) {
                                        sb.append(l.get(i));
                                    }
                                    System.out.println(sb.toString());
                                } else if (l.get(0).equals("ERROR")) {
                                    System.out.println("error message");
                                    disconnect();
                                } else {
                                    disconnect();
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Error during geting message: " + e.getMessage());
                            disconnect();
                        }
                    }
                }
            }
            keys.clear();
        } catch (Exception e) {
            IOUtils.printErrorAndExit("Smth bad occured during getting message from"
                    + " server: " + e.getMessage());
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

    public static boolean getMessage(SocketChannel sc) {
        try {
            ByteBuffer message = ByteBuffer.allocate(10000);
            int count = sc.read(message);
            if (count == 10000) {
                disconnect();
                System.out.println("Big message");
                return true;
            }
            if (count == 0) {
                return true;
            }
            if (count == -1) {
                // проверка на случай экстренного выхода сервера
                disconnect();
                return true;
            }

            byte[] bytes = messages.remove(curServerNumber);
            byte[] mess = new byte[bytes.length + count];

            for (int i = 0; i < bytes.length; ++i) {
                mess[i] = bytes[i];
            }
            for (int i = bytes.length; i < bytes.length + count; ++i) {
                mess[i] = message.get(i - bytes.length);
            }
            messages.add(curServerNumber, mess);
        } catch (Exception e) {
            IOUtils.printErrorAndExit("Bad geting message!" + e.getMessage());
        }
        return false;
    }
}
