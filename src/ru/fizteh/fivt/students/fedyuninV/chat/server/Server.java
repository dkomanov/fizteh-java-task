package ru.fizteh.fivt.students.fedyuninV.chat.server;


import ru.fizteh.fivt.students.fedyuninV.chat.message.Message;
import ru.fizteh.fivt.students.fedyuninV.chat.message.MessageType;
import ru.fizteh.fivt.students.fedyuninV.chat.message.MessageUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class Server implements Runnable{

    private ServerSocket serverSocket;
    private Set <UserWorker> unauthorizedUsers = new HashSet<>();
    private Map <String, UserWorker> usersOnline = new HashMap<>();
    private final Thread serverThread;

    public Server(int portNum) throws IOException{
        serverThread = new Thread(this);
        usersOnline.put("server", null);
        serverSocket = new ServerSocket(portNum);
        serverSocket.setSoTimeout(5000);
    }

    public void kill(Message message, String... userNames) {
        synchronized (usersOnline) {
            for(String userName: userNames) {
                if (usersOnline.containsKey(userName)) {
                    if (!userName.equals("server")) {
                        UserWorker userWorker = usersOnline.remove(userName);
                        if (message != null) {
                            userWorker.sendMessage(message.getBytes());
                        }
                    }
                } else {
                    System.out.println("No user with name " + userName);
                }
            }
        }
    }

    public void list() {
        Set<String> userNames = null;
        synchronized (usersOnline) {
            userNames = usersOnline.keySet();
        }
        for (String userName: userNames) {
            System.out.println(userName);
        }
    }

    public void start() {
        serverThread.start();
    }

    public void stop() {
        try {
            Set<String> userNames;
            synchronized (usersOnline) {
                userNames = usersOnline.keySet();
            }
            kill(new Message(MessageType.BYE), userNames.toArray(new String[0]));
            synchronized (unauthorizedUsers) {
                for (UserWorker worker: unauthorizedUsers) {
                    worker.sendMessage(MessageUtils.bye());
                    worker.kill();
                    worker.join();
                }
            }
            serverSocket.close();
        } catch (IOException ignored) {
            System.out.println(ignored.getMessage());
            System.out.println("here");
        }
        serverThread.interrupt();
    }

    public void send(Message message, String address) {
        synchronized (usersOnline) {
            if (!usersOnline.containsKey(address)) {
                System.out.println("There is no user with name " + address);
            } else if (address.equals("server")) {
                MessageUtils.printMessage(message);
            } else {
                usersOnline.get(address).sendMessage(message.getBytes());
            }
        }
    }

    public void sendAll(MessageType type, String name, String text) {
        Message message = new Message(type);
        message.setName(name);
        message.setText(text);
        synchronized (usersOnline) {
            for (String userName: usersOnline.keySet()) {
                send(message, userName);
            }
        }
    }

    public void processMessage(Message message, UserWorker worker) {
        String name = worker.getName();
        String text;
        switch (message.getType()) {
            case HELLO:
                String newName = message.getName();
                if (name != null) {
                    worker.sendMessage(MessageUtils.message("server", "Already authorized"));
                } else if (usersOnline.containsKey(newName)  ||  newName.matches("[ \n\t]+")) {
                    worker.sendMessage(MessageUtils.error("Cannot authorize with this name"));
                } else {
                    synchronized (unauthorizedUsers) {
                        unauthorizedUsers.remove(worker);
                    }
                    worker.setName(newName);
                    synchronized (usersOnline) {
                        usersOnline.put(newName, worker);
                    }
                    System.out.println(newName + " suddenly appeared");
                }
                break;
            case BYE:
                text = " left the chat";
            case ERROR:
                text = " left because of error";
                if (name != null) {
                    System.out.println("User " + name + text);
                    kill(null, name);
                    synchronized (usersOnline) {
                        usersOnline.remove(name);
                    }
                } else {
                    System.out.println("Unauthorized user " + text);
                    synchronized (unauthorizedUsers) {
                        unauthorizedUsers.remove(worker);
                    }
                }
                worker.kill();
                break;
            case MESSAGE:
                if (name == null) {
                    worker.sendMessage(MessageUtils.message("server", "You need to authorize"));
                } else {
                    sendAll(MessageType.MESSAGE, message.getName(), message.getText());
                }
        }
    }

    public void run() {
        try {
            while (!serverThread.isInterrupted()) {
                System.out.println("here");
                Socket socket = serverSocket.accept();
                System.out.println(socket);
                synchronized (unauthorizedUsers) {
                    UserWorker userWorker = new UserWorker(socket, this);
                    unauthorizedUsers.add(userWorker);
                    userWorker.start();
                }
            }
        } catch (SocketTimeoutException ignored) {
        } catch(Exception ex) {
        }
    }

    public void join() {
        try {
            serverThread.join();
        } catch (Exception ignored) {

        }
    }
}
