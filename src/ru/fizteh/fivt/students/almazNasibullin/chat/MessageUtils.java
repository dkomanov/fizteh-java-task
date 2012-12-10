package ru.fizteh.fivt.students.almazNasibullin.chat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * 19.10.12
 * @author almaz
 */

public final class MessageUtils {

    private MessageUtils() {
    }

    public static byte[] hello(String name) {
        return getMessageBytes(MessageType.HELLO, name.getBytes());
    }

    public static byte[] message(String name, String message) {
        return getMessageBytes(
                MessageType.MESSAGE,
                name.getBytes(),
                message.getBytes()
        );
    }

    public static byte[] bye() {
        return getMessageBytes(MessageType.BYE);
    }

    public static byte[] error(String message) {
        return getMessageBytes(MessageType.ERROR, message.getBytes());
    }

    private static byte[] getMessageBytes(MessageType type, byte[]... messages) {
        int messagesLength = 0;
        for (byte[] bytes : messages) {
            messagesLength += 4 + bytes.length;
        }
        // message-type (1 byte) + messages count (1 byte)
        // + messages counts * (message-length (4 byte) + message body)
        ByteBuffer buffer = ByteBuffer.allocate(1 + 1 + messagesLength)
                .order(ByteOrder.BIG_ENDIAN);
        buffer.put(type.getId());
        buffer.put((byte) messages.length);
        for (byte[] bytes : messages) {
            buffer.putInt(bytes.length).put(bytes);
        }
        return buffer.array();
    }

    public static List<String> getMessage(byte[] message) {
        if (message == null) {
            throw new RuntimeException("'message' is NullPointer");
        }

        ByteBuffer buffer = ByteBuffer.wrap(message);
        int length = message.length;
        if (length == 0) {
            throw new RuntimeException("'message' is empty");
        }

        List<String> mes = new ArrayList<String>();
        switch (buffer.get()) {
            case 1:
                mes.add("HELLO");
                break;
            case 2:
                mes.add("MESSAGE");
                break;
            case 3:
                mes.add("BYE");
                break;
            case 127:
                mes.add("ERROR");
                break;
            default:
                throw new RuntimeException("'message' type is wrong");
        }

        if (length == 1) {
            throw new RuntimeException("'message' has only type");
        }

        int count = buffer.get();
        int curPosition = 2;

        for (int i = 0; i < count; ++i) {
            if (curPosition >= length) {
                throw new RuntimeException("Incorrect number of messages");
            }

            int messageLength = buffer.getInt();
            if (messageLength <= 0) {
                throw new RuntimeException("Nonpositive length of message");
            }
            if (curPosition + messageLength >= length) {
                throw new RuntimeException("Incorrect length of message");
            }

            ++curPosition;
            byte[] bytes = new byte[messageLength];
            buffer.get(bytes);
            mes.add(new String(bytes));
            curPosition += messageLength;
        }
        return mes;
    }

    public static String getNickname(List<String> l) {
        if (l.size() != 2) {
            throw new RuntimeException("Message with nickname consists incorrect "
                    + "number of messages");
        }
        if (!l.get(0).equals("HELLO")) {
            throw new RuntimeException("Incorrect type of nickname message");
        }
        return l.get(1);
    }
}
