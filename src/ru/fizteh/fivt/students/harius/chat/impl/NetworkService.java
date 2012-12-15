package ru.fizteh.fivt.students.harius.chat.impl;

import ru.fizteh.fivt.students.harius.chat.base.NetworkObservable;
import ru.fizteh.fivt.students.harius.chat.io.*;
import java.net.Socket;
import java.io.IOException;

public class NetworkService extends NetworkObservable {
    private Socket socket;
    private PacketOutputStream output;
    private PacketInputStream input;
    private boolean closed = false;

    public NetworkService(Socket socket) throws IOException {
        this.socket = socket;
        output = new PacketOutputStream(socket.getOutputStream());
        input = new PacketInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            while (!closed) {
                Packet received = input.readPacket();
                notifyObserver(received);
            }
        } catch (IOException ioEx) {
            if (!closed) {
                try {
                    close("i/o error: " + ioEx.getMessage());
                } catch (IOException anotherIoEx) {
                    System.err.println("i/o error while closing network service: "
                        + anotherIoEx.getMessage());
                }
            }
        } catch (ProtocolException proto) {
            try {
                close("protocol error: " + proto.getMessage());
            } catch (IOException ioEx) {
                System.err.println("i/o error while closing network service: "
                    + ioEx.getMessage());
            }
        }
    }

    @Override
    public void send(Packet packet) throws IOException {
        if (!closed) {
            output.writePacket(packet);
        }
    }

    @Override
    public void close() throws IOException {
        close("disconnecting");
    }

    public void close(String reason) throws IOException {
        if (!closed) {
            closed = true;
            notifyClosed(reason);
            input.close();
            output.close();
        }
    }

    @Override
    public String repr() {
        return socket.getInetAddress().toString();
    }
}