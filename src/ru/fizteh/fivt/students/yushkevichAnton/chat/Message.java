package ru.fizteh.fivt.students.yushkevichAnton.chat;

import java.io.*;
import java.util.*;

public class Message {
    MessageType type;
    String[] contents;

    public MessageType getType() {
        return type;
    }

    public String[] getContents() {
        return contents;
    }

    private Message() {}

    private Message(MessageType type, String[] contents) {
        this.type = type;
        this.contents = contents;
    }

    public static Message readMessage(InputStream in) throws IOException {
        MessageType type = getMessageType(read(in));
        if (type == null) {
            throw new IOException("Unknown message type");
        }

        int messageCount = read(in);
        String[] contents = new String[messageCount];
        for (int i = 0; i < messageCount; i++) {
            int length = readInt(in);
            byte[] message = new byte[length];
            for (int j = 0; j < length; j++) {
                message[j] = read(in);
            }
            contents[i] = new String(message);
        }

        return new Message(type, contents);
    }

    private static byte read(InputStream in) throws IOException {
        int b = in.read();
        if (b == -1) {
            throw new IOException("Could not read");
        }
        return (byte) b;
    }

    private static int readInt(InputStream in) throws IOException {
        int i = 0;
        i += read(in) << 24;
        i += read(in) << 16;
        i += read(in) << 8;
        i += read(in);
        return i;
    }

    private static MessageType getMessageType(int x) {
        for (MessageType type : MessageType.values()) {
            if (x == type.getId()) {
                return type;
            }
        }
        return null;
    }
}