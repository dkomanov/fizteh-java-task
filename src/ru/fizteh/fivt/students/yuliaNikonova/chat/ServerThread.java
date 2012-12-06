package ru.fizteh.fivt.students.yuliaNikonova.chat;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import ru.fizteh.fivt.students.yuliaNikonova.common.Utils;

public class ServerThread extends Thread {
    // The Server that spawned us
    private Server server;
    // The Socket connected to our client
    private Socket socket;
    private ConcurrentHashMap<String, Socket> users;
    private ConcurrentHashMap<Socket, DataOutputStream> outputStreams;
    private ConcurrentHashMap<Socket, DataInputStream> inputStreams;
    private String userName;
    private boolean work;

    // Constructor.
    public ServerThread(Server server, Socket socket, ConcurrentHashMap<String, Socket> users, ConcurrentHashMap<Socket, DataOutputStream> outputStreams,
            ConcurrentHashMap<Socket, DataInputStream> inputStreams) {
        // Save the parameters
        this.server = server;
        this.socket = socket;
        this.users = users;
        this.outputStreams = outputStreams;
        this.inputStreams = inputStreams;
        userName = "";
        work = true;
        // Start up the thread
        start();
    }

    // This runs in a separate thread when start() is called in the
    // constructor.
    public void run() {
        try {

            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            DataInputStream din = new DataInputStream(socket.getInputStream());
            while (work) {
                byte[] message = getMessage(din);
                if (message == null || message.length < 1) {
                    sendErrorandStop(dout, "You send bad message");
                    work = false;
                } else {
                    if (message[0] == 1) { // сообщение с ником
                        if (!userName.isEmpty()) {
                            dout.writeInt(MessageUtils.error("You can't change your nick").length);
                            dout.write(MessageUtils.error("You can't change your nick"));
                        } else {
                            String nick = "";
                            try {
                                nick = MessageUtils.getNickname(message);
                            } catch (Exception e) {
                                sendErrorandStop(dout, e.getMessage());
                                work = false;
                            }
                            if (users.contains(nick)) {
                                sendErrorandStop(dout, "This nick " + nick + " is already exist");
                                work = false;
                            } else {
                                System.out.println("New user: " + nick);
                                userName = nick;
                                users.put(userName, socket);
                                outputStreams.put(socket, dout);
                                inputStreams.put(socket, din);
                            }
                        }
                    } else if (message[0] == 2) { // обычное сообщение
                        List<String> l = MessageUtils.parse(message);
                        String nick = l.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < l.size(); ++i) {
                            sb.append(l.get(i));
                        }
                        server.sendToAll(MessageUtils.message(nick, sb.toString()));
                    } else if (message[0] == 127) { // ошибка
                        List<String> l = MessageUtils.parse(message);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < l.size(); ++i) {
                            sb.append(l.get(i));
                        }
                        System.out.println("Error from " + userName + ": " + sb.toString());
                    } else { // непонятное сообшение, такого клиента отключаем
                        sendErrorandStop(dout, "Unknown message");
                        work = false;
                    }
                }
            }
            // System.out.println("work in cycle is over");
            Utils.close(din);
            Utils.close(dout);
            Utils.close(socket);
        } catch (EOFException ie) {
            // System.err.println("EOFEException");
        } catch (IOException ie) {
            // This does; tell the world!
            System.err.println(ie.getMessage());
            // ie.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            // e.printStackTrace();
        } finally {
            // The connection is closed for one reason or another,
            // so have the server dealing with it
            if (!this.userName.isEmpty()) {
                if (users.containsKey(userName)) {
                    users.remove(userName);
                } else {
                    server.removeConnection(socket);
                }
            }
        }

        // System.out.println("work is over");
    }

    private void sendErrorandStop(DataOutputStream dout, String errorMessage) throws IOException {
        if (work) {
            dout.writeInt(MessageUtils.error(errorMessage).length);
            dout.write(MessageUtils.error(errorMessage));
            // dout.write(MessageUtils.bye());
            if (!userName.isEmpty() || users.containsKey(userName)) {
                server.kill(userName);
            } else {
                server.removeConnection(socket);
            }
        }
    }

    private byte[] getMessage(DataInputStream din) {
        byte[] message; // = new byte[1024];
        // message = new byte[];
        try {
            // System.out.println("server try to get message");
            int len = din.readInt();
            message = new byte[len];
            // System.out.println("Length: " + len);
            int count = din.read(message);
            // System.out.println(count);
            if (count == -1) {
                if (!this.userName.isEmpty()) {
                    if (users.containsKey(userName)) {
                        server.kill(userName);
                    } else {
                        server.removeConnection(socket);
                    }
                }
            }
            return message;
        } catch (Exception e) {
            if (work) {
                if (users.containsKey(userName)) {
                    server.kill(userName);
                } else {
                    server.removeConnection(socket);
                }
            }
        }
        message = null;
        return message;
    }

    public void setWork(boolean value) {
        this.work = value;
    }

}