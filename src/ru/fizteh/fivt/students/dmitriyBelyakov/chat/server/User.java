package ru.fizteh.fivt.students.dmitriyBelyakov.chat.server;

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

    public User(Socket socket) {
        this.authorized = false;
        this.name = null;
        this.socket = new Socket();
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

    public void run() {
        try {
            InputStream iStream = socket.getInputStream();
            int iType;
            while((iType = iStream.read()) >= 0) {
                byte type = (byte)iType;
                if(type == MessageType.valueOf("HELLO").getId()) {
                    if((byte)iStream.read() != 1) {
                        close();
                    }
                    byte[] bLength = new byte[4];
                    if(iStream.read(bLength, 0, 4) != 4) {
                        close();
                    }
                    int length = ByteBuffer.allocate(4).put(bLength).getInt();
                    byte[] bName = new byte[length];
                    if(iStream.read(bName, 0, length) != length) {
                        close();
                    }
                    name = ByteBuffer.allocate(length).put(bName).toString();
                    // information
                    System.out.println(name);
                } else if(type == MessageType.valueOf("BYE").getId()) {
                    // TODO bye
                } else if(type == MessageType.valueOf("MESSAGE").getId()) {
                    // TODO message
                } else {
                    // TODO error
                }
            }
        } catch(Throwable t) {
            close();
        }

    }
}