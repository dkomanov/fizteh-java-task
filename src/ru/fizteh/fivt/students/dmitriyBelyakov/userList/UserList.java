package ru.fizteh.fivt.students.dmitriyBelyakov.userList;

import ru.fizteh.fivt.bind.test.Permissions;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;

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
                String fileName = JOptionPane.showInputDialog("Enter file name.");
                if (fileName != null) {
                    xmlFile = new File(fileName);
                    if (!xmlFile.exists()) {
                        JOptionPane.showMessageDialog(frame, "Cannot find file '" + fileName + "'");
                        xmlFile = null;
                    } else {
                        updateTable(xmlUserList.loadUsers(xmlFile));
                    }
                }
            } else if (event.getActionCommand().equals("SAVE")) {
                save();
            } else if (event.getActionCommand().equals("SAVE_AS")) {
                String fileName = JOptionPane.showInputDialog("Enter file name.");
                if (fileName != null && !fileName.equals("")) {
                    xmlFile = new File(fileName);
                    save();
                }
            }
        }

        public void save() {
            if (xmlFile == null) {
                JOptionPane.showMessageDialog(frame, "File for save don't open.");
                return;
            }
            ArrayList<User> usersList = new ArrayList<>();
            for (Vector<Object> vector : users) {
                int id = Integer.parseInt((String) vector.get(0));
                UserType userType = UserType.valueOf((String) vector.get(1));
                if (userType == null) {
                    throw new RuntimeException("Incorrect user type.");
                }
                UserName name = new UserName((String) vector.get(2), (String) vector.get(3));
                Permissions permissions = new Permissions();
                User user = new User(id, userType, name, permissions);
                usersList.add(user);
            }
            xmlUserList.saveUsers(usersList, xmlFile);
        }

        public void updateTable(ArrayList<User> list) {
            users.clear();
            for (User user : list) {
                Vector row = new Vector();
                row.add(Integer.toString(user.getId()));
                row.add(user.getUserType().toString());
                row.add(user.getName().getFirstName());
                row.add(user.getName().getLastName());
                row.add(Boolean.toString(user.getPermissions().isRoot()));
                row.add(Integer.toString(user.getPermissions().getQuota()));
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
        fileSave.setActionCommand("SAVE");
        fileSave.addActionListener(listener);
        file.add(fileSave);
        JMenuItem fileSaveAs = new JMenuItem("Save as");
        fileSaveAs.setActionCommand("SAVE_AS");
        fileSaveAs.addActionListener(listener);
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
        table = new JTable(new DefaultTableModel(
                users,
                names
        ));
        add(new JScrollPane(table));
    }
}