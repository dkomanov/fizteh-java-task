package ru.fizteh.fivt.students.dmitriyBelyakov.chat.server;

import ru.fizteh.fivt.students.dmitriyBelyakov.chat.Message;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageBuilder;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageType;
import ru.fizteh.fivt.students.dmitriyBelyakov.parallelSort.IoUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

class User implements Runnable {
    private Socket socket;
    private Thread myThread;
    private String name;
    private boolean authorized;
    private Listener myListener;

    public User(Socket socket, Listener listener) {
        this.authorized = false;
        this.name = null;
        this.socket = socket;
        this.myThread = new Thread(this);
        this.myThread.start();
        myListener = listener;
    }

    void close() {
        myListener.deleteUser(this);
        IoUtils.close(socket);
    }

    void sendMessage(byte[] bytes) {
        try {
            OutputStream oStream = socket.getOutputStream();
            oStream.write(bytes);
        } catch(Exception e) {
            close();
        }
    }

    private void getHelloMessage() {
        try {
            InputStream iStream = socket.getInputStream();
            if((byte)iStream.read() != 1) {
                close();
            }
            byte[] bLength = new byte[4];
            if(iStream.read(bLength, 0, 4) != 4) {
                close();
            }
            ByteBuffer buffer =  ByteBuffer.allocate(4).put(bLength);
            buffer.position(0);
            int length = buffer.getInt();
            byte[] bName = new byte[length];
            if(iStream.read(bName, 0, length) != length) {
                close();
            }
            name = new String(bName);
            authorized = true;
        } catch (Exception e) {
            close();
        }
    }

    private void getMessage() {
        if(!authorized) {
            close();
        }
        try {
            InputStream iStream = socket.getInputStream();
            if((byte)iStream.read() != 2) {
                close();
            }
            byte[] bLength = new byte[4];
            if(iStream.read(bLength, 0, 4) != 4) {
                close();
            }
            ByteBuffer buffer =  ByteBuffer.allocate(4).put(bLength);
            buffer.position(0);
            int length = buffer.getInt();
            byte[] bName = new byte[length];
            if(iStream.read(bName, 0, length) != length) {
                close();
            }
            if(!(new String(bName).equals(name))) {
                close();
            }
            bLength = new byte[4];
            if(iStream.read(bLength, 0, 4) != 4) {
                close();
            }
            buffer =  ByteBuffer.allocate(4).put(bLength);
            buffer.position(0);
            length = buffer.getInt();
            byte[] bMess = new byte[length];
            if(iStream.read(bMess, 0, length) != length) {
                close();
            }
            Message message = new Message(MessageType.MESSAGE, name, new String(bMess));
            System.out.println("Send...");
            myListener.sendAll(message);
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
        System.out.println("Sent current message...");
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
            while((iType = iStream.read()) >= 0) {
                byte type = (byte)iType;
                if(type == MessageType.valueOf("HELLO").getId()) {
                    getHelloMessage();
                } else if(type == MessageType.valueOf("BYE").getId()) {
                    getByeMessage();
                } else if(type == MessageType.valueOf("MESSAGE").getId()) {
                    getMessage();
                } else {
                    getErrorMessage();
                }
            }
        } catch(Throwable t) {
            close();
        }

    }

    public String name() {
        return name;
    }
}