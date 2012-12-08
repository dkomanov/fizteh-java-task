package ru.fizteh.fivt.students.yushkevichAnton.chat.client;

import ru.fizteh.fivt.students.yushkevichAnton.chat.Message;
import ru.fizteh.fivt.chat.MessageUtils;

import java.io.*;
import java.net.Socket;

class CommunicationThread extends Thread {
    private ServerConnection server;
    private Socket socket;
    private InputStream in;
    private OutputStream out;

    private StringBuilder buffer = new StringBuilder();

    CommunicationThread(Socket socket, ServerConnection server) throws IOException {
        this.server = server;
        this.socket = socket;

        in = socket.getInputStream();
        out = socket.getOutputStream();
    }

    @Override
    public void run() {
        try {
            hello();
            while (true) {
                getMessage();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            server.disconnect();
        }
    }

    void hello() throws IOException {
        out.write(MessageUtils.hello(server.getNickName()));
        out.flush();
    }

    void post(String message) throws IOException {
        out.write(MessageUtils.message(server.getNickName(), message));
        out.flush();
    }

    void bye() throws IOException {
        out.write(MessageUtils.bye());
        out.flush();

        in.close();
        out.close();
        socket.close();
    }

    void getMessage() throws IOException {
        Message message = Message.readMessage(in);

        switch (message.getType()) {
            case ERROR:
                throw new IOException(message.getContents()[0]);
            case BYE:
                throw new IOException("Server dropped connection");
            case HELLO:
                throw new IOException("Unexpected behaviour");
            case MESSAGE:
                if (message.getContents().length != 2)
                    throw new IOException("Wrong message format");
        }

        buffer.append('<');
        buffer.append(message.getContents()[0]);
        buffer.append('>');
        buffer.append(' ');
        buffer.append(message.getContents()[1]);
        buffer.append('\n');

        if (server.isCurrentServer()) {
            flush();
        }
    }

    void flush() {
        System.out.print(buffer.toString());
        buffer = new StringBuilder();
    }
}