package ru.fizteh.fivt.students.fedyuninV.chat.server;

import ru.fizteh.fivt.students.fedyuninV.chat.message.Message;
import ru.fizteh.fivt.students.fedyuninV.chat.message.MessageUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class UserWorker implements Runnable{
    private final Socket socket;
    private final Server server;
    private final Thread userThread;
    private String name;

    public UserWorker(Socket socket, Server server) {
        this.server = server;
        this.socket = socket;
        userThread = new Thread(this);
        name = null;
        System.out.println("here");
    }

    public void start() {
        userThread.start();
    }

    public void kill() {
        try {
            socket.close();
        } catch (Exception ignored) {

        }
        userThread.interrupt();
    }

    public void join() {
        try {
            userThread.join();
        } catch (Exception ignored) {

        }
    }

    public void sendMessage(byte[] bytes) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(bytes);
        } catch (Exception ex) {
            System.out.println("Can't deliver your message");
        }
    }

    public void run() {
        try {
            System.out.println("Started!");
            InputStream iStream = socket.getInputStream();
            while (!userThread.isInterrupted()) {
                try {
                    Message message = MessageUtils.getMessage(iStream);
                    server.processMessage(message, this);
                } catch (Exception ex) {
                }
            }
        } catch (Exception ex) {

        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
