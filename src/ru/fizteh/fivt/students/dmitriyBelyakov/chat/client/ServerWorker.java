package ru.fizteh.fivt.students.dmitriyBelyakov.chat.client;

import ru.fizteh.fivt.students.dmitriyBelyakov.chat.Message;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageType;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ServerWorker implements Runnable {
    private String name;
    public Socket socket;
    private InputStream iStream;
    private Manager myManager;

    ServerWorker(String host, int port, Manager manager) {
        myManager = manager;
        this.name = host + port;
        try {
            socket = new Socket(host, port);
            iStream = socket.getInputStream();
            new Thread(this).start();
        } catch (Throwable t) {
            close(true, true);
        }
    }

    void getMessage() {
        try {
            if ((byte) iStream.read() != 2) {
                close(true, true);
            }
            byte[] bLength = new byte[4];
            if (iStream.read(bLength, 0, 4) != 4) {
                close(true, true);
            }
            ByteBuffer buffer = ByteBuffer.allocate(4).put(bLength);
            buffer.position(0);
            int length = buffer.getInt();
            byte[] bName = new byte[length];
            if (iStream.read(bName, 0, length) != length) {
                close(true, true);
            }
            System.out.print("<" + new String(bName) + "> ");
            bLength = new byte[4];
            if (iStream.read(bLength, 0, 4) != 4) {
                close(true, true);
            }
            buffer = ByteBuffer.allocate(4).put(bLength);
            buffer.position(0);
            length = buffer.getInt();
            byte[] bMess = new byte[length];
            if (iStream.read(bMess, 0, length) != length) {
                close(true, true);
            }
            System.out.println(new String(bMess));
        } catch (Exception e) {
            close(true, true);
        }
    }

    @Override
    public void run() {
        try {
            int iType;
            while (!Thread.currentThread().isInterrupted() && (iType = iStream.read()) > 0) {
                byte type = (byte) iType;
                if (type == MessageType.BYE.getId()) {
                    close(false, false);
                } else if (type == MessageType.MESSAGE.getId()) {
                    getMessage();
                } else if (type == MessageType.ERROR.getId()) {
                    close(false, false);
                } else {
                    close(true, true);
                }
            }
        } catch (Exception e) {
            if (!Thread.currentThread().isInterrupted()) {
                close(true, true);
            }
        }
    }

    public void close(boolean isError, boolean sendMessage) {
        if (sendMessage && isError) {
            myManager.sendMessage(new Message(MessageType.ERROR, "", ""));
        } else if (sendMessage) {
            myManager.sendMessage(new Message(MessageType.BYE, "", ""));
        }
        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
        myManager.deleteServer(this);
        Thread.currentThread().interrupt();
    }

    public void join() throws InterruptedException {
        Thread.currentThread().join();
    }

    public String name() {
        return name;
    }
}