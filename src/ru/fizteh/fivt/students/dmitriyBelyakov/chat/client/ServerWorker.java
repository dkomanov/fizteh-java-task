package ru.fizteh.fivt.students.dmitriyBelyakov.chat.client;

import ru.fizteh.fivt.students.dmitriyBelyakov.chat.Message;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageType;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

class ServerWorkerThread extends Thread {
    private final InputStream iStream;
    private final ServerWorker worker;

    ServerWorkerThread(ServerWorker worker) {
        this.worker = worker;
        iStream = this.worker.iStream;
    }

    @Override
    public void run() {
        try {
            int iType;
            while (!isInterrupted() && (iType = iStream.read()) > 0) {
                byte type = (byte) iType;
                if (type == MessageType.BYE.getId()) {
                    worker.close(false, false);
                } else if (type == MessageType.MESSAGE.getId()) {
                    worker.getMessage();
                } else if (type == MessageType.ERROR.getId()) {
                    worker.close(false, false);
                } else {
                    worker.close(true, true);
                }
            }
        } catch (Exception e) {
            if (!isInterrupted()) {
                worker.close(true, true);
            }
        }
    }
}

class ServerWorker {
    private final String name;
    public Socket socket;
    InputStream iStream;
    private final Manager myManager;
    private Thread myThread;

    ServerWorker(String host, int port, Manager manager) {
        myManager = manager;
        this.name = host + ":" + port;
        try {
            socket = new Socket(host, port);
            iStream = socket.getInputStream();
        } catch (Throwable t) {
            close(true, true);
        }
    }

    public void start() {
        myThread = new ServerWorkerThread(this);
        myThread.start();
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
        myThread.interrupt();
    }

    public void join() throws InterruptedException {
        myThread.join();
    }

    public String name() {
        return name;
    }
}