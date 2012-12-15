package ru.fizteh.fivt.students.alexanderKuzmin.chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import ru.fizteh.fivt.chat.MessageType;
import ru.fizteh.fivt.chat.MessageUtils;
import javax.xml.ws.ProtocolException;

class StreamThread extends Thread {

    private InputStream iStream;
    private OutputStream oStream;
    private User user;
    private Socket socket;

    public StreamThread(Socket socket, User user) throws IOException {
        this.socket = socket;
        this.user = user;
        iStream = this.socket.getInputStream();
        oStream = this.socket.getOutputStream();
    }

    @Override
    public void run() {
        try {
            reservedName();
            while (user.isInit()) {
                getMessage();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            user.kill(e.getMessage());
        }

    }

    void reservedName() throws IOException {
        Message message = Message.readMessage(iStream);
        if (message.getType() != MessageType.HELLO
                || message.getContents().length < 1) {
            throw new ProtocolException("No initialization.");
        }
        try {
            user.setName(message.getContents()[0]);
            user.addUserInTable();
        } catch (Throwable e) {
            user.kill(e.getMessage());
        }
    }

    void getMessage() throws IOException {
        Message message = Message.readMessage(iStream);
        switch (message.getType()) {
        case ERROR:
            throw new IOException(message.getContents()[0]);
        case HELLO:
            throw new ProtocolException("You already authorized.");
        case BYE:
            user.kill(null);
            return;
        case MESSAGE:
            if (message.getContents().length < 2) {
                throw new ProtocolException("Incorrect message contents");
            }
        }
        user.announcement(message.getContents()[1]);
    }

    void sendMessage(String nickName, String message) throws IOException {
        oStream.write(MessageUtils.message(nickName, message));
        oStream.flush();
    }

    void sendError(String error) {
        try {
            oStream.write(MessageUtils.error(error));
            oStream.flush();
        } catch (IOException e) {
        }
    }

    public void sendLastMessage() {
        try {
            oStream.write(MessageUtils.bye());
            oStream.flush();
        } catch (IOException e) {

        }
    }
}

public class User {
    private final Socket socket;
    private Server server;
    private StreamThread thread;
    private String name;
    private boolean isInit = true;

    public User(Socket socket, Server server) {
        name = null;
        this.socket = socket;
        this.server = server;
        isInit = false;
    }

    public void addUserInTable() {
        server.addUserInTable(name, this);
    }

    public void announcement(String message) {
        server.announcement(name, message);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws IOException {
        if (server.haveName(name)) {
            throw new ProtocolException("That nickname is already taken");
        }
        this.name = name;
    }

    public void start() throws IOException {
        isInit = true;
        thread = new StreamThread(socket, this);
        thread.start();
    }

    public void kill(String message) {
        isInit = false;
        if (thread != null) {
            if (message != null) {
                thread.sendError(message);
            } else {
                thread.sendLastMessage();
            }
            thread.interrupt();
        }
        server.deleteFromTable(name, this);
    }

    public boolean isInit() {
        return isInit;
    }

    public void sendMessage(String name, String message) {
        try {
            thread.sendMessage(name, message);
        } catch (IOException e) {
            this.kill(e.getMessage());
        }
    }
}