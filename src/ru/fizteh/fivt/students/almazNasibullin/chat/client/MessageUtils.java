package ru.fizteh.fivt.students.almazNasibullin.chat.client;

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

    public static byte typeOf(byte[] message) {
        return message[0];
    }

    public static List<String> dispatch(byte[] message) {
        List<String> result = new ArrayList<String>();
        ByteBuffer buffer = ByteBuffer.wrap(message);
        int head = buffer.get();
        int count = buffer.get();
        for(int m = 0; m < count; ++m) {
            int length = buffer.getInt();
            byte[] temp = new byte[length];
            buffer.get(temp);
            result.add(new String(temp));
        }
        return result;
    }

    public static String getNickname(byte[] message) {
        List<String> result = dispatch(message);
        if (result.size() != 1) {
            System.err.println("Bad Nickname");
            System.exit(1);
        }
        return result.get(0);
    }
}
