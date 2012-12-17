/*
 * PacketInputStream.java
 * Dec 15, 2012
 * By github.com/harius
 */

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
        byte head = (byte) read();
        byte size = (byte) read();
        if (head == -1 || size == -1) {
            throw new IOException("Unexpected connection reset");
        }
        if (size > 100) {
            throw new ProtocolException("Invalid packages size: " + size);
        }
        List<String> messages = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            int length = 0;
            for (int shift = 24; shift >=0; shift -= 8) {
                byte next = (byte) read();
                if (next == -1) {
                    throw new IOException("Unexpected connection reset");
                }
                length |= (next << shift);
            }
            if (length < 0 || length > 100000) {
                throw new ProtocolException("Invalid part length: " + length);
            }
            byte[] part = new byte[length];
            for (int sym = 0; sym < length; ++sym) {
                part[sym] = (byte) read();
                if (part[sym] == -1) {
                    throw new IOException("Unexpected connection reset");
                }
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