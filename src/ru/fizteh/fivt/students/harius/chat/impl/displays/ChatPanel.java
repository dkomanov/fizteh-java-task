/*
 * ChatPanel.java
 * Dec 15, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat.impl.displays;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ChatPanel extends JPanel implements ActionListener {
    private Gui gui;
    private JButton connect;
    private JTextArea edit;
    private JTextArea errors;
    private JButton send;
    private JTextField field;
    private ListPanel list;
    private java.util.List<String> history
        = new ArrayList<>();
    private int current = -1;
    private Font font = new Font(null, Font.BOLD, 24);
    private Font font2 = new Font(null, Font.PLAIN, 20);

    public ChatPanel(Gui gui) {
        this.gui = gui;
        setLayout(new BorderLayout(5, 5));
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
        add(bottom, BorderLayout.SOUTH);
        field = new JTextField();
        field.setFont(font2);
        field.addActionListener(this);
        bottom.add(field);
        bottom.add(Box.createHorizontalStrut(3));
        send = new JButton("Send");
        send.setFont(font);
        send.setPreferredSize(new Dimension(150, 30));   
        send.addActionListener(this);
        send.setBackground(new Color(140, 140, 250));
        send.setForeground(Color.WHITE);
        bottom.add(send);
        bottom.add(Box.createHorizontalStrut(3));
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        connect = new JButton("Connect"); 
        connect.setFont(font); 
        connect.setPreferredSize(new Dimension(250, 30));      
        connect.addActionListener(this);
        connect.setBackground(Color.GREEN.darker());
        connect.setForeground(Color.WHITE);
        right.add(connect);
        right.add(Box.createVerticalStrut(10));
        list = new ListPanel(gui);
        right.add(new JScrollPane(list));        
        add(right, BorderLayout.EAST);
        JTabbedPane center = new JTabbedPane();
        center.setFont(font);
        edit = new JTextArea();
        edit.setFont(font2);
        edit.setEditable(false);
        center.add(new JScrollPane(edit), "Chat");
        errors = new JTextArea();
        errors.setEditable(false);
        errors.setFont(font2);
        center.add(new JScrollPane(errors), "Log");
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
            if (message.isEmpty()) {
                warn("Cannot send empty message");
            } else if (message.startsWith("/")) {
                warn("Cannot use command sequences with GUI");
            } else {
                gui.notifyObserver(message);
                field.setText("");
            }
        }
    }

    public synchronized void message(String user, String message) {
        edit.append("<" + user + ">");
        edit.append(" " + message + "\n");
    }

    public synchronized void warn(String warn) {
        errors.append(warn + "\n\n");
    }

    public synchronized void error(String error) {
        errors.append(error + "\n\n");
    }

    public synchronized void addServer(String desc) {
        list.addRow(new JLabel(desc));
        history.add("");
    }

    public synchronized void removeServer(int index) {
        list.removeRow(index);
        history.remove(index);
    }

    public synchronized void changeServerTo(int index) {
        if (current >=0 && current < history.size()) {
            history.set(current, edit.getText());
        }
        current = index;
        list.selectRow(index);
        if (current != -1) {
            edit.setText(history.get(current));
        }
    }
}