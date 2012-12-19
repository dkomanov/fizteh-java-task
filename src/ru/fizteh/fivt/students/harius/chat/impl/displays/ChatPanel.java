/*
 * ChatPanel.java
 * Dec 15, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat.impl.displays;

import javax.swing.*;
import java.awt.*;

public class ChatPanel extends JPanel {
    public ChatPanel() {
        setLayout(new BorderLayout(5, 5));
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
        add(bottom, BorderLayout.SOUTH);
        JTextField field = new JTextField();
        bottom.add(field);
        JButton send = new JButton("Send");
        send.setBackground(new Color(140, 140, 250));
        send.setForeground(Color.WHITE);
        bottom.add(send);
        JPanel right = new JPanel();
        // right.setBackground(Color.GREEN);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        JButton connect = new JButton("Connect");
        connect.setBackground(Color.GREEN.darker());
        connect.setForeground(Color.WHITE);
        right.add(connect);
        right.add(Box.createVerticalStrut(10));
        ListPanel list = new ListPanel();
        for (int i = 0; i < 10; ++i)
        {
            JButton red = new JButton("x");
            red.setBackground(Color.PINK.darker());
            red.setForeground(Color.WHITE);
            list.addRow(new JLabel("server " + i), Box.createHorizontalGlue(), red);
        }
        right.add(list);        
        add(right, BorderLayout.EAST);
        JPanel center = new JPanel();
        center.setLayout(new GridLayout(1, 1));
        JTextArea edit = new JTextArea();
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
}