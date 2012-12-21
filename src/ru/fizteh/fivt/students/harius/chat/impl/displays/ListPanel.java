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
    java.util.List<Component> struts = new ArrayList<>();

    public ListPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    public void addRow(Component... components) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.add(Box.createHorizontalStrut(3));    
        for (Component component : components) {
            row.add(component);
        }
        row.add(Box.createHorizontalStrut(3)); 
        rows.add(row);
        Component strut = Box.createVerticalStrut(5);
        struts.add(strut);
        add(row);
        add(strut);
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