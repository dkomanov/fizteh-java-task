package ru.fizteh.fivt.students.almazNasibullin.chat.server;

import java.nio.channels.SocketChannel;

/**
 * 14.12.12
 * @author almaz
 */

public class Client {
    public SocketChannel sc;
    public byte[] bytes;

    public Client(SocketChannel sc, byte[] bytes) {
        this.sc = sc;
        this.bytes = bytes;
    }
}
