package ru.fizteh.fivt.students.fedyuninV.chat.client;

import ru.fizteh.fivt.students.almazNasibullin.chat.MessageUtils;
import ru.fizteh.fivt.students.fedyuninV.CommandLine;
import ru.fizteh.fivt.students.fedyuninV.CommandLineParser;
import ru.fizteh.fivt.students.fedyuninV.chat.message.Message;

import java.io.IOException;
import java.util.Set;
import java.util.TreeMap;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class ChatClient implements CommandLine {
    private volatile TreeMap<String, Client> connections;
    private volatile Client activeConnection;
    private String name;

    public ChatClient(String name) {
        this.name = name;
        connections = new TreeMap<>();
    }

    public void printUsage() {
        System.out.println("Incorrect usage of client");
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new RuntimeException();
        }
        ChatClient chatClient = new ChatClient(args[0]);
        CommandLineParser parser = new CommandLineParser(chatClient, System.in, "/exit");
        parser.run();
    }

    @Override
    public void execute(String command, String[] args) {
        if (command.equals("")) {
            if (args ==  null  ||  args.length != 1) {
                printUsage();
                return;
            }
            if (activeConnection != null) {
                activeConnection.sendMessage(MessageUtils.message(name, args[0]));
            } else {
                System.out.println("You have no active connections");
            }
        } else if (command.equals("/use")) {
            if (args == null  ||  args.length != 1) {
                printUsage();
                return;
            }
            synchronized (connections) {
                if (connections.containsKey(args[0])) {
                    if (activeConnection != null) {
                        activeConnection.setActive(false);
                    }
                    activeConnection = connections.get(args[0]);
                    activeConnection.setActive(true);
                } else {
                    System.out.println("There is no connected server with name " + args[0]);
                }
            }
        } else if (command.equals("/connect")) {
            if (args == null  ||  args.length != 1  ||  args[0].indexOf(':') == -1) {
                printUsage();
                return;
            }
            String host = args[0].substring(0, args[0].indexOf(':'));
            try {
                int port = Integer.parseInt(args[0].substring(args[0].indexOf(':') + 1));
                Client newConnection = new Client(this, args[0], host, port);
                newConnection.setActive(true);
                newConnection.start();
                newConnection.sendMessage(MessageUtils.hello(name));
                if (activeConnection != null) {
                    activeConnection.setActive(false);
                }
                synchronized (connections) {
                    connections.put(args[0], newConnection);
                }
                activeConnection = newConnection;
            } catch (IOException ex) {
                System.out.println("Can't create connection to " + args[0]);
                System.out.println(ex.getMessage());
            } catch (Exception ex) {
                printUsage();
            }
        } else if (command.equals("/disconnect")) {
            if (args != null  &&  args.length != 0) {
                printUsage();
                return;
            }
            if (activeConnection != null) {
                activeConnection.setActive(false);
                activeConnection.kill();
                activeConnection.join();
                synchronized (connections) {
                    connections.remove(activeConnection.getName());
                }
            } else {
                System.out.println("You have no active connections");
                System.out.println(connections.size());
                return;
            }
            if (connections.isEmpty()) {
                activeConnection = null;
            } else {
                synchronized (connections) {
                    activeConnection = connections.firstEntry().getValue();
                }
                activeConnection.setActive(true);
            }
        } else if (command.equals("/whereami")) {
            if (activeConnection != null) {
                System.out.println(activeConnection.getName());
            } else {
                System.out.println("You have no active connections");
            }
        } else if (command.equals("/list")) {
            Set<String> hostNames;
            synchronized (connections) {
                hostNames = connections.keySet();
            }
            if (hostNames != null) {
                for (String hostName: hostNames) {
                    System.out.println(hostName);
                }
            }
        } else if (command.equals("/exit")) {
            while (!connections.isEmpty()) {
                activeConnection.sendMessage(MessageUtils.bye());
                execute("/disconnect", new String[0]);
            }
        } else {
            printUsage();
        }
    }

    public void processMessage(Message message, Client client) {
        String name = client.getName();
        switch (message.getType()) {
            case BYE:
                System.out.println("You were kicked from server " + name + ", try next time");
                connections.remove(name);
                client.kill();
                client.join();
                break;
            case ERROR:
                System.out.println("An error occured: " + message.getText());
                if (client.isActive()) {
                    execute("/disconnect", new String[0]);
                } else {
                    connections.remove(name);
                }
                client.kill();
                client.join();
                break;
            case MESSAGE:
                if (client.isActive()) {
                    System.out.println('<' + message.getName() + ">:" + message.getText());
                }
        }
    }

    public void setName(String name) {
        this.name = name;
    }
}
