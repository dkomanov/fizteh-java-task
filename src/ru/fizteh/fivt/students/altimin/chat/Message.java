package ru.fizteh.fivt.students.altimin.chat;

import ru.fizteh.fivt.chat.MessageType;
import ru.fizteh.fivt.chat.MessageUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: altimin
 * Date: 12/7/12
 * Time: 11:44 PM
 */
public class Message {
    public MessageType type;
    public List<String> data;

    public Message() {
        data = new ArrayList<String>();
    }

    public Message(MessageType type) {
        this.type = type;
        data = new ArrayList<String>();
    }

    public Message(MessageType type, String... data) {
        this.type = type;
        this.data = Arrays.asList(data);
    }

    public byte[] toByteArray() {
        if (type.equals(MessageType.BYE)) {
            if (data.size() != 0) {
                throw new RuntimeException("Expected zero strings as data for message with type 'bye'");
            }
            return MessageUtils.bye();
        } else if (type.equals(MessageType.ERROR)) {
            if (data.size() != 1) {
                throw new RuntimeException("Expected one string as data for message with type 'error'");
            }
            return MessageUtils.error(data.get(0));
        } else if (type.equals(MessageType.MESSAGE)) {
            if (data.size() != 2) {
                throw new RuntimeException("Expected two strings as data for message with type 'message'");
            }
            return MessageUtils.message(data.get(0), data.get(1));
        } else if (type.equals(MessageType.HELLO)) {
            if (data.size() != 1) {
                throw new RuntimeException("Expected one string as data for message with type 'hello'");
            }
            return MessageUtils.hello(data.get(0));
        } else {
            throw new RuntimeException("Unexpected message type");
        }
    }

    @Override
    public boolean equals(Object obj) {
        Message msg = (Message) obj;
        return msg.data.equals(data) && msg.type.equals(type);
    }
}
