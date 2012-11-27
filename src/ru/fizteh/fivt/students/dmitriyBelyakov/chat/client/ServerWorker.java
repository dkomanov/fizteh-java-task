package ru.fizteh.fivt.students.dmitriyBelyakov.chat.client;

import ru.fizteh.fivt.students.dmitriyBelyakov.chat.Message;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageType;
import ru.fizteh.fivt.students.dmitriyBelyakov.chat.MessageUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

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
                    worker.close(ServerWorker.BYE, ServerWorker.NOT_SEND_MESSAGE);
                } else if (type == MessageType.MESSAGE.getId()) {
                    worker.getMessage();
                } else if (type == MessageType.ERROR.getId()) {
                    Message message = MessageUtils.getErrorMessage(iStream);
                    System.out.println("[Server error] " + message.getText());
                    worker.close(ServerWorker.ERROR, ServerWorker.NOT_SEND_MESSAGE);
                } else {
                    throw new RuntimeException("Incorrect message type.");
                }
            }
        } catch (Exception e) {
            if (!isInterrupted()) {
                String messageText;
                if(e.getMessage() != null) {
                    messageText = e.getMessage();
                } else {
                    messageText = "Unknown.";
                }
                worker.close(ServerWorker.ERROR, ServerWorker.SEND_MESSAGE, messageText);
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
    private boolean isActive;

    static final boolean ERROR = true;
    static final boolean BYE = false;
    static final boolean SEND_MESSAGE = true;
    static final boolean NOT_SEND_MESSAGE = false;

    ServerWorker(String host, int port, Manager manager) {
        myManager = manager;
        this.name = host + ":" + port;
        try {
            socket = new Socket(host, port);
            iStream = socket.getInputStream();
        } catch (Throwable t) {
            close(ERROR, SEND_MESSAGE, "Cannot establish connection.");
        }
        myThread = null;
        isActive = true;
    }

    public void activate() {
        isActive = true;
    }

    public void deactivate() {
        isActive = false;
    }

    public void start() {
        myThread = new ServerWorkerThread(this);
        myThread.start();
    }

    void getMessage() {
        if (!isActive) {
            return;
        }
        try {
            Message message = MessageUtils.getMessage(iStream);
            System.out.println("[" + message.getName() + " at "
                    + new SimpleDateFormat("HH:mm:ss").format(new Date().getTime()) + "] " + message.getText());
        } catch (Exception e) {
            String messageText;
            if(e.getMessage() != null) {
                messageText = e.getMessage();
            } else {
                messageText = "Unknown.";
            }
            close(ERROR, SEND_MESSAGE, messageText);
        }
    }

    public void close(boolean isError, boolean sendMessage, String messageText) {
        if (sendMessage == SEND_MESSAGE && isError == ERROR) {
            myManager.sendMessage(new Message(MessageType.ERROR, "", messageText));
        } else if (sendMessage == SEND_MESSAGE) {
            myManager.sendMessage(new Message(MessageType.BYE, "", messageText));
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

    public void close(boolean isError, boolean sendMessage) {
        close(isError, sendMessage, "");
    }

    public void join() throws InterruptedException {
        myThread.join();
    }

    public String name() {
        return name;
    }
}
