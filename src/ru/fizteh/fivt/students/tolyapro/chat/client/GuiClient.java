package ru.fizteh.fivt.students.tolyapro.chat.client;

import ru.fizteh.fivt.students.tolyapro.chat.Keyboard;
import ru.fizteh.fivt.students.tolyapro.chat.MessageUtils;
import ru.fizteh.fivt.students.tolyapro.chat.TextAreaWriter;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author tolyapro
 * 
 * @20.12.2012
 */
public class GuiClient {

    private static String nick;

    private JFrame frame; // Main frame
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private static javax.swing.JComboBox jComboBox1;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private static javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField4;

    public GuiClient() {
        generateInterface();
    }

    public void generateInterface() {
        frame = new JFrame("Chat client");
        frame.getContentPane().setLayout(new FlowLayout());
        jFrame1 = new javax.swing.JFrame();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        jDialog1 = new javax.swing.JDialog();
        jLabel2 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jTextField4 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();

        jTextArea2.addKeyListener(new Keyboard(jTextArea2));

        jButton6.setText("Connect");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        frame.setLayout(new FlowLayout());

        jLabel2.setText("Enter nickname");

        jButton2.setText("ok");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTextField4.setText("user");

        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(100);
        jTextArea1.setRows(20);
        jScrollPane1.setViewportView(jTextArea1);

        jButton1.setText("Connect");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        frame.setSize(640, 480);
        JPanel p = new JPanel();
        JPanel pane = new JPanel();
        jComboBox1.setPreferredSize(new Dimension(90, 50));
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.add(p);
        pane.add(jComboBox1);
        pane.add(jScrollPane1);
        pane.add(jScrollPane2);
        frame.add(pane);
        p.setLayout(new FlowLayout());
        p.add(jButton1);
        p.add(jButton4);
        jScrollPane3.setPreferredSize(new Dimension(200, 400));
        frame.add(jScrollPane3);
        jButton4.setText("Exit");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { "No server" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);
        jFrame1.setLayout(new FlowLayout());
        jTextField1.setColumns(20);
        jTextField2.setColumns(8);
        jFrame1.add(jTextField1);
        jFrame1.add(jTextField2);
        jFrame1.add(jButton6);
        jTextArea3.setEditable(false);
        jTextArea3.setColumns(20);
        jTextArea3.setForeground(new java.awt.Color(255, 60, 60));
        jTextArea3.setRows(5);
        jTextArea3.setFocusable(false);
        jScrollPane3.setViewportView(jTextArea3);
        frame.pack();
        frame.setVisible(true);
        jDialog1.setVisible(true);
        jDialog1.setModal(true);
        jDialog1.setLayout(new FlowLayout());
        jDialog1.add(jLabel2);
        jTextField4.setColumns(10);
        jDialog1.add(jTextField4);
        jDialog1.add(jButton2);
        jDialog1.pack();
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        jFrame1.setEnabled(true);
        jFrame1.setAlwaysOnTop(true);
        jFrame1.setVisible(true);
        jFrame1.pack();
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        nick = jTextField4.getText();
        jDialog1.setModal(false);
        jDialog1.dispose();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TextAreaWriter areaWriter = new TextAreaWriter(jTextArea1);
                    TextAreaWriter errorWriter = new TextAreaWriter(jTextArea3);
                    chatClient = new ChatClient(nick, areaWriter, errorWriter);
                } catch (Exception e) {
                    printError(e.getMessage());
                }
            }
        });
        t.start();
    }

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {
        String host = jTextField1.getText();
        String port = jTextField2.getText();
        String name = host + ":" + port;
        try {
            // client.connect(host, port);
            for (int i = 0; i < jComboBox1.getItemCount(); ++i) {
                if (jComboBox1.getItemAt(i).equals(name)) {
                    throw new Exception(
                            "Can't connect to\\n the same server twice");
                }
            }
            chatClient.connect(host, port);
            jComboBox1.addItem(name);
            jComboBox1.setSelectedItem(name);
        } catch (Exception e) {
            jTextArea3.append("Error " + e.getMessage() + '\n');
            jTextArea3.setCaretPosition(jTextArea3.getDocument().getLength());
        }
        jFrame1.setAlwaysOnTop(false);
        jFrame1.setVisible(false);
        jFrame1.setEnabled(false);
    }

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
        // #TODO close everything
        System.exit(0);
    }

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {
        String name = (String) jComboBox1.getSelectedItem();
        jTextArea1.setText(""); // clear
        if (!name.equals("No server")) {
            String[] arg = name.split(":");
            chatClient.use(arg[0], arg[1]);
            System.out.println("reloading history"
                    + chatClient.getHistory(name));
            jTextArea1.setText(chatClient.getHistory(name));
        } else {
            chatClient.disconnectFromActive();
        }
    }

    public static void sendMyMessage(String string) {
        try {
            // sendMessage(string);
            chatClient.sendMessageFromConsole(new String(MessageUtils.message(
                    nick, string)));
        } catch (IOException e) {
            printError(e.getMessage());
        }
        jTextArea1.append(nick + ":" + string + '\n');
        chatClient.updateHistory((String) jComboBox1.getSelectedItem(), nick
                + ":" + string);
        jTextArea1.setCaretPosition(jTextArea1.getDocument().getLength());
    }

    public static void printError(String message) {

    }

    static ChatClient chatClient;

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        GuiClient guiClient = new GuiClient();
    }
}
