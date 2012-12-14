package ru.fizteh.fivt.students.tolyapro.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

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

    public static byte[] message(String name, String message) {
        return getMessageBytes(MessageType.MESSAGE, name.getBytes(),
                message.getBytes());
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
        ByteBuffer buffer = ByteBuffer.allocate(1 + 1 + messagesLength).order(
                ByteOrder.BIG_ENDIAN);
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

    public static List<String> parse(byte[] message) {
        List<String> result = new ArrayList<String>();
        ByteBuffer buffer = ByteBuffer.wrap(message);
        int head = buffer.get();
        int count = buffer.get();
        for (int m = 0; m < count; ++m) {
            int length = buffer.getInt();
            byte[] temp = new byte[length];
            buffer.get(temp);
            result.add(new String(temp));
        }
        return result;
    }

    public static String getNickname(byte[] message) throws Exception {
        List<String> result = parse(message);
        if (result.size() != 1 || result.get(0) == null
                || result.get(0).isEmpty()) {
            throw new Exception("Bad Nickname");
        }
        return result.get(0);
    }

    public static String get(InputStream iStream) throws IOException {
        if ((byte) iStream.read() != 2) {
            throw new RuntimeException("Incorrect message specification.");
        }
        ByteBuffer buffer = ByteBuffer.allocate(4);
        for (int i = 0; i < 4; ++i) {
            int tmp;
            if ((tmp = iStream.read()) < 0) {
                throw new RuntimeException("Cannot read message");
            }
            buffer.put((byte) tmp);
        }
        buffer.position(0);
        int length = buffer.getInt();
        if (length > 100 || length <= 0) {
            throw new RuntimeException("Incorrect length.");
        }
        byte[] bName = new byte[length];
        for (int i = 0; i < length; ++i) {
            int tmp;
            if ((tmp = iStream.read()) < 0) {
                throw new RuntimeException("Cannot read message.");
            }
            bName[i] = (byte) tmp;
        }
        String name = new String(bName);
        buffer = ByteBuffer.allocate(4);
        for (int i = 0; i < 4; ++i) {
            int tmp;
            if ((tmp = iStream.read()) < 0) {
                throw new RuntimeException("Cannot read message.");
            }
            buffer.put((byte) tmp);
        }
        buffer.position(0);
        length = buffer.getInt();
        if (length > 1000 || length <= 0) {
            throw new RuntimeException("Incorrect length.");
        }
        byte[] bMess = new byte[length];
        for (int i = 0; i < length; ++i) {
            int tmp;
            if ((tmp = iStream.read()) < 0) {
                throw new RuntimeException("Cannot read message.");
            }
            bMess[i] = (byte) tmp;
        }
        String result = new String(bMess);
        return name + ":" + result;
    }

    public static String getErrorMessage(InputStream iStream) throws Exception {
        if ((byte) iStream.read() != 1) {
            throw new RuntimeException("Incorrect message specification.");
        }
        byte[] bLength = new byte[4];
        for (int i = 0; i < 4; ++i) {
            int tmp;
            if ((tmp = iStream.read()) < 0) {
                throw new RuntimeException("Cannot read message.");
            }
            bLength[i] = (byte) tmp;
        }
        ByteBuffer buffer = ByteBuffer.allocate(4).put(bLength);
        buffer.position(0);
        int length = buffer.getInt();
        if (length <= 0 || length > 100) {
            throw new RuntimeException("Incorrect length.");
        }
        byte[] bText = new byte[length];
        for (int i = 0; i < length; ++i) {
            int tmp;
            if ((tmp = iStream.read()) < 0) {
                throw new RuntimeException("Cannot read message.");
            }
            bText[i] = (byte) tmp;
        }
        String text = new String(bText);
        return text;
    }
}