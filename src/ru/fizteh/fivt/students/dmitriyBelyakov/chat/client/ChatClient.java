package ru.fizteh.fivt.students.dmitriyBelyakov.chat.client;


import ru.fizteh.fivt.students.dmitriyBelyakov.chat.Message;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageBuilder;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

class ChatClient {
    public static void main(String[] args) {
        if(args.length != 1) {
            System.err.println("Use: ChatClient <nickname>");
            System.exit(1);
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String str;
            Socket socket = null;
            Listener listener = null;
            OutputStream oStream = null;
            String host = null;
            String port = null;
            String serverName = null;
            ArrayList<ServerWorker> workers = new ArrayList<>();
            while((str = reader.readLine()) != null) {
                if(str.matches("/connect[ ]+.+:[0-9]+")) {
                    host = str.replaceAll("(/connect[ ]+)|(:.+)", "");
                    port = str.replaceAll("/connect[ ]+.+:", "");
                    int portNum = Integer.parseInt(port);
                    socket = new Socket(host, portNum);
                    oStream = socket.getOutputStream();
                    listener = new Listener(socket.getInputStream());
                    serverName = host + ":" + port;
                    workers.add(new ServerWorker(host + ":" + port, socket, listener));
                    oStream.write(MessageBuilder.getMessageBytes(new Message(MessageType.HELLO, args[0], "")));
                } else if(str.equals("/whereiam")) {
                    if(listener != null) {
                        System.out.println(serverName);
                    }
                } else if(str.equals("/disconnect")) {
                    if(listener != null) {
                        oStream.write(MessageBuilder.getMessageBytes(new Message(MessageType.BYE, "", "")));
                        socket.close();
                        listener.stop(false);
                        for(int i = 0; i < workers.size(); ++i) {
                            if(workers.get(i).listener == listener) {
                                workers.remove(i);
                                break;
                            }
                        }
                        listener = null;
                    }
                } else if (str.equals("/list")) {
                    for(ServerWorker w : workers) {
                        System.out.println(w.name);
                    }
                } else if(str.equals("/exit")) {
                    for(ServerWorker w: workers) {
                        w.listener.stop(false);
                    }
                    System.exit(0);
                } else if(str.charAt(0) == '/') {
                    System.err.println("Unknown command.");
                } else {
                    oStream.write(MessageBuilder.getMessageBytes(new Message(MessageType.MESSAGE, args[0], str)));
                }
            }
        } catch (Throwable t) {
            if(t.getMessage() != null) {
                System.out.println("Error: " + t.getMessage() + ".");
            } else {
                System.err.println("Error: unknown.");
            }
        }
    }
}