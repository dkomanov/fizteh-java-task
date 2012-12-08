package ru.fizteh.fivt.students.fedyuninV.chat.client;

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
public class Client implements Runnable{
    private final Socket socket;
    private final ChatClient chatClient;
    private Thread clientThread;
    private boolean active;
    private final String name;

    public Client(ChatClient chatClient, String name, String host, int port) throws IOException{
        this.chatClient = chatClient;
        this.socket = new Socket(host, port);
        this.name = name;
        this.active = true;
    }

    public void start() {
        clientThread = new Thread(this);
        clientThread.start();
    }

    public void kill() {
        IOUtils.tryClose(socket);
        clientThread.interrupt();
    }

    public void join() {
        try {
            clientThread.join();
        } catch (Exception ignored) {

        }
    }

    public void sendMessage(byte[] bytes) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(bytes);
        } catch (Exception ex) {
            if (bytes.length > 0  &&  MessageType.MESSAGE.getId() == bytes[0]) {
                System.out.println("Can't deliver your message");
            }
        }
    }

    public boolean isActive() {
        return active;
    }

    public void run() {
        try {
            InputStream iStream = socket.getInputStream();
            while (!clientThread.isInterrupted()) {
                try {
                    Message message = MessageUtils.getMessage(iStream);
                    chatClient.processMessage(message, this);
                } catch (InterruptedException ignored) {
                } catch (Exception ex) {
                    try {
                        if (ex.getMessage() == null) {
                        } else {
                            sendMessage(MessageUtils.error(ex.getMessage()));
                        }
                    } catch (Exception ignored) {
                    } finally {
                        kill();
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    public void setActive(boolean status) {
        active = status;
    }

    public String getName() {
        return name;
    }
}
