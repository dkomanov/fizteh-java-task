package ru.fizteh.fivt.students.harius.chat.io;

import ru.fizteh.fivt.chat.MessageType;
import java.util.*;
import java.util.Collections;

public class Packet {
    private final byte type;
    private final List<String> data;

    public Packet(byte type, List<String> data) {
        this.type = type;
        this.data = data;
    }

    public static Packet error(String error) {
        return new Packet(MessageType.ERROR.getId(), Collections.singletonList(error));
    }

    public static Packet message(String nickname, String message) {
        List<String> content = new ArrayList<>();
        content.add(nickname);
        content.add(message);
        return new Packet(MessageType.MESSAGE.getId(), content);
    }

    public static Packet hello(String welcome) {
        return new Packet(MessageType.HELLO.getId(), Collections.singletonList(welcome));
    }

    public static Packet goodbye(String farewell) {
        return new Packet(MessageType.BYE.getId(), Collections.singletonList(farewell));
    }

    public byte getType() {
        return type;
    }

    public List<String> getData() {
        return data;
    }

    public boolean isValid() {
        return isHello()
            || isMessage()
            || isBye()
            || isError();
    }

    public boolean isHello() {
        return type == MessageType.HELLO.getId();
    }

    public boolean isMessage() {
        return type == MessageType.MESSAGE.getId();
    }

    public boolean isBye() {
        return type == MessageType.BYE.getId();
    }

    public boolean isError() {
        return type == MessageType.ERROR.getId();
    }
}