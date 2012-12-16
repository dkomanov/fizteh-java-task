package ru.fizteh.fivt.students.almazNasibullin.chatGui;

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
import javax.swing.JTextArea;
import ru.fizteh.fivt.students.almazNasibullin.chatGui.UtilsGui;
import ru.fizteh.fivt.students.almazNasibullin.IOUtils;
import ru.fizteh.fivt.students.almazNasibullin.chat.MessageType;
import ru.fizteh.fivt.students.almazNasibullin.chat.client.MessageUtils;

/**
 * 15.12.12
 * @author almaz
 */

public class ChatGuiClient {
    private JTextArea serverArea;
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
    private final Object synchronizedObject;

    public ChatGuiClient(String nick, JTextArea serverArea) {
        this.nick = nick;
        this.serverArea = serverArea;
        this.synchronizedObject = new Object();
    }

    public void run() {
        try {
            for (;;) {
                synchronized(synchronizedObject) {
                    if (connected) {
                        int num = selectors.get(curServerNumber).selectNow();
                        if (num == 0) {
                            continue;
                        }
                        handlerServer();
                    }
                }
            }
        } catch (Exception e) {
            UtilsGui.showErrorMessageAndExit("main: " + e.getMessage());
        }
    }

    public void closeSelector(Selector selector) {
        try {
            if (selector != null) {
                selector.close();
            }
        } catch (Exception e) {
            UtilsGui.showErrorMessageAndExit("Bad closing: " + e.getMessage());
        }
    }

    public void connect(StringTokenizer st) {
        // при вызове команды сonnect клиент переходит в чат-комнату нового сервера
        try {
            if (st.hasMoreTokens()) {
                String cur = st.nextToken();
                int pos = cur.indexOf(":");
                if (pos != -1) {
                    String host = cur.substring(0, pos);
                    String portNumber = cur.substring(pos + 1, cur.length());
                    if (servers.containsKey(cur)) {
                        serverArea.append("You .are already connected to this server\n");
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
                                    (selectors.get(selectors.size() - 1),
                                    SelectionKey.OP_READ);
                            servers.put(cur, channels.size() - 1);
                            curServer = cur;
                            connected = true;
                            curServerNumber = selectors.size() - 1;
                            sendMessage(channels.get(curServerNumber),
                                    MessageUtils.hello(nick));
                        } catch (Exception e) {
                            UtilsGui.showErrorMessageAndExit(e.getMessage());
                        }
                    }
                } else {
                    UtilsGui.showErrorMessageAndExit("Usage: /connect host:port");
                }
            } else {
                UtilsGui.showErrorMessageAndExit("Usage: /connect host port");
            }
        } catch (Exception e) {
            UtilsGui.showErrorMessageAndExit("Bad connecting: " + e.getMessage());
        }
    }

    public void disconnect() {
        sendMessage(channels.get(curServerNumber), MessageUtils.bye());
        IOUtils.closeOrExit(channels.get(curServerNumber));
        closeSelector(selectors.get(curServerNumber));
        servers.remove(curServer);
        connected = false;
        curServerNumber = -1;
        serverArea.append("You are disconnected from " + curServer + "\n");
    }

    public void handlerConsole(String str) {
        synchronized(synchronizedObject) {
            try {
                StringTokenizer st = new StringTokenizer(str, " \n\t");
                if (st.hasMoreTokens()) {
                    String cmd = st.nextToken();
                    if (cmd.equals("/connect")) {
                        connect(st);
                    } else if (cmd.equals("/disconnect")) {
                        if (connected) {
                            disconnect();
                        } else {
                            serverArea.append("You are not connected\n");
                        }
                    } else if (cmd.equals("/whereami")) {
                        if (connected) {
                            serverArea.append(curServer + "\n");
                        } else {
                            serverArea.append("You are not connected\n");
                        }
                    } else if (cmd.equals("/list")) {
                        Iterator it = servers.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry)it.next();
                            serverArea.append((String)pair.getKey() + "\n");
                        }
                    } else if (cmd.equals("/use")) {
                        if (st.hasMoreTokens()) {
                            String server = st.nextToken();
                            if (servers.containsKey(server)) {
                                connected = true;
                                curServer = server;
                                curServerNumber = servers.get(curServer);
                            } else {
                                UtilsGui.showErrorMessageAndExit(server
                                        + ": there is no such server");
                            }
                        } else {
                            UtilsGui.showErrorMessageAndExit("Usage: /use hostName");
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
                            serverArea.append("You are not connected. You can't "
                                    + "send messages.\n");
                        }
                    }
                }
            } catch (Exception e) {
                UtilsGui.showErrorMessageAndExit("handlerConsole: " + e.getMessage());
            }
        }
    }

    public void handlerServer()  {
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
                                    serverArea.append(sb.toString() + "\n");
                                } else if (l.get(0).equals("ERROR")) {
                                    serverArea.append("Error message is got\n");
                                    disconnect();
                                } else {
                                    disconnect();
                                }
                            }
                        } catch (RuntimeException e) {
                            if (e.getMessage().equals("BYE from server")) {
                                serverArea.append(e.getMessage() + "\n");
                            } else {
                                serverArea.append("Error during geting message: "
                                        + e.getMessage() + "\n");
                            }
                            disconnect();
                        }
                    }
                }
            }
            keys.clear();
        } catch (Exception e) {
            UtilsGui.showErrorMessageAndExit("Smth bad occured during getting message from"
                    + " server: " + e.getMessage());
        }
    }

    public void sendMessage(SocketChannel sc, byte[] message) {
        try {
            if(sc != null) {
                ByteBuffer bf = ByteBuffer.wrap(message);
                sc.write(bf);
            } else {
                UtilsGui.showErrorMessageAndExit("Bad SocketChannel");
            }
        } catch (Exception e) {
            UtilsGui.showErrorMessageAndExit("Bad sending message!" + e.getMessage());
        }
    }

    public boolean getMessage(SocketChannel sc) {
        try {
            ByteBuffer message = ByteBuffer.allocate(10000);
            int count = sc.read(message);
            if (count == 10000) {
                disconnect();
                serverArea.append("Big message is found\n");
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
            UtilsGui.showErrorMessageAndExit("Bad geting message!" + e.getMessage());
        }
        return false;
    }
}
