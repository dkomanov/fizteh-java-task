package ru.fizteh.fivt.students.alexanderKuzmin.chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import ru.fizteh.fivt.chat.MessageUtils;
import ru.fizteh.fivt.students.alexanderKuzmin.Closers;

class UserThread extends Thread {
    private UserWorker userWorker;
    private Socket socket;
    private InputStream iStream;
    private OutputStream oStream;
    private StringBuilder buffer = new StringBuilder();

    public UserThread(Socket socket, UserWorker userWorker) throws IOException {
        this.userWorker = userWorker;
        this.socket = socket;

        iStream = this.socket.getInputStream();
        oStream = this.socket.getOutputStream();
    }

    @Override
    public void run() {
        try {
            reservedName();
            while (userWorker.isWork()) {
                getMessage();
            }
        } catch (IOException e) {
            if (userWorker.isWork()) {
                Closers.printErrAndNoExit(e.getMessage());
            }
            userWorker.disconnect();
        }
    }

    private void reservedName() throws IOException {
        oStream.write(MessageUtils.hello(userWorker.getName()));
        oStream.flush();
    }

    void getMessage() throws IOException {
        Message message = Message.readMessage(iStream);
        switch (message.getType()) {
        case ERROR:
            throw new IOException(message.getContents()[0]);
        case BYE:
            throw new IOException("Server closed connection.");
        case HELLO:
            throw new IOException("Requery NickName.");
        case MESSAGE:
            if (message.getContents().length != 2)
                throw new IOException("Incorrect message format.");
        }
        buffer.append("<").append(message.getContents()[0]).append(">: ")
                .append(message.getContents()[1]).append("\n");

        if (userWorker.isWork()) {
            flush();
        }
    }

    void flush() {
        System.out.print(buffer.toString());
        buffer = new StringBuilder();
    }

    public void sendMessage(String message) throws IOException {
        oStream.write(MessageUtils.message(userWorker.getName(), message));
        oStream.flush();
    }

    public void closeConnection() throws IOException {
        oStream.write(MessageUtils.bye());
        oStream.flush();
        iStream.close();
        oStream.close();
        socket.close();
    }
}

public class UserWorker {
    private Client client;
    public final String name;
    private UserThread thread;
    private Socket socket;
    private boolean worker = false;

    UserWorker(String host, int port, String clientName, Client client) {
        this.name = clientName;
        this.client = client;
        try {
            worker = true;
            socket = new Socket(host, port);
            thread = new UserThread(socket, this);
            thread.start();
        } catch (Exception e) {
            disconnect();
        }
    }

    public String getName() {
        return name;
    }

    public boolean isWork() {
        return worker;
    }

    public void disconnect() {
        if (!worker) {
            return;
        }
        worker = false;
        try {
            thread.closeConnection();
        } catch (Exception e) {
        }
        thread.interrupt();
        client.deleteFromTable();
    }

    public void close() {
        worker = false;
    }

    public void start() {
        worker = true;
    }

    public void sendMessage(String message) {
        try {
            thread.sendMessage(message);
        } catch (Exception e) {
            disconnect();
        }
    }
}