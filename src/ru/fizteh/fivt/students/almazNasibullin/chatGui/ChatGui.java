package ru.fizteh.fivt.students.almazNasibullin.chatGui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import ru.fizteh.fivt.students.almazNasibullin.chatGui.UtilsGui;
import ru.fizteh.fivt.students.almazNasibullin.chatGui.ChatGuiClient;

/**
 * 15.12.12
 * @author almaz
 */

public class ChatGui {

    public static void main(String[] args) {
        try {
            Authorization auto = new Authorization();
            for (;;) {
                synchronized(auto.sync) {
                    if (auto.finish) {
                        auto.chat.cgc.run();
                    }
                }
            }
        } catch (Exception e) {
            System.exit(1);
        }
    }
}

class Chat extends JFrame {
    JTextArea serverArea;
    JTextArea clientArea;
    JButton send;
    JPanel forServer;
    JPanel forClient;
    ChatGuiClient cgc;

    Chat(String s) {
        super("Chat");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(450, 500);

        serverArea = new JTextArea();
        serverArea.setBackground(Color.white);
        serverArea.setEditable(false);

        forServer = new JPanel(new BorderLayout());
        forServer.add(new JScrollPane(serverArea));
        add(forServer, BorderLayout.CENTER);

        clientArea = new JTextArea();
        clientArea.setBackground(Color.lightGray);
        clientArea.setEditable(true);

        send = new JButton("send");
        send.setPreferredSize(new Dimension(70, 40));
        send.addActionListener(new SendActionListener());

        forClient = new JPanel(new BorderLayout());
        forClient.add(new JScrollPane(clientArea), BorderLayout.CENTER);
        forClient.add(send, BorderLayout.EAST);
        add(forClient, BorderLayout.SOUTH);

        cgc = new ChatGuiClient(s, serverArea);
        setVisible(true);
    }

    private class SendActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String s = clientArea.getText();
            if (!s.equals("")) {
                cgc.handlerConsole(s);
            }
            clientArea.setText("");
        }
    }
}

class Authorization extends JFrame {
        JTextArea nickname;
        JLabel name;
        JButton log;
        JPanel auto;
        Chat chat;
        boolean finish = false;
        final Object sync;

        Authorization() {
            super("Authorization");
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setSize(225, 150);

            name = new JLabel("Enter your nickname");
            name.setBackground(Color.LIGHT_GRAY);
            name.setForeground(Color.blue);

            nickname = new JTextArea();
            nickname.setBackground(Color.white);
            nickname.setEditable(true);

            log = new JButton("log");
            log.setPreferredSize(new Dimension(70, 40));
            log.addActionListener(new LogActionListener());

            BorderLayout bl = new BorderLayout();
            bl.setVgap(5);
            auto = new JPanel(bl);
            auto.add(name, BorderLayout.BEFORE_FIRST_LINE);
            auto.add(new JScrollPane(nickname), BorderLayout.CENTER);
            auto.add(log, BorderLayout.SOUTH);
            add(auto);

            sync = new Object();
            setVisible(true);
        }

        class LogActionListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = nickname.getText();
                if (!s.equals("")) {
                    dispose();
                    chat = new Chat(s);
                    synchronized(sync) {
                        finish = true;
                    }
                } else {
                    UtilsGui.showErrorMessage("Put your nick");
                }
            }
        }
    }
