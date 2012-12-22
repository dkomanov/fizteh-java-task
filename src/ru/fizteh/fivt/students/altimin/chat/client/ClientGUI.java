package ru.fizteh.fivt.students.altimin.chat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.util.ArrayList;


public class ClientGUI extends JFrame {
    static final int WIDTH = 300;

    private JTextArea messageArea;
    private JTextArea errorArea;
    private JTextField inputField;
    private ClientWithGui client;

    private static void createGUI(String login) {
        ClientGUI client = new ClientGUI();
        client.setLayout(new GridBagLayout());
        client.messageArea = new JTextArea();
        client.errorArea = new JTextArea();
        client.inputField = new JTextField();
        client.inputField.addActionListener(client.new NewCommandListener());
        client.messageArea.setEditable(false);
        client.errorArea.setEditable(false);
        client.client = client.new ClientWithGui(login);
        JScrollPane messageScrollPane = new JScrollPane(client.messageArea);
        JScrollPane errorScrollPane = new JScrollPane(client.errorArea);
        messageScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        errorScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 2.5;
        c.weighty = 4;
        client.add(messageScrollPane, c);
        c.fill = GridBagConstraints.BOTH;
        c.gridy += 1;
        c.weighty = 1;
        c.insets = new Insets(10, 0, 0, 0);
        client.add(errorScrollPane, c);
        c.gridy += 1;
        c.weighty = 0;
        c.insets = new Insets(10, 0, 0, 0);
        client.add(client.inputField, c);
        client.setMinimumSize(new Dimension(400, 600));
        client.setVisible(true);
        client.setDefaultCloseOperation(EXIT_ON_CLOSE);
        client.inputField.requestFocus();
    }

    public class NewCommandListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String command = ClientGUI.this.inputField.getText();
            ClientGUI.this.inputField.setText("");
            ClientGUI.this.client.processCommand(command);
        }
    }

    public class ClientWithGui extends Client {
        public ClientWithGui(String login) {
            super(login);
        }

        @Override
        public void print(String s) {
            synchronized (ClientGUI.this.messageArea) {
                ClientGUI.this.messageArea.append(s + '\n');
            }
        }

        @Override
        public void log(String s) {
            synchronized (ClientGUI.this.errorArea) {
                ClientGUI.this.errorArea.append(s + '\n');
            }
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: client <login>");
            System.exit(1);
        }
        final String login = args[0];
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    createGUI(login);
                }
            });
    }
}
