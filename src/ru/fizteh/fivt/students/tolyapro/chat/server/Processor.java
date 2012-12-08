package ru.fizteh.fivt.students.tolyapro.chat.server;

import java.awt.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.*;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import ru.fizteh.fivt.students.tolyapro.chat.MessageUtils;
import ru.fizteh.fivt.students.tolyapro.chat.client.Client;
import ru.fizteh.fivt.students.tolyapro.wordCounter.BufferCloser;

/**
 * @author tolyapro
 * 
 * @03.12.2012
 */
public class Processor implements Runnable {

    int port;
    ConcurrentHashMap<String, Socket> users;
    ConcurrentHashMap<Socket, InputStream> readers;
    ConcurrentHashMap<Socket, OutputStream> writers;
    Thread thisTread;

    public Processor(int port) {
        this.port = port;
        users = new ConcurrentHashMap<String, Socket>();
        readers = new ConcurrentHashMap<Socket, InputStream>();
        writers = new ConcurrentHashMap<Socket, OutputStream>();
        thisTread = null;
    }

    synchronized public void stop() {
        System.out.println("Bye fomr server");
        sendToAll(new String(MessageUtils.bye()), null);
        for (InputStream in : readers.values()) {
            BufferCloser.close(in);
        }

        for (OutputStream out : writers.values()) {
            BufferCloser.close(out);
        }

        for (Socket socket : users.values()) {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (Exception e) {

            }
        }
        thisTread.interrupt();
    }

    public void start() {
        thisTread = new Thread(this);
        thisTread.start();
    }

    public void run() {
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
        } catch (Exception e) {
            System.err.println("Could not listen on port: " + port);
            System.err.println(e);
            System.exit(1);
        }

        Socket client = null;
        while (!thisTread.isInterrupted()) {
            try {
                client = server.accept();
                System.out.println("accepted");
            } catch (Exception e) {
                System.err.println("Accept failed.");
                System.err.println(e);
                System.exit(1);
            }
            Thread t = new Thread(new ClientConn(client, users, readers,
                    writers, this));
            t.start();
        }
    }

    void sendToAll(String message, Socket self) {
        OutputStream selfWriter = null;
        if (self != null) {
            selfWriter = writers.get(self);
        }
        for (OutputStream pw : writers.values()) {
            System.out.println("Writing");
            if (!pw.equals(selfWriter)) {
                try {
                    pw.write(message.getBytes());
                } catch (Exception e) {
                    System.out.println("Bad stream");
                    // writers.values().remove(pw);
                }
            }
        }
    }

    public Set<String> getUsers() {
        return users.keySet();
    }

    synchronized public void kill(String name) {
        System.out.println("Trying to kill " + name);
        kill(users.get(name));
    }

    synchronized void kill(Socket client) {
        sendTo(new String(MessageUtils.error("You have been killed by server")),
                client);
        writers.remove(client);
        readers.remove(client);
        while (users.values().remove(client)) {
            System.out.println("Removing");
        }
        try {
            client.close();
        } catch (Exception e) {
        }
    }

    boolean sendTo(String message, String to) {
        Socket socket = users.get(to);
        if (socket != null) {
            sendTo(message, socket);
            return true;
        }
        return false;
    }

    void sendTo(String message, Socket to) {
        try {
            OutputStream selfWriter = writers.get(to);
            if (selfWriter == null) {

            } else {
                selfWriter.write(message.getBytes());
            }
        } catch (Exception e) {
            System.out.println("in sendto " + e.getMessage());
        }
    }
}

class ClientConn implements Runnable {
    private Socket client;
    private InputStream in = null;
    private OutputStream out = null;
    ConcurrentHashMap<String, Socket> users;
    ConcurrentHashMap<Socket, InputStream> readers;
    ConcurrentHashMap<Socket, OutputStream> writers;
    Processor processor;

    ClientConn(Socket client, ConcurrentHashMap<String, Socket> users,
            ConcurrentHashMap<Socket, InputStream> readers,
            ConcurrentHashMap<Socket, OutputStream> writers, Processor processor) {
        this.client = client;
        this.users = users;
        this.readers = readers;
        this.writers = writers;
        this.processor = processor;
        try {
            in = client.getInputStream();
            // out = new PrintWriter(client.getOutputStream(), true);
            out = client.getOutputStream();
        } catch (Exception e) {
            System.err.println(e);
            return;
        }
    }

    public void run() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        byte[] bytes = byteBuffer.array();
        try {
            while (in.read(bytes) != -1) {

                processMessage(new String(bytes));
            }
            System.out.println("End of server");
        } catch (IOException e1) {
        }

    }

    void processMessage(String message) {
        byte[] msg = message.getBytes();
        // System.out.print("in ");
        // for (int i = 0; i < msg.length; ++i) {
        // System.out.print(msg[i] + " ");
        // }
        // System.out.println("");
        if (msg.length == 0) {
            return;
        }
        byte type = msg[0];
        if (type == 1) {
            try {
                String name = MessageUtils.getNickname(msg);
                System.out.println("new user " + name);
                if (users.containsKey(name)) {
                    // out.println(new String(MessageUtils
                    // .error("Nickname is already in use")));
                    out.write(MessageUtils.error("Nickname is already in use"));
                    synchronized (client) {
                        if (!client.isClosed()) {
                            processor.kill(client);
                        }
                    }
                } else {
                    users.put(name, client);
                    readers.put(client, in);
                    writers.put(client, out);
                }
            } catch (Exception e) {

            }
        } else if (type == 2) {
            System.out.println("Trying : " + new String(msg));
            processor.sendToAll(new String(msg), client);
        } else if (type == 127) {
            System.out.println("Error from user. Killing him");
            processor.kill(client);
        } else {
            processor.sendTo(new String(MessageUtils.error("Bad message")),
                    client);
        }
    }

}