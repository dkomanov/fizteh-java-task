package ru.fizteh.fivt.students.dmitriyBelyakov.chat.client;

import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageType;

import java.io.InputStream;
import java.nio.ByteBuffer;

class Listener implements Runnable {
    private InputStream iStream;
    private boolean     isClosed;

    Listener(InputStream stream) {
        iStream = stream;
        isClosed = false;
        Thread thread = new Thread(this);
        thread.start();
    }

    void getMessage() {
        try {
            if ((byte) iStream.read() != 2) {
                stop();
            }
            byte[] bLength = new byte[4];
            if (iStream.read(bLength, 0, 4) != 4) {
                stop();
            }
            ByteBuffer buffer = ByteBuffer.allocate(4).put(bLength);
            buffer.position(0);
            int length = buffer.getInt();
            byte[] bName = new byte[length];
            if (iStream.read(bName, 0, length) != length) {
                stop();
            }
            System.out.print("<" + new String(bName) + "> ");
            bLength = new byte[4];
            if (iStream.read(bLength, 0, 4) != 4) {
                stop();
            }
            buffer = ByteBuffer.allocate(4).put(bLength);
            buffer.position(0);
            length = buffer.getInt();
            byte[] bMess = new byte[length];
            if(iStream.read(bMess, 0, length) != length) {
                stop();
            }
            System.out.println(new String(bMess));
        } catch (Exception e) {
            stop();
        }
    }

    @Override
    public void run() {
        try {
            int iType;
            while (!Thread.currentThread().isInterrupted() && (iType = iStream.read()) >= 0) {
                byte type = (byte) iType;
                if (type == MessageType.valueOf("BYE").getId()) {
                    stop();
                } else if (type == MessageType.valueOf("MESSAGE").getId()) {
                    getMessage();
                } else {
                    stopError();
                }
            }
        } catch (Exception e) {
            stop();
        }
    }

    void stop() {
        System.exit(0);
    }

    void stopError() {
        System.exit(1);
    }

    boolean closed() {
        return isClosed;
    }
}