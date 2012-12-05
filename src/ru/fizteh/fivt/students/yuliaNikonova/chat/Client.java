package ru.fizteh.fivt.students.yuliaNikonova.chat;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import ru.fizteh.fivt.students.yuliaNikonova.common.Utils;

public class Client extends Thread {
    private Socket socket;
    boolean work = true;
    private DataOutputStream dout;
    private DataInputStream din;
    private String userName;
    private ConcurrentHashMap<String, Client> servers;
    private String key = "";
    private boolean show = true;
    private Boolean pause = false;

    // Constructor
    public Client(String host, int port, String name, ConcurrentHashMap<String, Client> servers) throws UnknownHostException, IOException {
        din = null;
        dout = null;
        userName = name;
        this.servers = servers;
        key = host + ":" + port;

        // Initiate the connection
        socket = new Socket(host, port);

        // We got a connection! Tell the world
        System.out.println("connected to " + socket);
        // Let's grab the streams and create DataInput/Output streams
        // from them
        din = new DataInputStream(socket.getInputStream());
        dout = new DataOutputStream(socket.getOutputStream());
        // Start a background thread for receiving messages
        this.start();

    }

    // Gets called when the user types something
    public void sendMessage(String message) {
        try {
            // System.out.println("Message: "+message);
            // Send it to the server
            dout.write(MessageUtils.message(userName, message));
            // System.out.println("Sended message \"" + message +
            // "\" to server");
        } catch (IOException ie) {
            System.err.println("Error: " + ie.getMessage());
            disconnect();
        }
    }

    // Background thread runs this: show messages from other window
    public void run() {
        servers.put(key, this);
        try {
            // System.out.println("Want to say hello to server");
            dout.write(MessageUtils.hello(userName));
            // System.out.println("Username: " + userName);
            // System.out.println("I said hello to server");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            disconnect();
        }
        try {
            // Receive messages one-by-one, forever
            while (work) {
                // System.out.println("pause for " + socket + " : " + pause);
                synchronized (pause) {
                    if (pause) {
                        pause.wait();
                    }
                    // System.out.println("Awake???");
                }
                byte[] message = getMessage(din);

                if (message == null) {
                    // System.err.println("Null message");
                } else {
                    if (message[0] == 2) {
                        List<String> l = MessageUtils.parse(message);
                        StringBuilder sb = new StringBuilder(l.get(0) + ": ");
                        for (int i = 1; i < l.size(); ++i) {
                            sb.append(l.get(i));
                        }
                        if (show) {
                            System.out.println(sb.toString());
                        }
                    } else if (message[0] == 3) {
                        disconnect();
                    } else if (message[0] == 127) {

                        List<String> l = MessageUtils.parse(message);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < l.size(); ++i) {
                            sb.append(l.get(i));
                        }
                        if (show) {
                            System.out.println("Error: " + sb.toString());
                        }
                    }
                }
            }
        } catch (Exception ie) {
            System.out.println(ie);
        }
    }

    private byte[] getMessage(DataInputStream din) {
        // System.out.println("GET MESSAGE");
        byte[] message = new byte[1024];
        try {
            int count = din.read(message);
            if (count == -1) {
                if (work) {
                    if (show) {
                        System.err.println("Error: problems with getting message " + socket);
                    }
                    disconnect();
                }
            }
            return message;
        } catch (Exception e) {
            if (work) {
                if (show) {
                    System.err.println("Error: problems with getting message " + socket + " " + e.getMessage());
                }
                disconnect();
            }
        }
        message = null;
        return message;
    }

    protected void pause() {
        show = false;
        pause = true;
        System.out.println(socket + " is paused");
    }

    protected void begin() {
        System.out.println(socket + " is started");
        show = true;
        pause = false;
        synchronized (pause) {
            pause.notifyAll();
        }
    }

    protected void disconnect() {
        // System.out.println("Disconnect!!!");
        work = false;
        Utils.close(din);
        Utils.close(dout);
        Utils.close(socket);
        if (servers.containsKey(key)) {
            servers.remove(key);
        }

    }

}
