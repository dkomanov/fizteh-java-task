package ru.fizteh.fivt.students.yushkevichAnton.chat.client.gui;

import ru.fizteh.fivt.students.yushkevichAnton.chat.client.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;

public class ClientGUI {
    private JTextArea chatTextArea;
    private JTextField inputTextField;
    private JButton sendButton;
    private JPanel panel;
    private JTextArea logTextArea;

    public ClientGUI() {
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = inputTextField.getText();
                chatTextArea.append(command);
                chatTextArea.append("\n");
                inputTextField.setText("");

                send(command);
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
    }

    private Client client;

    private void send(String s) {
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
        JFrame frame = new JFrame("ClientGUI");
        ClientGUI clientGUI = new ClientGUI();
        frame.setContentPane(clientGUI.panel);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        clientGUI.client = new Client(clientGUI.textPrintStream, clientGUI.logPrintStream, args);
        clientGUI.client.run();
    }
}