package ru.fizteh.fivt.students.yushkevichAnton.chat.gui;

import ru.fizteh.fivt.students.yushkevichAnton.chat.client.Client;
import ru.fizteh.fivt.students.yushkevichAnton.chat.gui.dialogs.ConnectDialog;
import ru.fizteh.fivt.students.yushkevichAnton.chat.gui.dialogs.SelectConnectionDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;

public class ClientGraphicalUserInterface {
    private JTextArea chatTextArea;
    private JTextField inputTextField;
    private JButton sendButton;
    private JPanel panel;
    private JTextArea logTextArea;
    private JMenuBar menuBar = new JMenuBar();

    public ClientGraphicalUserInterface() {
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = inputTextField.getText();
                inputTextField.setText("");

                sendMessage(command);
            }
        });
        inputTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    sendButton.doClick();
                }
            }
        });

        JMenu menu = new JMenu("Actions");
        menuBar.add(menu);

        JMenuItem menuItem = new JMenuItem("Connect");
        menu.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ConnectDialog(client);
            }
        });

        menuItem = new JMenuItem("Disconnect");
        menu.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.processCommand("/disconnect");
            }
        });

        menuItem = new JMenuItem("Where am I?");
        menu.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.processCommand("/whereami");
            }
        });

        menu.addSeparator();

        menuItem = new JMenuItem("List connections");
        menu.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.processCommand("/list");
            }
        });

        menuItem = new JMenuItem("Select connection");
        menu.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SelectConnectionDialog(client);
            }
        });

        menu.addSeparator();

        menuItem = new JMenuItem("Exit");
        menu.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.processCommand("/exit");
            }
        });
    }

    private Client client;

    private void sendMessage(String s) {
        if (s.startsWith("/")) s = " " + s;
        client.processCommand(s);
    }

    private PrintStream textPrintStream = new PrintStream(new OutputStream() {
        @Override
        public void write(int b) throws IOException {
            synchronized (this) {
                chatTextArea.append(new String(new char[] {(char) b}));
            }
        }
    }, true);

    private PrintStream logPrintStream = new PrintStream(new OutputStream() {
        @Override
        public void write(int b) throws IOException {
            synchronized (this) {
                logTextArea.append(new String(new char[] {(char) b}));
            }
        }
    }, true);

    public static void main(String[] args) {
        JFrame frame = new JFrame("Graphical User Interface");
        ClientGraphicalUserInterface clientGraphicalUserInterface = new ClientGraphicalUserInterface();

        frame.setJMenuBar(clientGraphicalUserInterface.menuBar);

        frame.setContentPane(clientGraphicalUserInterface.panel);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        clientGraphicalUserInterface.client = new Client(clientGraphicalUserInterface.textPrintStream, clientGraphicalUserInterface.logPrintStream, args);
        clientGraphicalUserInterface.client.run();
    }
}