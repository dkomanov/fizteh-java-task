package ru.fizteh.fivt.students.frolovNikolay.chat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import ru.fizteh.fivt.chat.MessageType;

public class MsgHandler {
    
    static public String parseHelloAndError(byte[] msg) {
        byte[] inf = new byte[msg.length - 1];
        for (int i = 1; i < msg.length; ++i) {
            inf[i - 1] = msg[i];
        }
        return new String(inf).trim();
    }
    
    static public String[] parseMessage(byte[] msg) {
        try {
            String[] result = new String[2];
            StringBuffer message = new StringBuffer();
            ByteBuffer buffer = ByteBuffer.wrap(msg);
            buffer.get();
            int numberOfMessages = buffer.get();
            for (int i = 0; i < numberOfMessages; ++i) {
                byte[] bytes = new byte[buffer.getInt()];
                buffer.get(bytes);
                if (i != 0) {
                    message.append(new String(bytes));
                } else {
                    result[0] = new String(bytes).trim();
                }
            }
            result[1] = message.toString().trim();
            return result;
        } catch (Throwable ignoringException) {
            String[] result = new String[2];
            result[0] = null;
            result[1] = null;
            return result;
        }
    }

    
    private MsgHandler() {
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
}