package ru.fizteh.fivt.students.harius.chat.io;

import java.util.*;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class PacketOutputStream extends OutputStream {
    protected OutputStream stream;

    public PacketOutputStream(OutputStream stream) {
        this.stream = stream;
    }

    public void writePacket(Packet packet) throws IOException {
        write(packet.getType());
        write(packet.getData().size());
        for (String piece : packet.getData()) {
            byte[] bytes = piece.getBytes();
            ByteBuffer buffer = ByteBuffer.allocate(4);
            buffer.putInt(bytes.length);
            write(buffer.array());
            write(bytes);
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