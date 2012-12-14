package ru.fizteh.fivt.students.verytable.chat;

import ru.fizteh.fivt.chat.MessageType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public final class MessageUtils {
    private MessageUtils() {
    }

    public static byte[] hello(String name) {
        return getMessageBytes(MessageType.HELLO, name.getBytes(Charset.forName("UTF-8")));
    }

    public static byte[] message(String name, String message) {
        return getMessageBytes(
                MessageType.MESSAGE,
                name.getBytes(Charset.forName("UTF-8")),
                message.getBytes(Charset.forName("UTF-8"))
        );
    }

    public static byte[] bye() {
        return getMessageBytes(MessageType.BYE);
    }

    public static byte[] error(String message) {
        return getMessageBytes(MessageType.ERROR, message.getBytes(Charset.forName("UTF-8")));
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

    static byte[] toPrimitive(Byte[] bm) {
        byte[] byteMessage = new byte[bm.length];
        for (int i = 0; i < byteMessage.length; ++i) {
            byteMessage[i] = bm[i];
        }
        return byteMessage;
    }
}
