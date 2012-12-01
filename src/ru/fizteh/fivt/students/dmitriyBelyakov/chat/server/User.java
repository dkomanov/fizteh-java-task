package ru.fizteh.fivt.students.dmitriyBelyakov.chat.server;

/**
 * @author Dmitriy Belyakov
 */

import ru.fizteh.fivt.students.dmitriyBelyakov.chat.Message;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageBuilder;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageType;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

class UserThread extends Thread {
    private final InputStream iStream;
    private final User user;

    UserThread(User user, InputStream iStream) {
        this.user = user;
        this.iStream = iStream;
    }

    @Override
    public void run() {
        try {
            int iType;
            while (!isInterrupted() && (iType = iStream.read()) > 0) {
                byte type = (byte) iType;
                if (type == MessageType.HELLO.getId()) {
                    user.getHelloMessage();
                } else if (type == MessageType.BYE.getId()) {
                    user.getByeMessage();
                } else if (type == MessageType.MESSAGE.getId()) {
                    user.getMessage();
                } else {
                    user.getErrorMessage();
                }
            }
        } catch (Throwable t) {
            if (!isInterrupted()) {
                String messageText;
                if (t.getMessage() != null) {
                    messageText = t.getMessage();
                } else {
                    messageText = "Unknown.";
                }
                user.close(User.ERROR, User.SEND_MESSAGE, messageText);
            }
        }

    }
}

public class User {
    private final Socket socket;
    public UserThread myThread;
    private String name;
    private boolean authorized;
    private final Manager myManager;

    static final boolean ERROR = true;
    static final boolean BYE = false;
    static final boolean SEND_MESSAGE = true;
    static final boolean NOT_SEND_MESSAGE = false;

    public User(Socket socket, Manager listener) {
        authorized = false;
        name = null;
        this.socket = socket;
        myManager = listener;
    }

    public void close(boolean isError, boolean sendMessage, String messageText) {
        myThread.interrupt();
        if (sendMessage == SEND_MESSAGE && isError == ERROR) {
            sendMessage(new Message(MessageType.ERROR, "", messageText));
        } else if (sendMessage) {
            sendMessage(new Message(MessageType.BYE, "", messageText));
        }
        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
        myManager.deleteUser(this);
        if (authorized) {
            myManager.showInServer("User " + name + " disconnected.");
        }
    }

    public void close(boolean isError, boolean sendMessage) {
        close(isError, sendMessage, "");
    }

    public void start() {
        try {
            myThread = new UserThread(this, socket.getInputStream());
            myThread.start();
        } catch (Throwable t) {
            String messageText;
            if (t.getMessage() != null) {
                messageText = t.getMessage();
            } else {
                messageText = "Unknown.";
            }
            close(ERROR, SEND_MESSAGE, messageText);
        }
    }

    void getHelloMessage() {
        try {
            Message message = MessageUtils.getHelloMessage(socket.getInputStream());
            name = message.getName();
            if (myManager.names.contains(name) || name.matches(".*\\s+.*") || name.equals("")) {
                throw new RuntimeException("Incorrect or already used name.");
            }
            myManager.names.add(name);
            authorized = true;
            myManager.showInServer("User " + name + " connected.");
        } catch (Throwable e) {
            String messageText;
            if (e.getMessage() != null) {
                messageText = e.getMessage();
            } else {
                messageText = "Unknown.";
            }
            close(ERROR, SEND_MESSAGE, messageText);
        }
    }

    void getMessage() {
        if (!authorized) {
            close(ERROR, SEND_MESSAGE, "You are not authorised");
            return;
        }
        try {
            Message message = MessageUtils.getMessage(socket.getInputStream());
            if (!(message.getName().equals(name))) {
                throw new RuntimeException();
            }
            myManager.sendAll(message, this);
        } catch (Throwable e) {
            String messageText;
            if (e.getMessage() != null) {
                messageText = e.getMessage();
            } else {
                messageText = "Unknown.";
            }
            close(ERROR, SEND_MESSAGE, messageText);
        }
    }

    public boolean isAuthorized() {
        return authorized;
    }

    void getByeMessage() {
        close(BYE, NOT_SEND_MESSAGE);
    }

    void getErrorMessage() {
        try {
            Message message = MessageUtils.getErrorMessage(socket.getInputStream());
            myManager.showInServer(message.getText());
        } catch (Throwable t) {
            close(ERROR, NOT_SEND_MESSAGE);
        }
    }

    public void sendMessage(Message message) {
        try {
            socket.getOutputStream().write(MessageBuilder.getMessageBytes(message));
        } catch (Throwable e) {
            String messageText;
            if (e.getMessage() != null) {
                messageText = e.getMessage();
            } else {
                messageText = "Unknown.";
            }
            close(ERROR, SEND_MESSAGE, messageText);
        }
    }

    public void join() throws InterruptedException {
        myThread.join();
    }

    public String name() {
        return name;
    }
}
