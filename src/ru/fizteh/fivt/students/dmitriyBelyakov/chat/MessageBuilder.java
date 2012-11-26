package ru.fizteh.fivt.students.dmitriyBelyakov.chat;

/**
 * @author Dmitriy Belyakov
 */

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public final class MessageBuilder {
    private MessageBuilder() {

    }

    public static byte[] getMessageBytes(Message message) throws RuntimeException {
        switch (message.getType()) {
            case HELLO:
                return getBytes(MessageType.HELLO, message.getName().getBytes());
            case BYE:
                return getBytes(MessageType.BYE);
            case ERROR:
                return getBytes(MessageType.ERROR, message.getText().getBytes());
            case MESSAGE:
                return getBytes(MessageType.MESSAGE, message.getName().getBytes(), message.getText().getBytes());
            default:
                throw new RuntimeException("Unknown message type.");
        }
    }

    private static byte[] getBytes(MessageType type, byte[]... messages) {
        int messagesLength = 0;
        for (byte[] b : messages) {
            messagesLength += 4 + b.length;
        }
        ByteBuffer buf = ByteBuffer.allocate(1 + 1 + messagesLength).order(ByteOrder.BIG_ENDIAN);
        buf.put(type.getId());
        buf.put((byte) messages.length);
        for (byte[] b : messages) {
            buf.putInt(b.length);
            buf.put(b);
        }
        //System.out.println(Arrays.toString(buf.array()));
        return buf.array();
    }
}
