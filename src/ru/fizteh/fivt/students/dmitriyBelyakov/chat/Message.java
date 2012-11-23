package ru.fizteh.fivt.students.dmitriyBelyakov.chat;

public class Message {
    private final MessageType   type;
    private final String        name;
    private final String        text;

    public Message(MessageType type, String name, String text) {
        this.type = type;
        this.name = name;
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
