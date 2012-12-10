package ru.fizteh.fivt.students.fedyuninV.chat.server;

import ru.fizteh.fivt.students.fedyuninV.IOUtils;
import ru.fizteh.fivt.students.fedyuninV.chat.message.Message;
import ru.fizteh.fivt.students.fedyuninV.chat.message.MessageType;
import ru.fizteh.fivt.students.fedyuninV.chat.message.MessageUtils;

import java.io.IOException;
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
    }

    public void start() {
        userThread.start();
    }

    public void kill() {
        IOUtils.tryClose(socket);
        userThread.interrupt();
    }

    public void join() {
        try {
            userThread.join();
        } catch (Exception ignored) {
        }
    }

    public void sendMessage(byte[] bytes) throws Exception{
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(bytes);
        } catch (Exception ex) {
            if (bytes.length > 0  &&  MessageType.MESSAGE.getId() == bytes[0]) {
                System.out.println("Can't deliver your message");
            }
            throw ex;
        }
    }

    public void run() {
        try {
            InputStream iStream = socket.getInputStream();
            while (!userThread.isInterrupted()) {
                try {
                    Message message = MessageUtils.getMessage(iStream);
                    server.processMessage(message, this);
                } catch (InterruptedException ignored) {
                } catch (Exception ex) {
                    try {
                        if (ex.getMessage() == null) {
                            sendMessage(MessageUtils.error("An error occured"));
                        } else {
                            sendMessage(MessageUtils.error(ex.getMessage()));
                        }
                    } catch (Exception ignored) {
                    } finally {
                        kill();
                    }
                }
            }
        } catch (IOException ex) {
            try {
                sendMessage(MessageUtils.error("Unable to open input stream"));
            } catch (Exception ignored) {
            }
            kill();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
