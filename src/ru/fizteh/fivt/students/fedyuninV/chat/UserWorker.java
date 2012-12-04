package ru.fizteh.fivt.students.fedyuninV.chat;

import java.net.Socket;
import java.nio.*;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class UserWorker implements Runnable{
    private final Socket socket;
    private final Server server;
    private final ByteBuffer buffer;
    private final Thread userThread;
    public UserWorker(Socket socket, Server server) {
        this.server = server;
        this.socket = socket;
        buffer = ByteBuffer.wrap(null);
        userThread = new Thread(this);
    }

    public void start() {
        userThread.start();
    }

    public void kill() {
        userThread.interrupt();
    }

    public String getClientName() {
        return null;
    }

    public void run() {
        try {

        } catch (Exception ex) {

        }
    }

}
