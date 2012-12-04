package ru.fizteh.fivt.students.fedyuninV.chat.message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


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

    public static Message getExpectedMessage(ByteBuffer buffer, MessageType expectedType) throws Exception{
        Message message = getMessage(buffer);
        if (!message.getType().equals(expectedType)) {
            throw new RuntimeException("Unexpectable type");
        } else {
            return message;
        }
    }

    public static Message getMessage(ByteBuffer buffer) throws Exception{
        int initialPosition = buffer.position();
        MessageType type = MessageType.getMessageType((byte) buffer.get());
        if (type == null) {
            buffer.position(initialPosition);
            throw new Exception("Error in getting message");
        }
        getExpectedMessage(buffer, type);
        return null;
    }

    private static byte[] getMessageBytes(MessageType type, byte[]... messages) {
        int messagesLength = 0;
        for (byte[] bytes : messages) {
            messagesLength += 4 + bytes.length;
        }
        ByteBuffer buffer = ByteBuffer.allocate(1 + 1 + messagesLength)
                .order(ByteOrder.BIG_ENDIAN);
        buffer.put(type.getId());
        buffer.put((byte) messages.length);
        for (byte[] bytes : messages) {
            buffer.putInt(bytes.length).put(bytes);
        }
        return buffer.array();
    }
}