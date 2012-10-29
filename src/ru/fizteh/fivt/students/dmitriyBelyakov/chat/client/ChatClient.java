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
        } catch (Exception e) {
            System.exit(1);
        }
        try {
            OutputStream oStream = socket.getOutputStream();
            oStream.write(MessageBuilder.getMessageBytes(new Message(MessageType.HELLO, "my_name", "")));
            Listener listener = new Listener(socket.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String str = reader.readLine();
                if (str == null || listener.closed()) {
                    break;
                }
                oStream.write(MessageBuilder.getMessageBytes(new Message(MessageType.MESSAGE, "my_name", str)));
            }
        } catch (Exception e) {
            System.exit(1);
        }
    }
}