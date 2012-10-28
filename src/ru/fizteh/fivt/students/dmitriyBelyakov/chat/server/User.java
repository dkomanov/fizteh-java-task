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
    volatile private Listener myListener;

    public User(Socket socket, Listener listener) {
        this.authorized = false;
        this.name = null;
        this.socket = socket;
        myListener = listener;
        this.myThread = new Thread(this);
        myThread.start();
    }

    void close() {
        myThread.interrupt();
        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
        myListener.deleteUser(this);
    }

    private void getHelloMessage() {
        try {
            InputStream iStream = socket.getInputStream();
            if ((byte) iStream.read() != 1) {
                close();
                return;
            }
            byte[] bLength = new byte[4];
            if (iStream.read(bLength, 0, 4) != 4) {
                close();
                return;
            }
            ByteBuffer buffer = ByteBuffer.allocate(4).put(bLength);
            buffer.position(0);
            int length = buffer.getInt();
            byte[] bName = new byte[length];
            if (iStream.read(bName, 0, length) != length) {
                close();
                return;
            }
            name = new String(bName);
            if(myListener.names.contains(name)) {
                close();
                return;
            }
            myListener.names.add(name);
            authorized = true;
        } catch (Exception e) {
            close();
        }
    }

    private void getMessage() {
        if (!authorized) {
            close();
            return;
        }
        try {
            InputStream iStream = socket.getInputStream();
            if ((byte) iStream.read() != 2) {
                close();
                return;
            }
            ByteBuffer buffer = ByteBuffer.allocate(4);
            for (int i = 0; i < 4; ++i) {
                int tmp;
                if((tmp = iStream.read()) < 0) {
                    close();
                    return;
                }
                buffer.put((byte)tmp);
            }
            buffer.position(0);
            int length = buffer.getInt();
            byte[] bName = new byte[length];
            for(int i = 0; i < length; ++i) {
                int tmp;
                if((tmp = iStream.read()) < 0) {
                    close();
                    return;
                }
                bName[i] = (byte)tmp;
            }
            if (!(new String(bName).equals(name))) {
                close();
                return;
            }
            buffer = ByteBuffer.allocate(4);
            for (int i = 0; i < 4; ++i) {
                int tmp;
                if((tmp = iStream.read()) < 0) {
                    close();
                    return;
                }
                buffer.put((byte)tmp);
            }
            buffer.position(0);
            length = buffer.getInt();
            byte[] bMess = new byte[length];
            for(int i = 0; i < length; ++i) {
                int tmp;
                if((tmp = iStream.read()) < 0) {
                    close();
                    return;
                }
                bMess[i] = (byte)tmp;
            }
            Message message = new Message(MessageType.MESSAGE, name, new String(bMess));
            myListener.sendAll(message, this);
        } catch (Exception e) {
            close();
        }
    }

    private void getByeMessage() {
        close();
    }

    private void getErrorMessage() {
        close();
    }

    public void sendMessage(Message message) {
        try {
            socket.getOutputStream().write(MessageBuilder.getMessageBytes(message));
        } catch (Exception e) {
            close();
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
            close();
        }

    }

    public String name() {
        return name;
    }
}