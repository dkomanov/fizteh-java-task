package ru.fizteh.fivt.students.dmitriyBelyakov.chat;

import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageType;

public class Message {
    private final MessageType   type;
    private final String        name;
    private final String        text;

    Message(MessageType type, String name, String text) {
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