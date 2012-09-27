package ru.fizteh.fivt.chat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Contains methods to create a message, that could be transmitted via network.
 * It's just for protocol explanation
 */
public final class MessageUtils {

    private MessageUtils() {
    }

    public static byte[] hello(String name) {
        return getMessageBytes(MessageType.HELLO, name.getBytes());
    }

    public static byte[] message(String message) {
        return getMessageBytes(MessageType.MESSAGE, message.getBytes());
    }

    public static byte[] bye() {
        return getMessageBytes(MessageType.BYE, new byte[0]);
    }

    private static byte[] getMessageBytes(MessageType type, byte[] content) {
        return ByteBuffer.allocate(1 + 4 + content.length)
                .order(ByteOrder.BIG_ENDIAN)
                .put(type.getId())
                .putInt(content.length)
                .put(content)
                .array();
    }
}
