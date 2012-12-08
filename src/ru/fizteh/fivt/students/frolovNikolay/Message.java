package ru.fizteh.fivt.students.frolovNikolay;

import ru.fizteh.fivt.chat.MessageType;

public class Message {
    public final MessageType msgType;
    public final String ownerName;
    public final String msg;
    
    public Message(MessageType msgType, String ownerName, String msg) {
        this.msgType = msgType;
        this.ownerName = ownerName;
        this.msg = msg;
    }
}
