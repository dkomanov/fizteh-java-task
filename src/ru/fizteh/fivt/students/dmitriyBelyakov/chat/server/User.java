package ru.fizteh.fivt.students.dmitriyBelyakov.chat.server;

import ru.fizteh.fivt.students.dmitriyBelyakov.chat.Message;
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

    public User(Socket socket) {
        this.authorized = false;
        this.name = null;
        this.socket = socket;
        this.myThread = new Thread(this);
        this.myThread.run();
    }

    void close() {
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
            // information
            System.out.println("name: " + name);
        } catch (Exception e) {
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
            Message message = new Message(MessageType.HELLO, name, new String(bMess));
            // TODO send all
        } catch (Exception e) {
            close();
        }
    }

    private void getByeMessage() {
        System.out.println("Bye, " + name);
        close();
    }

    private void getErrorMessage() {
        System.out.println("Error, " + name);
        close();
    }

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
}