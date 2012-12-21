/*
 * GUI.java
 * Dec 15, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat.impl.displays;

import ru.fizteh.fivt.students.harius.chat.base.DisplayBase;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ListPanel extends JPanel {
    private java.util.List<JPanel> rows = new ArrayList<>();
    private java.util.List<Component> struts = new ArrayList<>();
    private Gui gui;
    private Component puff = Box.createVerticalGlue();

    public ListPanel(Gui gui) {
        this.gui = gui;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(puff);
    }

    public void addRow(Component... components) {
        remove(puff);
        final JPanel row = new JPanel();
        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                gui.notifyObserver("/use " + rows.indexOf(event.getSource()));
            }
        });
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.add(Box.createHorizontalStrut(3)); 
        row.setMinimumSize(new Dimension(10, 30));   
        row.setMaximumSize(new Dimension(1000, 30));
        for (Component component : components) {
            row.add(component);
        }
        JButton red = new JButton("x");
        red.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                int deleted = rows.indexOf(row);
                gui.notifyObserver("/use " + deleted);
                gui.notifyObserver("/disconnect");
            }
        });
        red.setBackground(Color.PINK.darker());
        red.setForeground(Color.WHITE);
        row.add(Box.createHorizontalGlue()); 
        row.add(red);
        rows.add(row);
        Component strut = Box.createVerticalStrut(5);
        struts.add(strut);
        add(row);
        add(strut);
        add(puff);
    }

    public void removeRow(int index) {
        remove(rows.get(index));
        remove(struts.get(index));
        rows.remove(index);
        struts.remove(index);
    }

    public void selectRow(int index) {
        int count = 0;
        for (JPanel row : rows) {
            if (count % 2 == 0) {
                row.setBackground(new Color(200, 200, 230));
            } else {
                row.setBackground(new Color(240, 240, 240));
            }
            ++count;
        }
        if (index != -1) {
            rows.get(index).setBackground(new Color(170, 250, 170));
        }
    }
}