package ru.fizteh.fivt.students.fedyuninV.chat.server;


import ru.fizteh.fivt.students.fedyuninV.IOUtils;
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
    private Thread serverThread;
    private int portNum;

    public Server(int portNum) throws IOException{
        usersOnline.put("server", null);
        this.portNum = portNum;
    }

    public void kill(Message message, String... userNames) {
        synchronized (usersOnline) {
            for(String userName: userNames) {
                if (usersOnline.containsKey(userName)) {
                    if (!userName.equals("server")) {
                        UserWorker userWorker = usersOnline.remove(userName);
                        if (message != null) {
                            try {
                                userWorker.sendMessage(message.getBytes());
                            } catch (Exception ignored) {
                            }
                        }
                        userWorker.kill();
                        userWorker.join();
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
        try {
            serverSocket = new ServerSocket(portNum);
        } catch (IOException ex) {
            System.out.println("Can't start the server");
            System.out.println(ex.getMessage());
            throw new RuntimeException();
        }
        serverThread = new Thread(this);
        serverThread.start();
    }

    public void stop() {
        Set<String> userNames;
        synchronized (usersOnline) {
            userNames = usersOnline.keySet();
        }
        kill(new Message(MessageType.BYE), userNames.toArray(new String[0]));
        synchronized (usersOnline) {
            usersOnline.clear();
        }
        synchronized (unauthorizedUsers) {
            for (UserWorker worker: unauthorizedUsers) {
                try {
                    worker.sendMessage(MessageUtils.bye());
                } catch (Exception ignored) {
                }
                worker.kill();
                worker.join();
            }
            unauthorizedUsers.clear();
        }
        IOUtils.tryClose(serverSocket);
        if (serverThread != null) {
            serverThread.interrupt();
        }
    }

    public void send(Message message, String address) {
        UserWorker worker = null;
        synchronized (usersOnline) {
            if (!usersOnline.containsKey(address)) {
                System.out.println("There is no user with name " + address);
                return;
            } else if (address.equals("server")) {
                MessageUtils.printMessage(message);
                return;
            } else {
                worker = usersOnline.get(address);
            }
        }
        try {
            if (worker != null) {
                worker.sendMessage(message.getBytes());
            }
        } catch (Exception ex) {
            Message errorMessage = new Message(MessageType.ERROR);
            errorMessage.setText("can't deliver previous messages to you");
            kill(errorMessage, address);
        }
    }

    public void sendAll(MessageType type, String name, String text) {
        Message message = new Message(type);
        message.setName(name);
        message.setText(text);
        Set<String> userNames = null;
        synchronized (usersOnline) {
            userNames = usersOnline.keySet();
        }
        if (userNames != null) {
            for (String userName: userNames) {
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
                    try {
                        worker.sendMessage(MessageUtils.message("server", "Already authorized"));
                    } catch (Exception ex) {
                        try {
                            worker.sendMessage(MessageUtils.error("can't deliver previous messages to you"));
                        } catch (Exception ignored) {
                        }
                        worker.kill();
                        worker.join();
                    }
                    return;
                }
                synchronized (usersOnline) {
                    if (usersOnline.containsKey(newName)  ||  newName == null  ||  newName.matches("[ \n\t]*")) {
                        try {
                            worker.sendMessage(MessageUtils.error("Cannot authorize with this name"));
                        } catch (Exception ignored) {
                        }
                        worker.kill();
                        worker.join();
                        return;
                    }
                }
                synchronized (unauthorizedUsers) {
                    unauthorizedUsers.remove(worker);
                }
                worker.setName(newName);
                synchronized (usersOnline) {
                    usersOnline.put(newName, worker);
                }
                System.out.println(newName + " suddenly appeared");
                break;
            case BYE:
                text = " left the chat";
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
                    try {
                        worker.sendMessage(MessageUtils.message("server", "You need to authorize"));
                    } catch (Exception ex) {
                        try {
                            worker.sendMessage(MessageUtils.error("can't deliver previous messages to you"));
                        } catch (Exception ignored) {
                        }
                        worker.kill();
                        worker.join();
                    }
                } else {
                    if (!message.getName().equals(worker.getName())) {
                        try{
                            worker.sendMessage(MessageUtils.message("server", "Don't change your name please"));
                        } catch (Exception ex) {
                            try {
                                worker.sendMessage(MessageUtils.error("can't deliver previous messages to you"));
                            } catch (Exception ignored) {
                            }
                            worker.kill();
                            worker.join();
                        }
                    } else {
                        sendAll(MessageType.MESSAGE, message.getName(), message.getText());
                    }
                }
        }
    }

    public void run() {
        try {
            while (!serverThread.isInterrupted()) {
                Socket socket = serverSocket.accept();
                UserWorker userWorker = new UserWorker(socket, this);
                synchronized (unauthorizedUsers) {
                    unauthorizedUsers.add(userWorker);
                }
                userWorker.start();
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
