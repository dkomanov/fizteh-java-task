package ru.fizteh.fivt.students.dmitriyBelyakov.chat.client;

import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageType;

import java.io.InputStream;
import java.nio.ByteBuffer;

class Listener implements Runnable {
    volatile private InputStream iStream;

    Listener(InputStream stream) {
        iStream = stream;
        Thread thread = new Thread(this);
        thread.start();
    }

    void getMessage() {
        try {
            if ((byte) iStream.read() != 2) {
                stop(true);
            }
            byte[] bLength = new byte[4];
            if (iStream.read(bLength, 0, 4) != 4) {
                stop(true);
            }
            ByteBuffer buffer = ByteBuffer.allocate(4).put(bLength);
            buffer.position(0);
            int length = buffer.getInt();
            byte[] bName = new byte[length];
            if (iStream.read(bName, 0, length) != length) {
                stop(true);
            }
            System.out.print("<" + new String(bName) + "> ");
            bLength = new byte[4];
            if (iStream.read(bLength, 0, 4) != 4) {
                stop(true);
            }
            buffer = ByteBuffer.allocate(4).put(bLength);
            buffer.position(0);
            length = buffer.getInt();
            byte[] bMess = new byte[length];
            if(iStream.read(bMess, 0, length) != length) {
                stop(true);
            }
            System.out.println(new String(bMess));
        } catch (Exception e) {
            stop(true);
        }
    }

    @Override
    public void run() {
        try {
            int iType;
            while (!Thread.currentThread().isInterrupted() && (iType = iStream.read()) >= 0) {
                byte type = (byte) iType;
                if (type == MessageType.valueOf("BYE").getId()) {
                    stop(true);
                } else if (type == MessageType.valueOf("MESSAGE").getId()) {
                    getMessage();
                } else {
                    stop(true);
                }
            }
        } catch (Exception e) {
            System.out.println("Ooops...");
            if(!Thread.currentThread().isInterrupted()) {
                System.out.println("?");
                stop(true);
            }
        }
    }

    synchronized public void stop(boolean exit) {
        Thread.currentThread().interrupt();
        try {
            iStream.close();
        } catch (Exception e) {

        }
    }
}