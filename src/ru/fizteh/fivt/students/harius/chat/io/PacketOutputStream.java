package ru.fizteh.fivt.students.harius.chat.io;

import java.util.*;
import java.io.OutputStream;
import java.io.IOException;

public class PacketOutputStream extends OutputStream {
    protected OutputStream stream;

    public PacketOutputStream(OutputStream stream) {
        this.stream = stream;
    }

    public void writePacket(Packet packet) throws IOException {
        write(packet.getType());
        for (String piece : packet.getData()) {
            write(piece.getBytes());
        }
    }

    @Override
    public final void write(int b) throws IOException {
        stream.write(b);
    }

    @Override
    public void close() throws IOException {
        super.close();
        stream.close();
    }
}