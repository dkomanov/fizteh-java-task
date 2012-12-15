package ru.fizteh.fivt.students.harius.chat.io;

import java.util.*;
import java.io.InputStream;
import java.io.IOException;

public class PacketInputStream extends InputStream {
    protected InputStream stream;

    public PacketInputStream(InputStream stream) {
        this.stream = stream;
    }

    public Packet readPacket() throws IOException, ProtocolException {
        // System.err.println("reading");
        byte head = (byte) read();
        byte size = (byte) read();
        // System.out.println("head " + head + " size " + size);
        if (size < 0 || size > 100) {
            throw new ProtocolException("Invalid packages size:" + size);
        }
        List<String> messages = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            int length = 0;
            for (int shift = 24; shift >=0; shift -= 8) {
                byte next = (byte) read();
                length |= (next << shift);
            }
            if (length < 0 || length > 100000) {
                throw new ProtocolException("Invalid part length");
            }
            byte[] part = new byte[length];
            for (int sym = 0; sym < length; ++sym) {
                part[sym] = (byte) read();
            }
            messages.add(new String(part));
        }
        return new Packet(head, messages);
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