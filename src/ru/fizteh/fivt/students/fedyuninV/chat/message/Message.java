package ru.fizteh.fivt.students.fedyuninV.chat.message;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class Message {
    private MessageType type;
    private String sender;
    private String receiver;
    private String text;

    public Message(MessageType type) {
        this.type = type;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MessageType getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getText() {
        return text;
    }
}
