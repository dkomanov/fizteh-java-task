package ru.fizteh.fivt.students.altimin.chat.server;

import ru.fizteh.fivt.chat.MessageType;
import ru.fizteh.fivt.students.altimin.chat.Message;
import ru.fizteh.fivt.students.altimin.chat.MessageReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ConnectionManager {

    public class User {
        public class UserListener extends Thread {
            InputStream inputStream;

            public UserListener(InputStream inputStream) {
                this.inputStream = inputStream;
            }

            @Override
            public void run() {
                MessageReader reader = new MessageReader(inputStream);
                while (true) {
                    try {
                        Message message = reader.read();
                        if (message.type == MessageType.HELLO) {
                            User.this.processHelloMessage(message);
                        } else if (message.type == MessageType.BYE) {
                            User.this.processByeMessage(message);
                        } else if (message.type == MessageType.MESSAGE) {
                            User.this.processMessage(message);
                        } else {
                            User.this.processErrorMessage(message);
                        }
                    } catch (Exception e) {
                        User.this.close(true, e.getMessage() == null ? "Unknown error" : e.getMessage());
                    }
                }
            }
        }

        private boolean authorized = false;
        private String name = null;
        Socket socket;
        UserListener listener;
        OutputStream outputStream;

        public User(Socket socket) {
            this.socket = socket;
        }

        public void start() {
            try {
                outputStream = socket.getOutputStream();
                listener = new UserListener(socket.getInputStream());
                listener.start();
            } catch (Exception e) {
                close(true, e.getMessage() == null ? "Unknown error" : e.getMessage());
            }
        }

        private void processHelloMessage(Message message) {
           try {
               if (message.data.size() != 1) {
                   throw new Exception("Expected length 1 for hello message");
               }
               name = message.data.get(0);
               if (!ConnectionManager.isCorrectName(name)) {
                   throw new Exception(name + " isn't correct name");
               }
               if (ConnectionManager.this.hasName(name)) {
                   throw new Exception(name + " is already taken");
               }
               if (authorized) {
                   throw new Exception("Already authorized");
               }
               authorized = true;
               ConnectionManager.this.print("User " + name + " connected");
           } catch (Exception e) {
               close(true, e.getMessage() == null ? "Unknown error" : e.getMessage());
           }
        }

        private void processByeMessage(Message message) {
            close(false, null);
        }

        public void kill(boolean isError, String message) {
            listener.interrupt();
            try {
                if (message != null) {
                    if (isError) {
                        outputStream.write(new Message(MessageType.ERROR, message).toByteArray());
                    } else {
                        outputStream.write(new Message(MessageType.BYE, message).toByteArray());
                    }
                }
            } catch (Exception e) {
            }
            try {
                socket.close();
            } catch (Exception e) {
            }
        }

        public void close(boolean isError, String message) {
            kill(isError, message);
            ConnectionManager.this.deleteUser(this);
        }

        private void processErrorMessage(Message message) {
            close(true, null);
        }

        private void processMessage(Message message) {
            if (!authorized) {
                close(true, "Not authorized");
            }
            if (message.data.size() != 2) {
                return;
            }
            String name = message.data.get(0);
            String msg = message.data.get(1);
            if (!name.equals(this.name)) {
                close(true, "Incorrect name");
            }
            ConnectionManager.this.send(msg, name, null);
        }

        public void send(Message message) {
            if (message.data.get(0).equals(name)) {
                return;
            }
            try {
                outputStream.write(message.toByteArray());
            } catch (Exception e) {
                close(true, e.getMessage() == null ? "Unknown error" : e.getMessage());
            }
        }

        public String getName() {
            return name;
        }
    }

    static boolean isCorrectName(String name) {
        return !name.equals("<server>") && name.indexOf(' ' ) == -1;
    }

    private final Object userListLock = new Object();
    private List<User> users = new ArrayList<User>();
    private Set<String> userNames = Collections.synchronizedSet(new HashSet<String>());

    public boolean hasName(String name) {
        return userNames.contains(name);
    }

    public void print(String s) {
        synchronized (System.out) {
            System.out.println(s);
        }
    }

    public void addUser(User user) {
        synchronized (userListLock) {
            users.add(user);
        }
        userNames.add(user.getName());
    }

    public void deleteUser(User user) {
        synchronized (userListLock) {
            users.remove(user);
        }
        userNames.remove(user.name);
    }

    public boolean killUser(String userName) {
        synchronized (userListLock) {
            for (User user: users) {
                if (user.getName().equals(userName)) {
                    user.kill(false, "Server closed connection");
                    users.remove(user);
                    return true;
                }
            }
        }
        return false;
    }

    public void killAll() {
        synchronized (userListLock) {
            for (User user: users) {
                user.kill(false, "Server closed connection");
            }
            users.clear();
        }
    }

    public void send(String msg, String from, String to) {
        Message message = new Message(MessageType.MESSAGE, from, msg);
        synchronized (userListLock) {
            for (User user: users) {
                if (to == null || user.getName().equals(to))
                    user.send(message);
            }
        }
    }

    String listUsers() {
        StringBuilder buffer = new StringBuilder();
        synchronized (userListLock) {
            boolean appendSpace = false;
            for (User user: users) {
                if (user.authorized) {
                    if (appendSpace) {
                        buffer.append(' ');
                    }
                    buffer.append(user.getName());
                    appendSpace = true;
                }
            }
        }
        return buffer.toString();
    }

    ConnectionListener listener;

    public static void log(String s) {
        synchronized (System.err) {
            System.err.println(s);
        }
    }

    public ConnectionManager() {
    }

    private boolean isRun = false;

    public void start(ServerSocket socket) {
        if (isRun) {
            print("Already running");
        } else {
            userNames.clear();
            users.clear();
            listener = new ConnectionListener(socket);
            listener.start();
            isRun = true;
        }
    }

    public void stop() {
        killAll();
        listener.interrupt();
        isRun = false;
    }

    public class ConnectionListener extends Thread {
        ServerSocket serverSocket;

        public ConnectionListener(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Socket socket = serverSocket.accept();
                    User user = new User(socket);
                    ConnectionManager.this.addUser(user);
                    user.start();
                }
            } catch (IOException e) {
                ConnectionManager.this.print("Connection failed");
                ConnectionManager.this.killAll();
            }
        }
    }
}
