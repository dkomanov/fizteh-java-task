package ru.fizteh.fivt.students.dmitriyBelyakov.chat;

/**
 * @author Dmitriy Belyakov
 */

import java.io.InputStream;
import java.nio.ByteBuffer;

public class MessageUtils {
    public static Message getHelloMessage(InputStream iStream) throws Exception {
        if ((byte) iStream.read() != 1) {
            throw new RuntimeException("Incorrect message specification.");
        }
        ByteBuffer buffer = ByteBuffer.allocate(4);
        for (int i = 0; i < 4; ++i) {
            int tmp;
            if ((tmp = iStream.read()) < 0) {
                throw new RuntimeException("Cannot read message.");
            }
            buffer.put((byte) tmp);
        }
        buffer.position(0);
        int length = buffer.getInt();
        if (length <= 0 || length > 100) {
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
        return new Message(MessageType.HELLO, name, "");
    }

    public static Message getMessage(InputStream iStream) throws Exception {
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
        return new Message(MessageType.MESSAGE, name, new String(bMess));
    }

    public static Message getByeMessage(InputStream iStream) throws Exception {
        return new Message(MessageType.BYE, "", "");
    }

    public static Message getErrorMessage(InputStream iStream) throws Exception {
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
        return new Message(MessageType.HELLO, "", text);
    }
}
