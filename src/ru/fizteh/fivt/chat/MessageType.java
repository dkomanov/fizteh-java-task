package ru.fizteh.fivt.chat;

/**
 * A message type. Encodes as one byte.
 */
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
