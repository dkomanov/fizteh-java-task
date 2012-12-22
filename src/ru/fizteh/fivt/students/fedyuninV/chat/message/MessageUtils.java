package ru.fizteh.fivt.students.fedyuninV.chat.message;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public final class MessageUtils {

    private static final int MAX_LENGTH = 1000;

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

    private static int getLength(InputStream inputStream) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.position(0);
        int nextByte;
        for (int i = 0; i < 4; i++) {
            if ((nextByte = inputStream.read()) < 0) {
                System.err.println("Cannot get string length");
                throw new Exception("Cannot get string length");
            }
            buffer.put((byte) nextByte);
        }
        buffer.position(0);
        return buffer.getInt();
    }

    private static String getString(InputStream inputStream) throws Exception{
        int length = getLength(inputStream);
        if (length < 0  ||  length > MAX_LENGTH) {
            System.err.println("Incorrect length of message");
            throw new Exception("Incorrect length of message");
        }
        byte[] text = new byte[length];
        int nextByte;
        for (int i = 0; i < length; i++) {
            if ((nextByte = inputStream.read()) < 0) {
                System.err.println("Can't get message");
                throw new Exception("Can't get message");
            }
            text[i] = (byte) nextByte;
        }
        return new String(text);
    }

    public static Message getMessage(InputStream inputStream) throws Exception {
        int typeInt;
        if ((typeInt = inputStream.read()) < 0) {
            System.err.println("Can't get type of message");
            throw new Exception("Can't get type of message");
        }
        Message message = new Message(MessageType.getMessageType((byte) typeInt));
        if (message.getType() == null) {
            throw new Exception("Incorrect type of message");
        }
        int stringsNum = inputStream.read();
        try {
            switch (message.getType()) {
                case MESSAGE:
                    if (stringsNum != 2) {
                        System.err.println("Incorrect string num in message");
                        throw new Exception("Incorrect string num in message");
                    }
                    message.setName(getString(inputStream));
                    message.setText(getString(inputStream));
                    break;
                case BYE:
                    break;
                case ERROR:
                    if (stringsNum != 1) {
                        System.err.println("Incorrect string num in message");
                        throw new Exception("Incorrect string num in message");
                    }
                    message.setText(getString(inputStream));
                    break;
                case HELLO:
                    if (stringsNum != 1) {
                        System.err.println("Incorrect string num in message");
                        throw new Exception("Incorrect string num in message");
                    }
                    message.setName(getString(inputStream));
                    break;
            }
        } catch (Exception ex) {
            if (ex.getMessage().equals("")) {
                System.err.println("Incorrect type of message");
            }
            throw ex;
        }
        return message;
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

    public static void printMessage(Message message) {
        System.err.println('<' + message.getName() + ">:" + message.getText());
    }
}