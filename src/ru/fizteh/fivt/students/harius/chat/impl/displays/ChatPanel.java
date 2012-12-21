/*
 * ChatPanel.java
 * Dec 15, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat.impl.displays;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChatPanel extends JPanel implements ActionListener {
    private Gui gui;
    private JButton connect;
    private JTextArea edit;
    private JButton send;
    private JTextField field;
    private ListPanel list;

    public ChatPanel(Gui gui) {
        this.gui = gui;
        setLayout(new BorderLayout(5, 5));
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
        add(bottom, BorderLayout.SOUTH);
        field = new JTextField();
        field.addActionListener(this);
        bottom.add(field);
        send = new JButton("Send");
        send.addActionListener(this);
        send.setBackground(new Color(140, 140, 250));
        send.setForeground(Color.WHITE);
        bottom.add(send);
        JPanel right = new JPanel();
        // right.setBackground(Color.GREEN);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        connect = new JButton("Connect");        
        connect.addActionListener(this);
        connect.setBackground(Color.GREEN.darker());
        connect.setForeground(Color.WHITE);
        right.add(connect);
        right.add(Box.createVerticalStrut(10));
        list = new ListPanel(gui);
        right.add(new JScrollPane(list));        
        add(right, BorderLayout.EAST);
        JPanel center = new JPanel();
        center.setLayout(new GridLayout(1, 1));
        edit = new JTextArea();
        // edit.setEditable(false);
        center.add(edit);
        add(center, BorderLayout.CENTER);
        add(new JPanel(), BorderLayout.WEST);
        add(new JPanel(), BorderLayout.NORTH);
        try {
            UIManager.setLookAndFeel(new javax.swing.plaf.nimbus.NimbusLookAndFeel());
        } catch (Exception ex) {
            System.err.println(ex.getStackTrace());
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == connect) {
            Object address = JOptionPane.showInputDialog(this, "Enter the address: ", 
                "Connect to server", JOptionPane.PLAIN_MESSAGE,
                null, null, "host:port");
            if (address != null) {
                gui.notifyObserver("/connect " + address.toString());
            }
        } else if (event.getSource() == send || event.getSource() == field) {
            String message = field.getText();
            if (!message.isEmpty()) {
                gui.notifyObserver(message);
                field.setText("");
            }
        }
    }

    public void message(String user, String message) {
        // RectanglePainter painter = new RectanglePainter(Color.BLUE.darker());
        int start = edit.getCaretPosition();
        edit.append("<" + user + ">");
        int end = edit.getCaretPosition();
        edit.append(" " + message + "\n");
        // edit.getHighlighter().addHighlight(start, end, painter);
    }

    public void warn(String warn) {
        // RectanglePainter painter = new RectanglePainter(Color.YELLOW.darker());
        int start = edit.getCaretPosition();
        edit.append(warn + "\n");
        int end = edit.getCaretPosition();
        // edit.getHighlighter().addHighlight(start, end, painter);
    }

    public void error(String error) {
        // RectanglePainter painter = new RectanglePainter(Color.RED.darker());
        int start = edit.getCaretPosition();
        edit.append(error + "\n");
        int end = edit.getCaretPosition();
        // edit.getHighlighter().addHighlight(start, end, painter);
    }

    public void addServer(String desc) {
        list.addRow(new JLabel(desc));
    }

    public void removeServer(int index) {
        list.removeRow(index);
    }

    public void changeServerTo(int index) {
        list.selectRow(index);
    }
}