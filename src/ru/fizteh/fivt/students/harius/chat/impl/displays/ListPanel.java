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

public class ListPanel extends JPanel {
    java.util.List<JPanel> rows = new ArrayList<>();

    public ListPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    public void addRow(Component... components) {
        JPanel row = new JPanel();
        if (rows.size() % 2 == 0) {
            row.setBackground(new Color(200, 200, 230));
        }
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        if (rows.size() == 3) {
            row.setBackground(new Color(170, 250, 170));
        }
        row.add(Box.createHorizontalStrut(3));    
        for (Component component : components) {
            row.add(component);
        }
        row.add(Box.createHorizontalStrut(3)); 
        rows.add(row);
        add(row);
        add(Box.createVerticalStrut(5));
    }
}