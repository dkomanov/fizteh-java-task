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
    private BufferedReader buf;
    // хранит ip-address сервера + номер порта и номер ячейки в массивах selectors
    // и channels, в которых хранятся соответствующий Selector и SocketChannel
    private Map<String, Integer> servers = new TreeMap<String, Integer>();
    // ip-address + порт текущего сервера
    private String curServer = "";
    // номер текущего сервера
    private int curServerNumber = -1;
    // подключен или нет клиент к какому-нибудь серверу в данный момент
    private boolean connected = false;
    // список SocketChannel каждого сервера
    private List<SocketChannel>  channels = new ArrayList<SocketChannel>();
    // список Selector каждого сервера
    private List<Selector> selectors = new ArrayList<Selector>();
    private List<byte[]> messages = new ArrayList<byte[]>();
    // ник клиента
    private String nick = "";

    public Client(String nick) {
        this.nick = nick;
        this.buf = new BufferedReader(new InputStreamReader(System.in));
    }

    public boolean isBufReady() {
        boolean ready = false;
        try {
            ready = buf.ready();
        } catch (Exception e) {
            IOUtils.printErrorAndExit("Inner error: " + e.getMessage());
        }
        return ready;
    }

    public boolean isConnected() {
        return connected;
    }

    public int getSelectedCount() {
        int num = 0;
        try {
            num = selectors.get(curServerNumber).selectNow();
        } catch (Exception e) {
            IOUtils.printErrorAndExit("Inner error: " + e.getMessage());
        }
        return num;
    }

    private void closeSelector(Selector selector) {
        try {
            if (selector != null) {
                selector.close();
            }
        } catch (Exception e) {
            IOUtils.printError("Bad closing: " + e.getMessage());
        }
    }

    public boolean connect(String host, String portNumber) {
        // при вызове команды сonnect клиент переходит в чат-комнату нового сервера
        try {
            connected = false;
            String cur = host + ":" + portNumber;
            int port = Integer.parseInt(portNumber);
            // создаем новый канал для нового сервера
            channels.add(SocketChannel.open());
            // создаем новый selector для нового сервера
            selectors.add(Selector.open());
            messages.add(new byte[0]);
            channels.get(channels.size() - 1).connect(new InetSocketAddress(host, port));
            // делаем канал неблокирующим для использования Selector
            channels.get(channels.size() - 1).configureBlocking(false);
            channels.get(channels.size() - 1).register(selectors.get(selectors.size() - 1),
                SelectionKey.OP_READ);
            servers.put(cur, channels.size() - 1);
            curServer = cur;
            connected = true;
            curServerNumber = selectors.size() - 1;
            sendMessage(channels.get(curServerNumber), MessageUtils.hello(nick));
        } catch (Exception e) {
            IOUtils.printError("Bad connecting: " + e.getMessage());
            return false;
        }
        return true;
    }

    public void disconnect() {
        sendMessage(channels.get(curServerNumber), MessageUtils.bye());
        IOUtils.closeOrExit(channels.get(curServerNumber));
        closeSelector(selectors.get(curServerNumber));
        servers.remove(curServer);
        connected = false;
        curServerNumber = -1;
        System.out.println("You are disconnected from " + curServer);
    }

    public void use(String server) {
        if (servers.containsKey(server)) {
            connected = true;
            curServer = server;
            curServerNumber = servers.get(curServer);
        } else {
            IOUtils.printError(server + ": there is no such server");
        }
    }

    public void messageSender() {
        try {
            String str = buf.readLine();
            if (isConnected()) {
                sendMessage(channels.get(curServerNumber),
                MessageUtils.message(nick, str));
            } else {
                System.out.println("You are not connected. You can't "  +
                        "send messages.");
            }
        } catch (Exception e) {
            IOUtils.printError("handlerConsole: " + e.getMessage());
        }
    }

    public void handlerConsole() {
        try {
            String str = buf.readLine();
            StringTokenizer st = new StringTokenizer(str, " \t");
            if (st.hasMoreTokens()) {
                String cmd = st.nextToken();
                if (cmd.equals("/connect")) {
                    if (st.hasMoreTokens()) {
                        String cur = st.nextToken();
                        if (servers.containsKey(cur)) {
                            System.out.println("You are already connected to this server");
                            return;
                        }
                        int pos = cur.indexOf(":");
                        if (pos != -1) {
                            String host = cur.substring(0, pos);
                            String portNumber = cur.substring(pos + 1, cur.length());
                            connect(host, portNumber);
                        } else {
                            IOUtils.printError("Usage: /connect host:port");
                        }
                    } else {
                        IOUtils.printError("Usage: /connect host port");
                    }
                } else if (cmd.equals("/disconnect")) {
                    if (isConnected()) {
                        disconnect();
                    } else {
                        System.out.println("You are not connected");
                    }
                } else if (cmd.equals("/whereami")) {
                    if (isConnected()) {
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
                        use(server);
                    } else {
                        IOUtils.printError("Usage: /use hostName");
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
                    if (isConnected()) {
                        sendMessage(channels.get(curServerNumber),
                                MessageUtils.message(nick, str));
                    } else {
                        System.out.println("You are not connected. You can't "  +
                                "send messages.");
                    }
                }
            }
        } catch (Exception e) {
            IOUtils.printError("handlerConsole: " + e.getMessage());
        }
    }

    public boolean handlerServer()  {
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
                                    return false;
                                } else {
                                    disconnect();
                                    return false;
                                }
                            }
                        } catch (Exception e) {
                            if (e.getMessage().equals("BYE from server")) {
                                System.out.println(e.getMessage());
                            } else {
                                System.out.println("Error during geting message: " + e.getMessage());
                            }
                            disconnect();
                            return false;
                        }
                    } else {
                        if (!isConnected()) {
                            return false;
                        }
                    }
                }
            }
            keys.clear();
        } catch (Exception e) {
            IOUtils.printError("Smth bad occured during getting message from"
                    + " server: " + e.getMessage());
        }
        return true;
    }

    public void sendMessage(SocketChannel sc, byte[] message) {
        try {
            if(sc != null) {
                ByteBuffer bf = ByteBuffer.wrap(message);
                sc.write(bf);
            } else {
                IOUtils.printError("Bad SocketChannel");
            }
        } catch (Exception e) {
            IOUtils.printError("Bad sending message!" + e.getMessage());
        }
    }

    public boolean getMessage(SocketChannel sc) {
        try {
            ByteBuffer message = ByteBuffer.allocate(10000);
            int count = sc.read(message);
            if (count == 10000) {
                disconnect();
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
            IOUtils.printError("Bad geting message!" + e.getMessage());
        }
        return false;
    }
}
