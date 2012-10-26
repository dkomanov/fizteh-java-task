package ru.fizteh.fivt.students.dmitriyBelyakov.chat.client;

import ru.fizteh.fivt.students.dmitriyBelyakov.chat.Message;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageBuilder;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageType;

import java.io.OutputStream;
import java.net.Socket;

class ChatClient {
    public static void main(String[] args) {
        Socket socket = null;
        try {
            socket = new Socket("localhost", 6666);
        } catch(Exception e) {
            System.exit(2);
        }
        Message message = new Message(MessageType.HELLO, "my_name", "");
        MessageBuilder.getMessageBytes(message);
        try {
            OutputStream oStream = socket.getOutputStream();
            oStream.write(MessageBuilder.getMessageBytes(message));
            oStream.write(MessageBuilder.getMessageBytes(new Message(MessageType.BYE, "", "")));
        } catch(Exception e) {
            System.exit(1);
        }
    }
}