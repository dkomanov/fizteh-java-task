package ru.fizteh.fivt.students.tolyapro.chat.client;

import ru.fizteh.fivt.students.tolyapro.chat.KeyBoard;
import ru.fizteh.fivt.students.tolyapro.chat.MessageUtils;
import ru.fizteh.fivt.students.tolyapro.chat.TextAreaWriter;

import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.jdesktop.layout.*;

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
    
    private boolean ready;

    public GuiClient() {
        generateInterface();
    }
    /* almost auto-generated code */
    public void generateInterface() {
        // Create Frame
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

        jTextArea2.addKeyListener(new KeyBoard(jTextArea2));
        
        jButton6.setText("Connect");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jFrame1Layout = new org.jdesktop.layout.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jFrame1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 131, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jTextField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 65, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jFrame1Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jButton6)
                .add(76, 76, 76))
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jFrame1Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jFrame1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jTextField2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 49, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(93, 93, 93))
        );

        jLabel2.setText("Enter nickname");

        jButton2.setText("ok");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTextField4.setText("user");

        org.jdesktop.layout.GroupLayout jDialog1Layout = new org.jdesktop.layout.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jDialog1Layout.createSequentialGroup()
                .addContainerGap(116, Short.MAX_VALUE)
                .add(jDialog1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jDialog1Layout.createSequentialGroup()
                        .add(jButton2)
                        .add(165, 165, 165))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jDialog1Layout.createSequentialGroup()
                        .add(jDialog1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 136, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jTextField4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 155, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(96, 96, 96))))
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jDialog1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jTextField4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 7, Short.MAX_VALUE)
                .add(jButton2))
        );

        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jButton1.setText("Connect");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton4.setText("Exit");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "No server" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);

        jTextArea3.setEditable(false);
        jTextArea3.setColumns(20);
        jTextArea3.setForeground(new java.awt.Color(255, 60, 60));
        jTextArea3.setRows(5);
        jTextArea3.setFocusable(false);
        jScrollPane3.setViewportView(jTextArea3);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(frame.getContentPane());
        frame.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
                    .add(jComboBox1, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1)
                    .add(jButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 105, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .add(jButton4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 49, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 49, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 41, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 190, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(jScrollPane3)
                        .addContainerGap())))
        );

        layout.linkSize(new java.awt.Component[] {jButton1, jButton4}, org.jdesktop.layout.GroupLayout.VERTICAL);

        frame.pack();
        frame.setVisible(true);
        jDialog1.setVisible(true);
        jDialog1.setModal(true);
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
            //client.connect(host, port);
            chatClient.connect(host, port);
            jComboBox1.addItem(name);
            jComboBox1.setSelectedItem(name);
        } catch (Exception e) {
            jTextArea3.append("Error " + e.getMessage()+'\n');
            jTextArea3.setCaretPosition(jTextArea3.getDocument().getLength());
        }
        jFrame1.setAlwaysOnTop(false);
        jFrame1.setVisible(false);
        jFrame1.setEnabled(false);
    }
    
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
        //#TODO close everything
        System.exit(0);
    }
    
    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {
        String name = (String) jComboBox1.getSelectedItem();
        jTextArea1.setText(""); // clear
        if (!name.equals("No server")) {
            String[] arg = name.split(":");
            chatClient.use(arg[0], arg[1]);
            System.out.println("reloading history" + chatClient.getHistory(name));
            jTextArea1.setText(chatClient.getHistory(name));
        } else {
            chatClient.disconnectFromActive();
        }
    }
    
    public static void sendMyMessage(String string) {
        try {
            //sendMessage(string);
            chatClient.sendMessageFromConsole(new String(MessageUtils
                    .message(nick, string)));
        } catch (IOException e) {
            printError(e.getMessage());
        }
        jTextArea1.append(nick + ":" + string + '\n');
        chatClient.updateHistory((String) jComboBox1.getSelectedItem(), nick + ":" + string);
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
