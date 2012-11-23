package ru.fizteh.fivt.students.myhinMihail.chat;

import ru.fizteh.fivt.students.myhinMihail.Utils;

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
    
    public static Utils.Pair<String, String> parseMessage(byte[] message) {
        try {
            String nick = null;
            StringBuilder sb = new StringBuilder();
            
            ByteBuffer buf = ByteBuffer.wrap(message);
            buf.get();
            int messagesCount = buf.get();
            
            for (int i = 0; i < messagesCount; ++i) {
                byte[] bytes = new byte[buf.getInt()];
                buf.get(bytes);
                
                if (i == 0) {
                    nick = new String(bytes);
                } else {
                    sb.append(new String(bytes));
                }
            }
            return new Utils.Pair<String, String>(nick, sb.toString());
            
        } catch (Exception expt) {
            return new Utils.Pair<String, String>(null, null);
        }
    }
}
