package ru.fizteh.fivt.students.alexanderKuzmin.chat;

import java.io.IOException;
import java.io.InputStream;
import ru.fizteh.fivt.chat.MessageType;

public class Message {
    MessageType type;
    String[] contents;

    private Message() {
    }

    private Message(MessageType type, String[] contents) {
        this.type = type;
        this.contents = contents;
    }

    public MessageType getType() {
        return type;
    }

    public String[] getContents() {
        return contents;
    }

    public static Message readMessage(InputStream iStream) throws IOException {
        MessageType type = getMessageType(read(iStream));
        if (type == null) {
            throw new IOException("Unknown message type");
        }

        int messageCount = read(iStream);
        String[] contents = new String[messageCount];
        for (int i = 0; i < messageCount; i++) {
            int length = readInt(iStream);
            byte[] message = new byte[length];
            for (int j = 0; j < length; j++) {
                message[j] = read(iStream);
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

    private static int readInt(InputStream iStream) throws IOException {
        int count = 0;
        count += read(iStream) << 24;
        count += read(iStream) << 16;
        count += read(iStream) << 8;
        count += read(iStream);
        return count;
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