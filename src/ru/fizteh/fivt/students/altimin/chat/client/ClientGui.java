    package ru.fizteh.fivt.students.altimin.chat.client;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ClientGui extends JFrame {
    static final int WIDTH = 300;

    private JTextArea messageArea;
    private JTextArea errorArea;
    private JTextField inputField;
    private JList<String> servers;
    private DefaultListModel<String> serversListModel = new DefaultListModel<String>();
    private ClientWithGui client = null;
    private JTextField loginField;
    private JButton loginButton;

    private JTextField newConnectionField;
    private JButton newConnectionButton;

    private static void createGUI() {
        ClientGui client = new ClientGui();
        client.setLayout(new GridBagLayout());
        client.servers = new JList<String>(client.serversListModel);
        client.servers.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        client.servers.getSelectionModel().addListSelectionListener(client.new MyListSelectionListener());
        client.messageArea = new JTextArea();
        client.errorArea = new JTextArea();
        client.inputField = new JTextField();
        client.inputField.addActionListener(client.new NewCommandListener());
        client.messageArea.setEditable(false);
        client.errorArea.setEditable(false);
        client.loginField = new JTextField();
        client.loginButton = new JButton("Authorize");
        client.loginButton.addActionListener(client.new AuthorizedActionListener());
        client.newConnectionField = new JTextField();
        client.newConnectionButton = new JButton("Connect");
        client.newConnectionButton.addActionListener(client.new NewConnectionActionListener());
        //client.client = client.new ClientWithGui(login);
        JScrollPane messageScrollPane = new JScrollPane(client.messageArea);
        JScrollPane errorScrollPane = new JScrollPane(client.errorArea);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 2.5;
        c.weighty = 4;
        c.gridheight = 5;
        client.add(messageScrollPane, c);
        c.fill = GridBagConstraints.BOTH;
        c.gridy += c.gridheight;
        c.gridheight = 1;
        c.weighty = 1;
        c.insets = new Insets(10, 0, 0, 0);
        client.add(errorScrollPane, c);
        c.gridy += 1;
        c.weighty = 0;
        c.insets = new Insets(10, 0, 0, 0);
        client.add(client.inputField, c);
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1;
        c.insets = new Insets(10, 20, 0, 0);
        client.add(client.loginField, c);
        c.gridy += 1;
        c.insets = new Insets(0, 20, 0, 0);
        c.fill = GridBagConstraints.BOTH;
        client.add(client.loginButton, c);
        c.insets = new Insets(10, 20, 0, 0);
        c.gridy += 1;
        client.add(client.newConnectionField, c);
        c.gridy += 1;
        c.insets = new Insets(0, 20, 0, 0);
        client.add(client.newConnectionButton, c);
        c.insets = new Insets(10, 20, 0, 0);
        c.gridy += 1;
        c.gridheight = 4;
        client.add(client.servers, c);
        client.setMinimumSize(new Dimension(400, 600));
        client.setVisible(true);
        client.setDefaultCloseOperation(EXIT_ON_CLOSE);
        client.inputField.requestFocus();
    }

    public class NewCommandListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String command = ClientGui.this.inputField.getText();
            ClientGui.this.inputField.setText("");
            if (ClientGui.this.client != null) {
                ClientGui.this.client.processCommand(command);
            } else {
                ClientGui.this.errorArea.append("Not authorized\n");
            }
        }
    }

    public class ClientWithGui extends Client {
        public ClientWithGui(String login) {
            super(login);
        }

        @Override
        public void print(String s) {
            synchronized (ClientGui.this.messageArea) {
                ClientGui.this.messageArea.append(s + '\n');
            }
        }

        @Override
        public void log(String s) {
            synchronized (ClientGui.this.errorArea) {
                ClientGui.this.errorArea.append(s + '\n');
            }
        }

        @Override
        public void deleteServer(String serverName) {
            super.deleteServer(serverName);
            ClientGui.this.serversListModel.removeElement(serverName);
        }

        @Override
        public void addServer(String serverName) {
            super.addServer(serverName);
            ClientGui.this.serversListModel.addElement(serverName);
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    createGUI();
                }
            });
    }

    public class AuthorizedActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getSource().equals(ClientGui.this.loginButton)) {
                String login = ClientGui.this.loginField.getText();
                if (login == null || login.length() == 0) {
                    ClientGui.this.errorArea.append("Incorrect name\n");
                } else {
                    if (client != null) {
                        ClientGui.this.client.log("Already authorized");
                    } else {
                        client = new ClientWithGui(login);
                        loginButton.setEnabled(false);
                        loginField.setEnabled(false);
                    }
                }
            }
        }
    }

    public class NewConnectionActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getSource().equals(ClientGui.this.newConnectionButton)) {
                if (ClientGui.this.client != null) {
                    ClientGui.this.client.processCommand("/connect " + ClientGui.this.newConnectionField.getText());
                    ClientGui.this.servers.setSelectedIndex(ClientGui.this.serversListModel.size() - 1);
                } else {
                    ClientGui.this.errorArea.append("Not authorized\n");
                }
            }
        }
    }

    public class MyListSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent listSelectionEvent) {
            ClientGui.this.client.setActiveServer(ClientGui.this.serversListModel.elementAt(ClientGui.this.servers.getSelectionModel().getMinSelectionIndex()));
        }
    }

}

