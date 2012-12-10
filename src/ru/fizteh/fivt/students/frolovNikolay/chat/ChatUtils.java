package ru.fizteh.fivt.students.frolovNikolay.chat;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ChatUtils {

    public static void sendMsg(byte[] msg, SocketChannel to) throws Throwable {
        if (to.isConnected()) {
            to.write(ByteBuffer.wrap(msg));
        }
    }

    public static boolean hasKey(SelectionKey key, int selectionKey)  {
        return (key.readyOps() & selectionKey) == selectionKey;
    }
}