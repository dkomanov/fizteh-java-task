package ru.fizteh.fivt.students.tolyapro.chat.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import ru.fizteh.fivt.students.tolyapro.chat.MessageUtils;
import ru.fizteh.fivt.students.tolyapro.chat.TextAreaWriter;

/**
 * @author tolyapro
 * 
 * @08.12.2012
 */
public class ChatClient {
    static String name;
    static ArrayList<ServerConnection> servers;
    static ArrayList<String> serverNames;
    static ArrayList<OutputStream> streams;
    static ArrayList<Integer> toDelete;
    static TextAreaWriter areaWriter;
    static TextAreaWriter errorWriter;

    public ChatClient(String name, TextAreaWriter areaWriter,
            TextAreaWriter errorWriter) {
        this.name = name;
        this.areaWriter = areaWriter;
        this.errorWriter = errorWriter;
        servers = new ArrayList<ServerConnection>();
        serverNames = new ArrayList<String>();
        streams = new ArrayList<OutputStream>();
        toDelete = new ArrayList<Integer>();
    }

    synchronized void disconnectFromActive() {
        for (int i = 0; i < servers.size(); ++i) {
            if (servers.get(i).isActive()) {
                // servers.remove(i);
                // serverNames.remove(i);
                // streams.remove(i);
                servers.get(i).disable();
                // disableAll();
                return;
            }
        }
    }

    void disableAll() {
        for (ServerConnection s : servers) {
            s.disable();
        }
    }

    synchronized void deleteServers() {
        boolean flag = true;
        while (flag) {
            int prev = servers.size();
            for (int i = 0; i < servers.size(); ++i) {
                if (servers.get(i).toBeDeleted()) {
                    servers.remove(i);
                    serverNames.remove(i);
                    streams.remove(i);
                }
            }
            if (prev == servers.size()) {
                flag = false;
            }
        }
    }

    void connect(String host, String portString) throws Exception {
        int port = Integer.parseInt(portString);
        Socket server = null;
        server = new Socket(host, port);
        OutputStream out = server.getOutputStream();
        disableAll();
        ServerConnection sc = new ServerConnection(server, toDelete,
                servers.size(), areaWriter, errorWriter);
        servers.add(sc);
        serverNames.add(host + ":" + portString);
        streams.add(out);
        Thread t = new Thread(sc);
        t.start();
        sendMessageFromConsole(new String(MessageUtils.hello(name)));
    }

    String whereAmI() {
        deleteServers();
        for (int i = 0; i < servers.size(); ++i) {
            if (servers.get(i).isActive()) {
                return serverNames.get(i);
            }
        }
        return "You are not connected to any server";
    }

    ArrayList<String> list() {
        deleteServers();
        return serverNames;
    }

    boolean use(String name, String port) {
        deleteServers();
        String thisName = name + ":" + port;
        for (int i = 0; i < serverNames.size(); ++i) {
            if (serverNames.get(i).equals(thisName)) {
                disableAll();
                servers.get(i).activate();
                return true;
            }
        }
        return false;
    }

    void exit() {

    }

    void sendMessageFromConsole(String message) throws IOException {
        for (int i = 0; i < servers.size(); ++i) {
            if (servers.get(i).isActive()) {
                // servers.get(i).history += message;
                streams.get(i).write(message.getBytes());
                return;
            }
        }
    }

    String getHistory(String name) {
        System.out.println("Wnat to get history from " + name);
        for (int i = 0; i < serverNames.size(); ++i) {
            System.out.println(serverNames.size());
            if (serverNames.get(i).equals(name)) {
                System.out.println("name" + name);
                System.out.println(servers.get(i).getHistory());
                return servers.get(i).getHistory();
            }
        }
        System.out.println("Not found");
        return "";
    }

    void updateHistory(String name, String message) {
        for (int i = 0; i < serverNames.size(); ++i) {
            if (serverNames.get(i).equals(name)) {
                servers.get(i).updateHistory(message);
            }
        }
    }
}
