package ru.fizteh.fivt.students.dmitriyBelyakov.userList;

import ru.fizteh.fivt.bind.test.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

public class UserList extends JFrame {
    private JMenuBar menu;
    private JFrame frame = this;
    private JTable table;
    private XmlUserList xmlUserList;
    private File xmlFile;
    private Vector<Vector<Object>> users;

    private class Listener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            if (event.getActionCommand().equals("OPEN")) {
                String fileName = (String) JOptionPane.showInputDialog("Enter file name.");
                if (fileName != null) {
                    try {
                        xmlFile = new File(fileName);
                        if (!xmlFile.exists()) {
                            JOptionPane.showMessageDialog(frame, "Cannot find file '" + fileName + "'");
                            xmlFile = null;
                        } else {
                            updateTable(xmlUserList.loadUsers(xmlFile));
                        }
                    } finally {
                        xmlFile = null;
                    }
                }
            }
        }

        public void updateTable(ArrayList<User> list) {
            users.clear();
            for (User user : list) {
                Vector row = new Vector();
                row.add(user.getId());
                row.add(user.getUserType());
                row.add(user.getName().getFirstName());
                row.add(user.getName().getLastName());
                row.add(user.getPermissions().isRoot());
                row.add(user.getPermissions().getQuota());
                users.add(row);
            }
            table.updateUI();
        }
    }

    UserList() {
        super("UserList");
        xmlUserList = new XmlUserList();
        users = new Vector<>();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(500, 700);
        createMenu();
        createTable();
        setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UserList userList = new UserList();
        } catch (Throwable t) {
            System.exit(1);
        }
    }

    private void createMenu() {
        Listener listener = new Listener();
        menu = new JMenuBar();
        JMenu file = new JMenu("File");
        menu.add(file);
        JMenuItem fileOpen = new JMenuItem("Open");
        fileOpen.setActionCommand("OPEN");
        fileOpen.addActionListener(listener);
        file.add(fileOpen);
        JMenuItem fileSave = new JMenuItem("Save");
        file.add(fileSave);
        JMenuItem fileSaveAs = new JMenuItem("Save as");
        file.add(fileSaveAs);
        setJMenuBar(menu);
    }

    private void createTable() {
        Vector<String> names = new Vector<>();
        names.add("ID");
        names.add("Type");
        names.add("First name");
        names.add("Last name");
        names.add("Root");
        names.add("Quota");
        users.add(new Vector<Object>());
        table = new JTable(new DefaultTableModel(
                users,
                names
        ));
        add(new JScrollPane(table));
    }
}