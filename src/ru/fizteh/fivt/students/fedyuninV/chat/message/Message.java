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
        name = null;
        text = null;
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

    public byte[] getBytes() {
        switch (type) {
            case BYE:
                return MessageUtils.bye();
            case MESSAGE:
                return MessageUtils.message(name, text);
            case HELLO:
                return MessageUtils.hello(name);
            case ERROR:
                return MessageUtils.error(text);
        }
        return null;
    }
}
