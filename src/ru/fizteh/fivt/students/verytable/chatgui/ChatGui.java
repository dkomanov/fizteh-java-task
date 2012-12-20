package ru.fizteh.fivt.students.verytable.chatgui;

import ru.fizteh.fivt.students.verytable.chat.Client;
import ru.fizteh.fivt.students.verytable.chat.MessageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class ChatGui extends JFrame implements ActionListener {

    private JLabel label;
    private JTextField tf;
    private JTextField tfServer, tfPort;
    private JButton login, logout;
    private JTextArea ta;
    private boolean connected;
    private Client client;
    private int defaultPort;
    private String defaultHost;
    private String username;

    ChatGui(String host, int port) {

        super("Chat Client");
        defaultPort = port;
        defaultHost = host;
        JPanel northPanel = new JPanel(new GridLayout(3, 1));
        JPanel serverAndPort = new JPanel(new GridLayout(1, 5, 1, 3));
        tfServer = new JTextField(host);
        tfPort = new JTextField("" + port);
        tfPort.setHorizontalAlignment(SwingConstants.RIGHT);
        serverAndPort.add(new JLabel("Server Address:  "));
        serverAndPort.add(tfServer);
        serverAndPort.add(new JLabel("Port Number:  "));
        serverAndPort.add(tfPort);
        serverAndPort.add(new JLabel(""));
        northPanel.add(serverAndPort);
        label = new JLabel("Please, enter your username below.", SwingConstants.CENTER);
        northPanel.add(label);
        tf = new JTextField("Anonymous");
        tf.setBackground(Color.WHITE);
        northPanel.add(tf);
        add(northPanel, BorderLayout.NORTH);
        ta = new JTextArea("Welcome!!!\n", 80, 80);
        JPanel centerPanel = new JPanel(new GridLayout(1, 1));
        centerPanel.add(new JScrollPane(ta));
        ta.setEditable(false);
        add(centerPanel, BorderLayout.CENTER);
        login = new JButton("Login");
        login.addActionListener(this);
        logout = new JButton("Logout");
        logout.addActionListener(this);
        logout.setEnabled(false);
        JPanel southPanel = new JPanel();
        southPanel.add(login);
        southPanel.add(logout);
        add(southPanel, BorderLayout.SOUTH);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(600, 600);
        setVisible(true);
        tf.requestFocus();
    }

    public void append(String str) {
        ta.append(str);
        ta.setCaretPosition(ta.getText().length() - 1);
    }

    public void reset() {
        login.setEnabled(true);
        logout.setEnabled(false);
        label.setText("Please, enter your username below");
        tf.setText("Anonymous");
        tfPort.setText("" + defaultPort);
        tfServer.setText(defaultHost);
        tfServer.setEditable(false);
        tfPort.setEditable(false);
        tf.removeActionListener(this);
        connected = false;
    }

    public void actionPerformed(ActionEvent e) {

        Object o = e.getSource();
        if(o == logout) {
            client.send(client.curSocketChannel, MessageUtils.bye());
            reset();
            return;
        }
        if(connected) {
            client.send(client.curSocketChannel, MessageUtils.message(username, tf.getText()));
            tf.setText("");
            return;
        }

        if (o == login) {
            username = tf.getText().trim();
            if (username.length() == 0) {
                return;
            }
            String host = tfServer.getText().trim();
            if (host.length() == 0) {
                return;
            }
            String portNumber = tfPort.getText().trim();
            if (portNumber.length() == 0) {
                return;
            }
            int port;
            try {
                port = Integer.parseInt(portNumber);
            } catch (Exception en) {
                return;
            }


            System.setErr(new PrintStream(new ErrorOutputStream()));
            String[] arg = new String[1];
            arg[0] = username;
            try {
                client = new Client(host, port, username, this);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
            tf.setText("");
            label.setText("Enter your message below, " + arg[0]);
            connected = true;

            login.setEnabled(false);
            logout.setEnabled(true);
            tfServer.setEditable(false);
            tfPort.setEditable(false);
            tf.addActionListener(this);
        }

    }

    public static void main(String[] args) {
        new ChatGui("localhost", 1111);
    }

}

class ErrorOutputStream extends OutputStream {

    private StringBuilder sb = new StringBuilder();

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
