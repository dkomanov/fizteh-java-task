package ru.fizteh.fivt.students.harius.chat.impl;

import ru.fizteh.fivt.students.harius.chat.base.NetworkObservable;
import ru.fizteh.fivt.students.harius.chat.io.*;
import java.net.Socket;
import java.io.IOException;

public class NetworkService extends NetworkObservable
                            implements Runnable {
    private Socket socket;
    private PacketOutputStream output;
    private boolean closed = false;

    public NetworkService(Socket socket) throws IOException {
        this.socket = socket;
        output = new PacketOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        try (PacketInputStream input
                = new PacketInputStream(socket.getInputStream())) {

            while (!closed) {
                Packet received = input.readPacket();
                notifyObserver(received);
            }
        } catch (IOException ioEx) {
            throw new RuntimeException(ioEx);
        }
    }

    @Override
    public void send(Packet packet) throws IOException {
        output.writePacket(packet);
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            // close input?
            output.close();
            closed = true;
        }
    }
}