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

    MessageType getType() {
        return type;
    }

    String getName() {
        return name;
    }

    String getText() {
        return text;
    }
}