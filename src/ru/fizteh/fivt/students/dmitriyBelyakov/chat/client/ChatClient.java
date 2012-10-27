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
        Socket socket = null;
        try {
            socket = new Socket("localhost", 6666);
        } catch(Exception e) {
            System.exit(1);
        }
        OutputStream oStream = null;
        Listener listener = null;
        try {
            oStream = socket.getOutputStream();
            oStream.write(MessageBuilder.getMessageBytes(new Message(MessageType.HELLO, "my_name", "")));
            listener = new Listener(socket.getInputStream());
        } catch (Exception e) {
            System.exit(1);
        }
        try {
            while(true) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String str;
                while((str = reader.readLine()) != null) {
                    System.out.println("Send...");
                    oStream.write(MessageBuilder.getMessageBytes(new Message(MessageType.MESSAGE, "my_name", str)));
                }
            }
        } catch(Exception e) {
            System.exit(1);
        }
    }
}