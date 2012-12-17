package ru.fizteh.fivt.students.almazNasibullin.chatGui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.*;
import ru.fizteh.fivt.students.almazNasibullin.chat.client.Client;

/**
 * 15.12.12
 * @author almaz
 */

public class ChatGui {

    public static void main(String[] args) {
        try {
            Authorization auto = new Authorization();
            System.setErr(new PrintStream(new ErrorOutputStream()));
            for (;;) {
                synchronized(auto.sync) {
                    if (auto.finish) {
                        System.setOut(new PrintStream(new MyOutputStream(auto.chat.serverArea)));
                        System.setIn(auto.chat.mis);
                        String[] arg = new String[1];
                        arg[0] = auto.chat.name;
                        Client.main(arg);
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
    MyInputStream mis = new MyInputStream();
    String name;

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

        name = s;
        setVisible(true);
    }

    private class SendActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String s = clientArea.getText();
            if (!s.equals("")) {
                mis.setData(s);
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
                showErrorMessage("Put your nick");
            }
        }
    }

    public void showErrorMessage(String error) {
        JFrame jf = new JFrame();
        JOptionPane optionPane = new JOptionPane(error, JOptionPane.ERROR_MESSAGE,
                JOptionPane.DEFAULT_OPTION);
        JDialog dialog = optionPane.createDialog(jf, "Error");
        dialog.setVisible(true);
    }
}

class MyOutputStream extends OutputStream {
    private JTextArea area;
    private StringBuilder sb = new StringBuilder();

    MyOutputStream(JTextArea area) {
        this.area = area;
    }

    @Override
    public void flush() {}

    @Override
    public void close() {}

    @Override
    public void write(int b) throws IOException {
        if (b == '\r') {
            return;
        }

        if (b == '\n') {
            String text = sb.toString() + "\n";
            area.append(text);
            sb.setLength(0);
            return;
        }
        sb.append((char) b);
    }
}

class ErrorOutputStream extends OutputStream {
    private StringBuilder sb = new StringBuilder();

    ErrorOutputStream() {}

    @Override
    public void flush() {}

    @Override
    public void close() {}

    @Override
    public void write(int b) throws IOException {
        if (b == '\r') {
            return;
        }

        if (b == '\n') {
            String text = sb.toString();
            JFrame jf = new JFrame();
            JOptionPane optionPane = new JOptionPane(text, JOptionPane.ERROR_MESSAGE,
                    JOptionPane.DEFAULT_OPTION);
            JDialog dialog = optionPane.createDialog(jf, "Error");
            dialog.setVisible(true);
            sb.setLength(0);
            return;
        }
        sb.append((char) b);
    }
}

class MyInputStream extends InputStream {
    List<Byte> list = Collections.synchronizedList(new LinkedList<Byte>());
    final Object sync;

    MyInputStream() {
        sync = new Object();
    }

    @Override
    public void close() {}

    @Override
    public int read() throws IOException {
        synchronized(sync) {
            if (list.isEmpty()) {
                return -1;
            }
            byte b = list.remove(0);
            return b;
        }
    }

    @Override
    public int available() throws IOException {
        synchronized(sync) {
            return list.size();
        }
    }

    void setData(String s) {
        synchronized(sync) {
            for (byte b : s.getBytes()) {
                list.add(b);
            }
        }
    }
}
