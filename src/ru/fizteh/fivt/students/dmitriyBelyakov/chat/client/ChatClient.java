package ru.fizteh.fivt.students.dmitriyBelyakov.chat.client;

import ru.fizteh.fivt.students.dmitriyBelyakov.chat.Message;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageBuilder;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

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
            while((str = reader.readLine()) != null) {
                if(str.matches("/connect[ ]+.+:[0-9]+")) {
                    String host = str.replaceAll("(/connect[ ]+)|(:.+)", "");
                    String port = str.replaceAll("/connect[ ]+.+:", "");
                    int portNum = Integer.parseInt(port);
                    socket = new Socket(host, portNum);
                    listener = new Listener(socket.getInputStream());
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