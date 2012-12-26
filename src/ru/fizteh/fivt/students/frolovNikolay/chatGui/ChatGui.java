package ru.fizteh.fivt.students.frolovNikolay.chatGui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ru.fizteh.fivt.students.frolovNikolay.chat.client.Client;

public class ChatGui extends JFrame {
    
    class SpecialOutputStream extends OutputStream {
        private JTextArea output;
        private StringBuffer buffer = new StringBuffer();
        
        public SpecialOutputStream(JTextArea output) {
            this.output = output;
        }
        
        @Override
        public void flush() {}
        
        @Override
        public void close() {}
        
        @Override
        public void write(int b) {
            if (b == '\n') {
                String text = buffer.toString() + "\n";
                if (!currentServer.equals("")) {
                    history.get(currentServer).add(text);
                }
                output.append(text);
                buffer.setLength(0);
            } else {
                buffer.append((char) b);
            }
        }
    }

    class SpecialInputStream extends InputStream {
        
        List<Byte> buffer = new LinkedList<Byte>();
        
        @Override
        public void close() {}
        
        @Override
        public int available() {
            return buffer.size();
        }
        
        @Override
        public int read() {
            if (buffer.isEmpty()) {
                return -1;
            } else {
                byte symbol = buffer.remove(0);
                return symbol;
            }
        }
        
        public void addToBuffer(String text) {
            for (byte iter : text.getBytes()) {
                buffer.add(iter);
            }
        }
    }
    
    private JTextField host = new JTextField("host");
    private JTextField port = new JTextField("port");
    private JLabel errors = new JLabel("Client information");
    private JTextArea errorsStream = new JTextArea();
    private JScrollPane errorsPane = new JScrollPane(errorsStream);
    private JTextArea chatInputStream = new JTextArea();
    private JScrollPane chatInputPane = new JScrollPane(chatInputStream);
    private JTextArea chatOutputStream = new JTextArea();
    private JScrollPane chatOutputPane = new JScrollPane(chatOutputStream);
    private JButton connect = new JButton("Connect");
    private JButton disconnect = new JButton("Disconnect");
    private JButton exit = new JButton("Exit");
    private JLabel connectedServers = new JLabel("Servers");
    private JPanel mainPanel = new JPanel();
    private DefaultListModel<String> serversList = new DefaultListModel<String>();
    private JList<String> servers = new JList<String>(serversList);
    private JScrollPane serversPane = new JScrollPane(servers);
    private JLabel enterNick = new JLabel("Enter your nickname");
    private JTextArea nickInput = new JTextArea();
    private JButton ok = new JButton("ok");
    private JDialog nickQuery = new JDialog();
    private Client client;
    private Map<String, List<String>> history = new TreeMap<String, List<String>>();
    private String currentServer = "";
    private SpecialInputStream inputStream = new SpecialInputStream();
    private final Object sync = new Object();
    
    public ChatGui() {
        super("Chat Graphical User Interface");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(490, 640);
        setVisible(true);
        setResizable(false);
        
        System.setOut(new PrintStream(new SpecialOutputStream(chatOutputStream)));
        System.setIn(inputStream);
        
        buildTextAreas();
        buildButtons();
        buildPanel();
        
        add(mainPanel);
        
        nickQuery.setLayout(new FlowLayout());
        nickQuery.setModal(true);
        nickQuery.add(enterNick);
        nickQuery.add(nickInput);
        nickQuery.add(ok);
        nickQuery.pack();
        nickQuery.setVisible(true);
        
        repaint();
    }
    
    private void outputError(String error) {
        errorsStream.append(error + "\n");
        errorsStream.setCaretPosition(errorsStream.getDocument().getLength());
    }
    
    private class NickHandler implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent event) {
            String nick = nickInput.getText();
            nickQuery.setModal(false);
            nickQuery.dispose();
            try {
                client = new Client(nick);
            } catch (Throwable crush) {
                throw new RuntimeException("Crush: incorrect nickname");
            }
        }
    }
    
    private class ConnectHandler implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent event) {
            currentServer = "";
            String newHost = host.getText();
            String newPort = port.getText();
            host.setText("host");
            port.setText("port");
            currentServer = newHost + ":" + newPort;
            synchronized(sync) {
                chatOutputStream.setText("");
                if (history.containsKey(currentServer)) {
                    outputError("Connect: you are already connected to this server");
                    currentServer = "";
                    servers.clearSelection();
                } else if (client.connect(currentServer)) {
                    history.put(currentServer, new ArrayList<String>());
                    serversList.addElement(currentServer);
                    servers.setSelectedIndex(serversList.indexOf(currentServer));
                } else {
                    outputError("Connect: can't connect to this server");
                    currentServer = "";
                    servers.clearSelection();
                }
            }
        }
    }
    
    private class DisconnectHandler implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent event) {
            synchronized(sync) {
                if (!currentServer.equals("")) {
                    int usedServerIdx = serversList.indexOf(currentServer);
                    if (usedServerIdx != -1) {
                        serversList.remove(usedServerIdx);
                        history.remove(currentServer);
                    }
                } else {
                    outputError("Disconnect: you are not connected to any server");
                    return;
                }
                currentServer = "";
                chatOutputStream.setText("");
                client.disconnect();
            }
        }
    }
    
    private class ExitHandler implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent event) {
            System.exit(0);
        }
    }
    
    private class ServersHandler implements ListSelectionListener {
        
        @Override
        public void valueChanged(ListSelectionEvent event) {
            synchronized(sync) {
                int selected = servers.getSelectedIndex();
                if (selected != -1) {
                    currentServer = (String) serversList.get(selected);
                    client.use(currentServer);
                    chatOutputStream.setText("");
                    for (String iter : history.get(currentServer)) {
                        chatOutputStream.append(iter);
                    }
                }
            }
        }
    }
    
    private class KeyboardHandler implements KeyListener {
        
        @Override
        public void keyTyped(KeyEvent event) {
        }
        
        @Override
        public void keyReleased(KeyEvent event) {
        }
        
        @Override
        public void keyPressed(KeyEvent event) {
            if (event.getKeyChar() == KeyEvent.VK_ENTER) {
                sendMessage();
            }
        }
    }
    
    private void sendMessage() {
        inputStream.addToBuffer(chatInputStream.getText());
        chatInputStream.setText("");
    }

    public void handle() {
        while (true) {
            synchronized(sync) {
                if (client.isBufferReady()) {
                    client.sendMessage();
                }
                if (client.isConnected()) {
                    int msgNumber = client.selectedNumber();
                    if (msgNumber == 0) {
                        continue;
                    }
                    if (!client.checkServer()) {
                        int curServerIdx =  serversList.indexOf(currentServer);
                        if (curServerIdx != -1) {
                            serversList.remove(curServerIdx);
                            history.remove(currentServer);
                        }
                        outputError("You are disconnected from " + currentServer);
                        currentServer = "";
                    }
                }
            }
        }
    }
    
    private void buildTextAreas() {
        
        chatInputStream.setBackground(Color.WHITE);
        chatInputStream.setEditable(true);
        chatInputStream.addKeyListener(new KeyboardHandler());
        chatInputPane.setBounds(0, 540, 480, 100);
        
        chatOutputStream.setBackground(Color.WHITE);
        chatOutputStream.setEditable(false);
        chatOutputPane.setBounds(0, 40, 360, 500);
        
        errors.setBounds(360, 270, 120, 40);
        errorsStream.setBackground(Color.WHITE);
        errorsStream.setEditable(false);
        errorsPane.setBounds(360, 310, 120, 230);
        
        connectedServers.setBounds(360, 0, 120, 40);
        servers.addListSelectionListener(new ServersHandler());
        servers.setBackground(Color.WHITE);
        serversPane.setBounds(360, 40, 120, 230);
        
        nickInput.setColumns(32);
        nickInput.setText("Nick");
        nickInput.setEditable(true);
        nickInput.setVisible(true);
    }
    
    private void buildButtons() {
        
        host.setBounds(0, 0, 90, 20);
        port.setBounds(90, 0, 30, 20);
        
        connect.addActionListener(new ConnectHandler());
        connect.setBounds(0, 20, 120, 20);
        
        disconnect.addActionListener(new DisconnectHandler());
        disconnect.setBounds(120, 0, 120, 40);
        
        exit.addActionListener(new ExitHandler());
        exit.setBounds(240, 0, 120, 40);
        
        ok.addActionListener(new NickHandler());
    }

    private void buildPanel() {
        
        mainPanel.setLayout(null);
        mainPanel.add(chatInputPane);
        mainPanel.add(chatOutputPane);
        mainPanel.add(errorsPane);
        mainPanel.add(serversPane);
        mainPanel.add(connect);
        mainPanel.add(disconnect);
        mainPanel.add(exit);
        mainPanel.add(host);
        mainPanel.add(port);
        mainPanel.add(connectedServers);
        mainPanel.add(errors);
    }
}