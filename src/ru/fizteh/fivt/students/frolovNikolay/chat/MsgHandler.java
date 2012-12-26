package ru.fizteh.fivt.students.frolovNikolay.chat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

import ru.fizteh.fivt.chat.MessageType;

public class MsgHandler {
    
    static public String parseHelloAndError(List<Byte> byteBuffer) {
        byte[] inf = new byte[byteBuffer.size() - 1];
        for (int i = 1; i < byteBuffer.size(); ++i) {
            inf[i - 1] = byteBuffer.get(i);
        }
        byteBuffer.clear();
        return new String(inf).trim();
    }
    
    static public String[] parseMessage(List<Byte> byteBuffer) {
        try {
            String[] result = new String[2];
            StringBuilder message = new StringBuilder();
            int position = 1;
            if (byteBuffer.size() < 2) {
                return null;
            }
            int numberOfMessages = byteBuffer.get(position++);
            if (numberOfMessages < 0) {
                throw new RuntimeException("Incorrect number of messages");
            }
            for (int i = 0; i < numberOfMessages; ++i) {
                if (i + 4 + position > byteBuffer.size()) {
                    return null;
                }
                byte[] byteSize = new byte[4];
                for (int j = 0; j < 4; ++j) {
                    byteSize[j] = byteBuffer.get(position + j);
                }
                position += 4;
                int size = ((byteSize[0] & 0xFF) << 24) + ((byteSize[1] & 0xFF) << 16) + ((byteSize[2] & 0xFF) << 8) + (byteSize[3] & 0xFF);
                if (size <= 0) {
                    throw new RuntimeException("Incorrect length of message");
                }
                if (position + size > byteBuffer.size()) {
                    return null;
                }
                byte[] bytes = new byte[size];
                for (int j = 0; j < size; ++j) {
                    bytes[j] = byteBuffer.get(position + j);
                }
                position += size;
                if (i != 0) {
                    message.append(new String(bytes));
                } else {
                    result[0] = new String(bytes).trim();
                }
            }
            result[1] = message.toString().trim();
            for (int i = 0; i < position; ++i) {
                byteBuffer.remove(0);
            }
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