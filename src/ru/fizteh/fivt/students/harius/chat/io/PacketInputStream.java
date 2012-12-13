package ru.fizteh.fivt.students.harius.chat.io;

import java.util.*;
import java.io.InputStream;
import java.io.IOException;

public class PacketInputStream extends InputStream {
    protected InputStream stream;

    public PacketInputStream(InputStream stream) {
        this.stream = stream;
    }

    public Packet readPacket() throws IOException {
        throw new IOException("Unimplemented yet");
    }

    @Override
    public final int read() throws IOException {
        return stream.read();
    }

    @Override
    public void close() throws IOException {
        super.close();
        stream.close();
    }
}