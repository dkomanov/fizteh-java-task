package ru.fizteh.fivt.students.harius.chat.base;

import java.net.*;
import java.io.*;

public class Registrator implements Runnable, Closeable {
    protected final ServerSocket socket;
    protected final RegistratorObserver observer;

    public Registrator(RegistratorObserver observer, int port) throws IOException {
        this.observer = observer;
        socket = new ServerSocket(port);
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                Socket user = socket.accept();
                observer.processRegistration(user);
            } catch (IOException ioEx) {
                if (!socket.isClosed()) {
                    System.err.println("i/o exception while accepting user: "
                        + ioEx.getMessage());
                }
            }
        }
    }
}