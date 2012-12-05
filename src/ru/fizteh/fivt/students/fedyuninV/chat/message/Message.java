package ru.fizteh.fivt.students.fedyuninV.chat.message;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class Message {
    private MessageType type;
    private String name;
    private String text;

    public Message(MessageType type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MessageType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }
}
