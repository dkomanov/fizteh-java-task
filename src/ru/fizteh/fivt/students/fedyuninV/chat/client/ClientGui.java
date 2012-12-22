package ru.fizteh.fivt.students.fedyuninV.chat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */

class MessageInputStream extends InputStream {
    private List<Byte> bytes = new ArrayList<>();
    private final Object lock = new Object();

    @Override
    public int read() throws IOException {
        synchronized (lock) {
            if (bytes.size() == 0) {
                return -1;
            } else {
                return bytes.remove(0);
            }
        }
    }

    @Override
    public int available() throws IOException {
        synchronized (lock) {
            return bytes.size();
        }
    }

    public void add(byte[] bytes) {
        synchronized (lock) {
            for(byte b: bytes) {
                this.bytes.add(b);
            }
        }
    }
}

class TextAreaOutputStream extends OutputStream {
    private JTextArea area;
    StringBuilder builder = new StringBuilder();

    public TextAreaOutputStream(JTextArea area) {
        this.area = area;
    }

    @Override
    public void write(int i) throws IOException {
        if (i == '\n') {
            builder.append((char) i);
            area.append(builder.toString());
            builder.setLength(0);
        } else if (i != '\r') {
            builder.append((char) i);
        }
    }
}


public class ClientGui extends JFrame {
    private ChatClient client;

    private JTextField nameField;
    private JButton authorizeButton;

    private JLabel currentServer;
    private JButton exitButton;

    private JTextField serverNameField;
    private JButton connectButton;
    private JComboBox<Object> serverListBox;
    private JButton useButton;

    private JTextArea chatArea;
    private JScrollPane chatScrollPane;
    private JTextArea errArea;
    private JScrollPane errScrollPane;

    private JTextField msgField;
    private JButton sendButton;
    private JButton disconnectButton;

    private boolean authorized;

    public ClientGui() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        client = null;
        authorized = false;
        initFrame();
        try {
            System.in.close();
        } catch (IOException ignored) {
        }
        System.setOut(new PrintStream(new TextAreaOutputStream(chatArea)));
        System.setErr(new PrintStream(new TextAreaOutputStream(errArea)));
    }

    private void initFrame() {
        Container pane = getContentPane();
        pane.setLayout(new GridBagLayout());
        GridBagConstraints content = new GridBagConstraints();

        nameField = new JTextField("Type your name here");
        nameField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                if (keyEvent.getKeyChar() == KeyEvent.VK_ENTER) {
                    authorizeButton.doClick();
                }
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        content.fill = GridBagConstraints.HORIZONTAL;
        content.gridwidth = 2;
        content.gridx = 0;
        content.gridy = 0;
        pane.add(nameField, content);

        authorizeButton = new JButton("Authorize");
        authorizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (authorized) {
                    System.err.println("Already authorized");
                } else {
                    String name = nameField.getText();
                    if (name.equals(null)  ||  name.equals("")  ||  name.lastIndexOf(' ') != -1  ||  name.matches("[ \n\t]*")) {
                        System.err.println("Incorrect name");
                    } else {
                        authorized = true;
                        client = new ChatClient(name);
                        nameField.setEditable(false);
                    }
                }
            }
        });
        content.fill = GridBagConstraints.NONE;
        content.gridwidth = 1;
        content.gridx = 2;
        content.gridy = 0;
        pane.add(authorizeButton, content);

        currentServer = new JLabel("No active active connections");
        currentServer.setHorizontalAlignment(JLabel.CENTER);
        content.fill = GridBagConstraints.CENTER;
        content.gridx = 3;
        content.gridy = 0;
        content.gridwidth = 1;
        pane.add(currentServer, content);

        exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (authorized) {
                    client.execute("/exit", new String[0]);
                }
                setVisible(false);
                dispose();
                System.exit(0);
            }
        });
        content.fill = GridBagConstraints.NONE;
        content.weightx = 0.5;
        content.gridx = 6;
        content.gridy = 0;
        content.gridwidth = 1;
        pane.add(exitButton, content);

        serverNameField = new JTextField();
        serverNameField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                if (keyEvent.getKeyChar() == KeyEvent.VK_ENTER) {
                    connectButton.doClick();
                }
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        content.fill = GridBagConstraints.HORIZONTAL;
        content.gridx = 0;
        content.gridy = 1;
        content.gridwidth = 2;
        pane.add(serverNameField, content);

        connectButton = new JButton("Connect");
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!authorized) {
                    System.err.println("You should authorize before connect");
                } else {
                    client.execute("/connect", new String[]{serverNameField.getText()});
                    String activeServerName = client.getActiveServerName();
                    if (activeServerName != null) {
                        currentServer.setText(activeServerName);
                        chatArea.setText(client.getHistory());
                        chatScrollPane.getVerticalScrollBar().setValue(chatScrollPane.getVerticalScrollBar().getMaximum());
                    } else {
                        currentServer.setText("No active active connections");
                    }
                    setServerListBox();
                }
            }
        });
        content.weightx = 0;
        content.fill = GridBagConstraints.NONE;
        content.gridx = 2;
        content.gridy = 1;
        content.gridwidth = 1;
        pane.add(connectButton, content);

        serverListBox = new JComboBox<>();
        content.fill = GridBagConstraints.HORIZONTAL;
        content.weightx = 0.5;
        content.gridx = 4;
        content.gridy = 1;
        content.gridwidth = 2;
        pane.add(serverListBox, content);

        useButton = new JButton("Use");
        useButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!authorized) {
                    System.err.println("You should authorize before use");
                    return;
                }
                client.execute("/use", new String[]{serverListBox.getSelectedItem().toString()});
                currentServer.setText(client.getActiveServerName());
                chatArea.setText(client.getHistory());
                chatScrollPane.getVerticalScrollBar().setValue(chatScrollPane.getVerticalScrollBar().getMaximum());
                setServerListBox();
            }
        });
        content.weightx = 0;
        content.fill = GridBagConstraints.NONE;
        content.gridx = 6;
        content.gridy = 1;
        content.gridwidth = 1;
        pane.add(useButton, content);

        chatArea = new JTextArea(5, 1);
        chatArea.setEditable(false);
        chatScrollPane = new JScrollPane(chatArea);
        content.fill = GridBagConstraints.BOTH;
        content.weighty = 1;
        content.weightx = 0.5;
        content.gridx = 0;
        content.gridy = 2;
        content.gridwidth = 4;
        content.gridheight = 4;
        pane.add(chatScrollPane, content);

        errArea = new JTextArea(5, 1);
        errArea.setEditable(false);
        errScrollPane = new JScrollPane(errArea);
        content.fill = GridBagConstraints.BOTH;
        content.weightx = 0.5;
        content.gridx = 4;
        content.gridy = 2;
        content.gridwidth = 3;
        content.gridheight = 4;
        pane.add(errScrollPane, content);


        msgField = new JTextField();
        msgField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                if (keyEvent.getKeyChar() == KeyEvent.VK_ENTER) {
                    sendButton.doClick();
                }
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });
        content.fill = GridBagConstraints.BOTH;
        content.weighty = 0;
        content.weightx = 1;
        content.gridx = 0;
        content.gridy = 6;
        content.gridwidth = 4;
        pane.add(msgField, content);

        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!authorized) {
                    System.err.println("You should authorize before send");
                } else {
                    client.execute("", new String[]{msgField.getText()});
                    chatScrollPane.getVerticalScrollBar().setValue(chatScrollPane.getVerticalScrollBar().getMaximum());
                    msgField.setText("");
                }
            }
        });
        content.weightx = 0;
        content.fill = GridBagConstraints.NONE;
        content.gridx = 4;
        content.gridy = 6;
        content.gridwidth = 1;
        pane.add(sendButton, content);

        disconnectButton = new JButton("Disconnect");
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (authorized) {
                    client.execute("/disconnect", new String[0]);
                    String activeServerName = client.getActiveServerName();
                    if (activeServerName != null) {
                        currentServer.setText(activeServerName);
                        chatArea.setText(client.getHistory());
                        chatScrollPane.getVerticalScrollBar().setValue(chatScrollPane.getVerticalScrollBar().getMaximum());
                    } else {
                        currentServer.setText("No active active connections");
                    }
                    setServerListBox();
                } else {
                    System.err.println("You should authorize before disconnect");
                }
            }
        });
        content.fill = GridBagConstraints.NONE;
        content.gridx = 6;
        content.gridy = 6;
        pane.add(disconnectButton, content);

        setSize(800, 600);
        setVisible(true);
    }

    private void setServerListBox() {
        if (client != null) {
            serverListBox.setModel(new DefaultComboBoxModel<Object>(client.getServerList()));
        }
    }

    public static void main(String[] args) {
        new ClientGui();
    }
}
