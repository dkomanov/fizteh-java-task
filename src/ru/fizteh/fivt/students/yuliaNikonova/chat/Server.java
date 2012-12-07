package ru.fizteh.fivt.students.yuliaNikonova.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

import ru.fizteh.fivt.students.yuliaNikonova.common.Utils;

public class Server extends Thread {
    private ServerSocket ss;
    private ConcurrentHashMap<Socket, DataOutputStream> outputStreams;
    private ConcurrentHashMap<Socket, DataInputStream> inputStreams;
    private ConcurrentHashMap<String, Socket> users;
    private ConcurrentHashMap<Socket, ServerThread> serverThreads;
    private int port = 0;
    private boolean work = true;

    public Server(int port) {
        this.port = port;
        this.start();
    }

    protected void listen() throws IOException {
        // Create the ServerSocket

        ss = new ServerSocket(port);

        // Tell the world we're ready to go
        System.out.println("Listening on " + ss);
        // Keep accepting connections forever
        while (work) {
            // Grab the next incoming connection
            Socket s = ss.accept();
            // Tell the world we've got it
            System.out.println("Connection from " + s);
            // DataOutputStream dout = new
            // DataOutputStream(s.getOutputStream());
            // outputStreams.put(s, dout);
            ServerThread sThread = new ServerThread(this, s, users, outputStreams, inputStreams);
            serverThreads.put(s, sThread);

        }
    }

    // Get an enumeration of all the OutputStreams, one for each client
    // connected to us
    Enumeration getOutputStreams() {
        return outputStreams.elements();
    }

    Enumeration getInputStreams() {
        return inputStreams.elements();
    }

    Enumeration getThreads() {
        return serverThreads.elements();
    }

    // Send a message to all clients (utility routine)
    protected void sendToAll(byte[] message) {
        // System.out.println("Send to all new message");
        synchronized (outputStreams) {
            for (Enumeration e = getOutputStreams(); e.hasMoreElements();) {
                DataOutputStream dout = (DataOutputStream) e.nextElement();
                try {
                    dout.writeInt(message.length);
                    dout.write(message);
                } catch (IOException ie) {
                    System.out.println(ie);
                }
            }
        }
    }

    void removeConnection(Socket s) {

        // System.out.println("I want to remove connection");
        if (serverThreads.containsKey(s)) {
            try {
                serverThreads.get(s).setWork(false);
                serverThreads.get(s).interrupt();
                // System.out.println("Thread is stopped");
            } catch (Exception e) {

            }
        }
        synchronized (outputStreams) {
            // Tell the world
            // System.out.println("Removing connection to " + s);
            // Remove it from our hashtable/list
            if (outputStreams.containsKey(s)) {
                try {
                    outputStreams.get(s).write(MessageUtils.bye());
                } catch (Exception e) {

                }
                Utils.close(outputStreams.get(s));
                outputStreams.remove(s);
            }
            synchronized (inputStreams) {
                if (inputStreams.containsKey(s)) {
                    Utils.close(inputStreams.get(s));
                    inputStreams.remove(s);
                }
            }
            Utils.close(s);
        }
    }

    protected void stopThreads() {
        for (Enumeration e = getThreads(); e.hasMoreElements();) {
            ServerThread sThread = (ServerThread) e.nextElement();
            try {
                sThread.interrupt();
            } catch (Exception ex) {

            }
        }
    }

    public void list() {
        synchronized (users) {
            for (String user : users.keySet()) {
                System.out.println(user);
            }
        }
    }

    public void stopListen() {
        work = false;
        // System.out.println("stopListen");
        Utils.close(ss);
        synchronized (users) {
            for (String user : users.keySet()) {
                kill(user);
            }
        }
        stopThreads();
        synchronized (users) {
            users.clear();
        }
        synchronized (outputStreams) {
            outputStreams.clear();
        }
        synchronized (inputStreams) {
            inputStreams.clear();
        }
        serverThreads.clear();

    }

    public void kill(String user) {

        if (users.containsKey(user)) {
            Socket s = users.get(user);
            users.remove(user);
            if (s != null) {
                removeConnection(s);
            }
        } else {
            System.err.println("User " + user + " is not connected");
        }
    }

    public void send(String user, String message) {
        if (!users.containsKey(user)) {
            System.err.println("User " + user + " is not connected");
        } else {
            Socket s = users.get(user);
            if (s != null) {
                DataOutputStream dout = outputStreams.get(s);
                if (dout != null) {
                    try {
                        dout.writeInt(MessageUtils.message("server", message).length);
                        dout.write(MessageUtils.message("server", message));
                        return;
                    } catch (Exception e) {
                        System.err.println("Can't send message to " + user + " : " + e.getMessage());
                    }
                }
            }

            System.err.println("Can't send message to " + user);
        }

    }

    @Override
    public void run() {
        // System.out.println("run");
        outputStreams = new ConcurrentHashMap<Socket, DataOutputStream>();
        inputStreams = new ConcurrentHashMap<Socket, DataInputStream>();
        users = new ConcurrentHashMap<String, Socket>();
        serverThreads = new ConcurrentHashMap<Socket, ServerThread>();
        startListen();

    }

    public void exitChat() {
        stopListen();
        System.exit(0);

    }

    public void startListen() {
        try {
            work = true;
            this.listen();
        } catch (IOException e) {
            if (work) {
                System.err.println("Server can't listen port " + port + ": " + e.getMessage());
                System.exit(1);
            }
        }

    }
}
