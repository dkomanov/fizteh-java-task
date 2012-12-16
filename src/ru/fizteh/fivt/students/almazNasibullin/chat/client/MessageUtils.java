package ru.fizteh.fivt.students.almazNasibullin.chat.client;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import ru.fizteh.fivt.students.almazNasibullin.chat.MessageType;

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

    public static List<List<String>> getMessages(List<byte[]> messages,
            int curServerNumber) {
        List<List<String>> mes = new ArrayList<List<String>>();

        List<String> l = getMessage(messages, curServerNumber);
        while (!l.isEmpty()) {
            mes.add(l);
            l = getMessage(messages, curServerNumber);
        }
        
        return mes;
    }
    
    public static List<String> getMessage(List<byte[]> messages,
            int curServerNumber) {
        ByteBuffer buffer = ByteBuffer.wrap(messages.get(curServerNumber));

        if (buffer == null) {
            throw new RuntimeException("Buffer null");
        }

        int length = messages.get(curServerNumber).length;
        if (length == 0) {
            return new ArrayList<String>();
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
                throw new RuntimeException("BYE from server");
            case 127:
                throw new RuntimeException("Error from server");
            default:
                throw new RuntimeException("'message' type is wrong");
        }

        if (length == 1) {
            return new ArrayList<String>();
        }

        int count = (int)buffer.get();
        if (count == 0) {
            throw new RuntimeException("Count of messages is equal 0");
        }
        if (count < 0) {
            throw new RuntimeException("Count of messages is less than 0");
        }
        int curPosition = 2;

        for (int i = 0; i < count; ++i) {
            if (buffer.remaining() < 4) {
                return new ArrayList<String>();
            }

            int messageLength = buffer.getInt();
            if (messageLength == 0) {
                throw new RuntimeException("Length of message is equal 0");
            }
            if (messageLength < 0) {
                throw new RuntimeException("Length of message is less than 0");
            }
            if (messageLength > 1000) {
                throw new RuntimeException("Length of message is more than 1000");
            }

            curPosition += 4;
            if (buffer.remaining() < messageLength) {
                return new ArrayList<String>();
            }
            
            byte[] bytes = new byte[messageLength];
            buffer.get(bytes);
            mes.add(new String(bytes));
            curPosition += messageLength;
        }

        byte[] mess = new byte[length - curPosition];
        for (int i = 0; i < length - curPosition; ++i) {
            mess[i] = messages.get(curServerNumber)[i + curPosition];
        }
        messages.remove(curServerNumber);
        messages.add(curServerNumber, mess);
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
