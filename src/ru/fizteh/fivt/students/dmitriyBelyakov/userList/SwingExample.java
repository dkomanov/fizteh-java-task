package ru.fizteh.fivt.students.dmitriyBelyakov.userList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SwingExample {
    SwingExample() {
        JFrame frame = new JFrame("Простое приложение Swing...");
        frame.setSize(500, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JMenuBar menu = new JMenuBar();
        JMenu file = new JMenu("File");
        menu.add(file);
        frame.setJMenuBar(menu);
        JLabel label = new JLabel("Hello, world!", SwingConstants.CENTER);
        frame.add(label);
        JTable table = new JTable(
                new DefaultTableModel(
                    new Object[][] {
                            new Object[] {"Amie", 100}
                    }, new String[] {
                        "Name", "Salary"
                    }
                )
        );
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addRow(new Object[] {"New", 100});
        frame.add(new JScrollPane(table));
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SwingExample();
            }
        });
    }
}