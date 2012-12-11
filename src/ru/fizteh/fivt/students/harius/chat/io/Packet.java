package ru.fizteh.fivt.students.harius.chat.io;

import java.util.List;

public class Packet {
    private final byte type;
    private final List<String> data;

    public Packet(byte type, List<String> data) {
        this.type = type;
        this.data = data;
    }

    public byte getType() {
        return type;
    }

    public List<String> getData() {
        return data;
    }
}