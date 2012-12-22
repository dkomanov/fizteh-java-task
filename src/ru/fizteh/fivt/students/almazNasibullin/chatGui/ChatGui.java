package ru.fizteh.fivt.students.almazNasibullin.chatGui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
                        auto.chat.run();
                    }
                }
            }
        } catch (Exception e) {
            System.exit(1);
        }
    }
}

class Chat extends JFrame {
    JButton send;
    JButton connect;
    JButton disconnect;
    JLabel colon;
    JLabel advertisement1;
    JLabel advertisement2;
    JLabel allUsers;
    JList servers;
    JTextField host;
    JTextField port;
    JPanel panel;
    JPanel advertisement;
    JScrollPane serverAreaPane;
    JScrollPane serversAreaPane;
    JScrollPane clientAreaPane;
    JTextArea serverArea;
    JTextArea clientArea;
    DefaultListModel listModel;
    MyInputStream mis = new MyInputStream();
    String curServer = "";
    Client client;
    final Object sync = new Object();
    Map<String, List<String>> history = new TreeMap<String, List<String>>();

    Chat(String s) {
        super("Chat");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(550, 480);
        setResizable(false);

        serverArea = new JTextArea();
        serverArea.setBackground(Color.white);
        serverArea.setEditable(false);
        serverAreaPane = new JScrollPane(serverArea);
        serverAreaPane.setBounds(10, 10, 350, 370);

        clientArea = new JTextArea();
        clientArea.setBackground(Color.lightGray);
        clientArea.setEditable(true);
        clientArea.addKeyListener(new KeyBoard());
        clientAreaPane = new JScrollPane(clientArea);
        clientAreaPane.setBounds(10, 390, 265, 50);

        send = new JButton("send");
        send.addActionListener(new SendActionListener());
        send.setBounds(290, 395, 70, 40);
        
        host = new JTextField("host");
        host.setBackground(Color.white);
        host.setBounds(370, 10, 90, 20);

        colon = new JLabel(":");
        colon.setBounds(467, 10, 10, 20);

        port = new JTextField("port");
        port.setBackground(Color.white);
        port.setBounds(480, 10, 60, 20);

        connect = new JButton("connect");
        connect.addActionListener(new ConnectActionListener());
        connect.setBounds(370, 40, 170, 20);

        disconnect = new JButton("disconnect");
        disconnect.addActionListener(new DisconnectActionListener());
        disconnect.setBounds(370, 75, 170, 20);

        allUsers = new JLabel("servers");
        allUsers.setBounds(430, 115, 170, 20);
        allUsers.setForeground(Color.red);

        listModel = new DefaultListModel();
        servers = new JList(listModel);
        servers.addListSelectionListener(new ServersListSelectionListener());
        servers.setSelectionBackground(Color.GREEN);
        serversAreaPane = new JScrollPane(servers);
        serversAreaPane.setBounds(370, 140, 170, 240);

        advertisement1 = new JLabel("Здесь место для");
        advertisement1.setBounds(15, 5, 150, 20);

        advertisement2 = new JLabel("вашей рекламы!");
        advertisement2.setBounds(15, 30, 150, 20);

        advertisement = new JPanel();
        advertisement.setBounds(370, 390, 170, 50);
        advertisement.setBackground(Color.yellow);
        advertisement.setForeground(Color.blue);
        advertisement.add(advertisement1);
        advertisement.add(advertisement2);
        
        panel = new JPanel();
        panel.setLayout(null);
        panel.add(clientAreaPane);
        panel.add(serverAreaPane);
        panel.add(send);
        panel.add(host);
        panel.add(colon);
        panel.add(port);
        panel.add(connect);
        panel.add(disconnect);
        panel.add(allUsers);
        panel.add(serversAreaPane);
        panel.add(advertisement);
        add(panel);

        System.setOut(new PrintStream(new MyOutputStream(serverArea)));
        System.setIn(mis);
        client = new Client(s);
        setVisible(true);
        repaint();
    }

    public void run() {
        for (;;) {
            synchronized(sync) {
                if (client.isBufReady()) {
                    client.messageSender();
                }
                if (client.isConnected()) {
                    int num = client.getSelectedCount();
                    if (num == 0) {
                        continue;
                    }
                    if (!client.handlerServer()) {
                        listModel.remove(listModel.indexOf(curServer));
                        history.remove(curServer);
                        serverArea.setText("You are disconnected from " + curServer);
                        serverArea.append("\n");
                        curServer = "";
                    }
                }
            }
        }
    }

    private void sendMessage() {
        String s = clientArea.getText();
        StringTokenizer st = new StringTokenizer(s, " \n\t");
        if (st.hasMoreTokens()) {
            mis.setData(s);
        }
        clientArea.setText("");
    }

    private class KeyBoard implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
            if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                sendMessage();
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {}

        @Override
        public void keyReleased(KeyEvent e) {}
    }

    private class SendActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
           sendMessage();
        }
    }

    private class ConnectActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String hostName = host.getText();
            String portNumber = port.getText();
            
            if (hostName.equals("")) {
                ShowErrorMessage.showErrorMessage("Put host!");
                return;
            }
            if (portNumber.equals("")) {
                ShowErrorMessage.showErrorMessage("Put port!");
                return;
            }
            if (hostName.indexOf(" ") != -1 || hostName.indexOf("\t") != -1) {
                ShowErrorMessage.showErrorMessage("Host includes spaces");
                return;
            }
            if (portNumber.indexOf(" ") != -1 || portNumber.indexOf("\t") != -1) {
                ShowErrorMessage.showErrorMessage("Port includes spaces");
                return;
            }

            synchronized (sync) {
                serverArea.setText("");
                if (client.connect(hostName, portNumber)) {
                    curServer = hostName + ":" + portNumber;
                    listModel.addElement(curServer);
                    history.put(curServer, new ArrayList<String>());
                    servers.setSelectedIndex(listModel.indexOf(curServer));
                } else {
                    servers.clearSelection();
                }
            }
            host.setText("host");
            port.setText("port");
        }
    }

    private class DisconnectActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            history.remove(curServer);
            int index = listModel.indexOf(curServer);
            if (index != -1) {
                listModel.remove(index);
            }
            curServer = "";
            synchronized (sync) {
                serverArea.setText("");
                client.disconnect();
            }
        }
    }

    private class ServersListSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            int selectedIndex = servers.getSelectedIndex();
            if (selectedIndex != -1) {
                curServer = (String)listModel.get(selectedIndex);
                synchronized (sync) {
                    client.use(curServer);
                    serverArea.setText("");
                }
                for (String s : history.get(curServer)) {
                    serverArea.append(s);
                }
            }
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
                if (!curServer.equals("")) {
                    if (history.get(curServer).size() == 100) {
                        history.get(curServer).remove(0);
                    }
                    history.get(curServer).add(text);
                }
                synchronized(sync) {
                    area.append(text);
                }
                sb.setLength(0);
                return;
            }
            sb.append((char) b);
        }
    }
}

class Authorization extends JFrame {
    JTextField nickname;
    JLabel name;
    JButton log;
    JPanel auto;
    Chat chat;
    boolean finish = false;
    final Object sync;

    Authorization() {
        super("Authorization");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(250, 170);
         setResizable(false);

        name = new JLabel("Enter your nickname");
        name.setBackground(Color.darkGray);
        name.setForeground(Color.blue);
        name.setBounds(45, 10, 150, 20);

        nickname = new JTextField();
        nickname.setBackground(Color.white);
        nickname.setBounds(25, 50, 200, 20);
        nickname.addKeyListener(new KeyBoard());

        log = new JButton("log");
        log.addActionListener(new LogActionListener());
        log.setBounds(85, 90, 70, 40);

        auto = new JPanel();
        auto.setLayout(null);
        auto.add(name);
        auto.add(nickname);
        auto.add(log);
        add(auto);

        sync = new Object();
        setVisible(true);
    }

    private void startChat() {
        String s = nickname.getText();
        if (!s.equals("")) {
            if (s.indexOf(" ") != -1) {
                ShowErrorMessage.showErrorMessage("Nickname includes spaces");
                nickname.setText("");
                return;
            }
            if (s.indexOf("\t") != -1) {
                ShowErrorMessage.showErrorMessage("Nickname includes tabs");
                nickname.setText("");
                return;
            }
            if (s.length() > 20) {
                ShowErrorMessage.showErrorMessage("Nickname is too long");
                nickname.setText("");
                return;
            }
            dispose();
            chat = new Chat(s);
            synchronized(sync) {
                finish = true;
            }
        } else {
            ShowErrorMessage.showErrorMessage("Put your nick");
        }
    }

    private class KeyBoard implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                startChat();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {}
    }

    class LogActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            startChat();
        }
    }
}

class ShowErrorMessage {
    public static void showErrorMessage(String error) {
        JFrame jf = new JFrame();
        JOptionPane optionPane = new JOptionPane(error, JOptionPane.ERROR_MESSAGE,
                JOptionPane.DEFAULT_OPTION);
        JDialog dialog = optionPane.createDialog(jf, "Error");
        dialog.setVisible(true);
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
