package ru.fizteh.fivt.students.yushkevichAnton.chat.server;

import misc.chat.Message;
import misc.chat.MessageType;
import misc.chat.MessageUtils;

import java.io.*;
import java.net.ProtocolException;
import java.net.Socket;
import java.util.*;

class CommunicationThread extends Thread {
    private ClientConnection client;
    private Socket socket;
    private InputStream in;
    private OutputStream out;

    CommunicationThread(Socket socket, ClientConnection client) throws IOException {
        this.client = client;
        this.socket = socket;

        in = socket.getInputStream();
        out = socket.getOutputStream();
    }

    @Override
    public void run() {
        try {
            getHello();
            while (true) {
                getMessage();
            }
        } catch (IOException e) {
            // I use protocol exception to mark exceptions which should be sent to client
            if (e instanceof ProtocolException) {
                sendError(e.getMessage());
            }
            System.err.println(e.getMessage());
            client.disconnect();
        }
    }

    void getHello() throws IOException {
        Message message = Message.readMessage(in);
        if (message.getType() != MessageType.HELLO || message.getContents().length < 1) {
            throw new ProtocolException("Not hello? Do svidania");
        }

        client.setNickName(message.getContents()[0]);
    }

    void getMessage() throws IOException {
        Message message = Message.readMessage(in);

        switch (message.getType()) {
            case ERROR:
                throw new IOException(message.getContents()[0]);
            case HELLO:
                throw new ProtocolException("I already know you");
            case BYE:
                client.disconnect();
                return;
            case MESSAGE:
                if (message.getContents().length < 2) {
                    throw new ProtocolException("Wrong message contents");
                }
        }

        client.announce(message.getContents()[1]);
    }

    void sendMessage(String nickName, String message) throws IOException {
        out.write(MessageUtils.message(nickName, message));
        out.flush();
    }

    void sendBye() {
        try {
            out.write(MessageUtils.bye());
            out.flush();
        } catch (IOException e) {
        }
    }

    void sendError(String error) {
        try {
            out.write(MessageUtils.error(error));
            out.flush();
        } catch (IOException e) {
        }
    }
}