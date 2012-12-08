package ru.fizteh.fivt.students.altimin.chat;

import ru.fizteh.fivt.chat.MessageType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * User: altimin
 * Date: 12/7/12
 * Time: 11:44 PM
 */
public class MessageReader {
    private InputStream inputStream;

    public MessageReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public byte readByte() throws IOException {
        int value = inputStream.read();
        if (value == -1) {
            throw new IOException("End of stream");
        }
        return (byte) value;
    }

    public int readInt() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        for (int i = 0; i < 4; i ++)
            buffer.put(readByte());
        buffer.position(0);
        int value = buffer.getInt();
        if (value < 0) {
            throw new IOException("Incorrect integer: should be non-negative");
        }
        return value;
    }

    public Message read() throws IOException {
        Message result = new Message();
        byte messageType = readByte();
        if (messageType == MessageType.BYE.getId()) {
            result.type = MessageType.BYE;
        } else if (messageType == MessageType.HELLO.getId()) {
            result.type = MessageType.HELLO;
        } else if (messageType == MessageType.ERROR.getId()) {
            result.type = MessageType.ERROR;
        } else if (messageType == MessageType.MESSAGE.getId()) {
            result.type = MessageType.MESSAGE;
        } else {
            throw new RuntimeException("Unexpected message type");
        }
        int dataLength = readByte();
        for (int i = 0; i < dataLength; i ++) {
            int strLength = readInt();
            byte[] stringBytes = new byte[strLength];
            for (int j = 0; j < strLength; j ++) {
                stringBytes[j] = readByte();
            }
            result.data.add(new String(stringBytes));
        }
        return result;
    }
}

