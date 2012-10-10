package ru.fizteh.fivt.students.dmitriyBelyakov.chat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MessageBuilder {
    private MessageBuilder() {

    }

    public static byte[] getMessageBytes(Message message) throws RuntimeException {
        // message-type (1 byte) + messages count (1 byte)
        // + messages counts * (message-length (4 byte) + message body)
        int messageLength = message.getName().getBytes().length + message.getText().getBytes().length;
        ByteBuffer buffer = ByteBuffer.allocate(1 + 1 + 4 + message.getName().getBytes().length
                + message.getText().getBytes().length).order(ByteOrder.BIG_ENDIAN);
        buffer.put(message.getType().getId());
        buffer.put((byte) 2);
        buffer.putInt(message.getName().length());
        // TODO getButes
        return buffer.array();
    }
}