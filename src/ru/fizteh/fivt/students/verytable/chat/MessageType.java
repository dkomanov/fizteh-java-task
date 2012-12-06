package ru.fizteh.fivt.students.verytable.chat;

public enum MessageType {
    HELLO(1),
    MESSAGE(2),
    BYE(3),
    ERROR(127);

    private final byte id;

    private MessageType(int id) {
        this.id = (byte) id;
    }

    public byte getId() {
        return id;
    }
}
