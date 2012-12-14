package ru.fizteh.fivt.students.fedyuninV.chat.message;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
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

    public static MessageType getMessageType(byte id) throws Exception{
        MessageType result = null;
        switch(id) {
            case (1):
                result = HELLO;
                break;
            case (2):
                result = MESSAGE;
                break;
            case (3):
                result = BYE;
                break;
            case (127):
                result = ERROR;
                break;
            default:
                throw new Exception("");
        }
        return result;
    }
}