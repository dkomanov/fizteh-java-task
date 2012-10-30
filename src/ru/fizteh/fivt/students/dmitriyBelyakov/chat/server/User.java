package ru.fizteh.fivt.students.dmitriyBelyakov.chat.server;

import ru.fizteh.fivt.students.dmitriyBelyakov.chat.Message;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageBuilder;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageType;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

class User implements Runnable {
    private Socket socket;
    public Thread myThread;
    private String name;
    private boolean authorized;
    volatile private Manager myManager;
    boolean isClosed;

    public User(Socket socket, Manager listener) {
        authorized = false;
        name = null;
        this.socket = socket;
        myManager = listener;
        isClosed = false;
    }

    public void close(boolean isError, boolean sendMessage) {
        isClosed = true;
        myManager.sendFromServer("User " + name() + " left the chat.");
        if (sendMessage && isError) {
            sendMessage(new Message(MessageType.ERROR, "", ""));
        } else if (sendMessage) {
            sendMessage(new Message(MessageType.BYE, "", ""));
        }
        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
        myManager.deleteUser(this);
        myThread.interrupt();
    }

    public void start() {
        myThread = new Thread(this);
        myThread.start();
    }

    private void getHelloMessage() {
        try {
            InputStream iStream = socket.getInputStream();
            if ((byte) iStream.read() != 1) {
                throw new RuntimeException();
            }
            byte[] bLength = new byte[4];
            if (iStream.read(bLength, 0, 4) != 4) {
                throw new RuntimeException();
            }
            ByteBuffer buffer = ByteBuffer.allocate(4).put(bLength);
            buffer.position(0);
            int length = buffer.getInt();
            byte[] bName = new byte[length];
            if (iStream.read(bName, 0, length) != length) {
                throw new RuntimeException();
            }
            name = new String(bName);
            if (myManager.names.contains(name) || name.matches(".*\\s.*")) {
                throw new RuntimeException();
            }
            myManager.names.add(name);
            authorized = true;
        } catch (Throwable e) {
            if (!isClosed) {
                close(true, true);
            }
        }
    }

    private void getMessage() {
        if (!authorized) {
            if (!isClosed) {
                close(true, true);
            }
            return;
        }
        try {
            InputStream iStream = socket.getInputStream();
            if ((byte) iStream.read() != 2) {
                throw new RuntimeException();
            }
            ByteBuffer buffer = ByteBuffer.allocate(4);
            for (int i = 0; i < 4; ++i) {
                int tmp;
                if ((tmp = iStream.read()) < 0) {
                    throw new RuntimeException();
                }
                buffer.put((byte) tmp);
            }
            buffer.position(0);
            int length = buffer.getInt();
            byte[] bName = new byte[length];
            for (int i = 0; i < length; ++i) {
                int tmp;
                if ((tmp = iStream.read()) < 0) {
                    throw new RuntimeException();
                }
                bName[i] = (byte) tmp;
            }
            if (!(new String(bName).equals(name))) {
                throw new RuntimeException();
            }
            buffer = ByteBuffer.allocate(4);
            for (int i = 0; i < 4; ++i) {
                int tmp;
                if ((tmp = iStream.read()) < 0) {
                    throw new RuntimeException();
                }
                buffer.put((byte) tmp);
            }
            buffer.position(0);
            length = buffer.getInt();
            byte[] bMess = new byte[length];
            for (int i = 0; i < length; ++i) {
                int tmp;
                if ((tmp = iStream.read()) < 0) {
                    throw new RuntimeException();
                }
                bMess[i] = (byte) tmp;
            }
            Message message = new Message(MessageType.MESSAGE, name, new String(bMess));
            try {
                myManager.sendAll(message, this);
            } catch (Exception e) {
                System.out.println(e.getClass().getName());
            }
        } catch (Throwable e) {
            if (!isClosed) {
                close(true, true);
            }
        }
    }

    public boolean isAuthorized() {
        return authorized;
    }

    private void getByeMessage() {
        if (!isClosed) {
            close(false, false);
        }
    }

    private void getErrorMessage() {
        if (!isClosed) {
            close(true, false);
        }
    }

    public void sendMessage(Message message) {
        try {
            socket.getOutputStream().write(MessageBuilder.getMessageBytes(message));
        } catch (Throwable e) {
            if (!isClosed) {
                close(true, true);
            }
        }
    }

    @Override
    public void run() {
        try {
            InputStream iStream = socket.getInputStream();
            int iType;
            while (!myThread.isInterrupted() && (iType = iStream.read()) > 0) {
                byte type = (byte) iType;
                if (type == MessageType.HELLO.getId()) {
                    getHelloMessage();
                } else if (type == MessageType.BYE.getId()) {
                    getByeMessage();
                } else if (type == MessageType.MESSAGE.getId()) {
                    getMessage();
                } else {
                    getErrorMessage();
                }
            }
        } catch (Throwable t) {
            if (!isClosed) {
                close(true, true);
            }
        }

    }

    public void join() throws InterruptedException {
        myThread.join();
    }

    public String name() {
        return name;
    }
}