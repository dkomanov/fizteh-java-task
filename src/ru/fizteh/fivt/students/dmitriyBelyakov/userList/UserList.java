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
import java.util.*;

class UserNameComparator implements Comparator<Vector<Object>> {
    @Override
    public int compare(Vector<Object> v1, Vector<Object> v2) {
        if (v1.get(2).equals(v2.get(2))) {
            return ((String) v1.get(3)).compareTo((String) v2.get(3));
        } else {
            return ((String) v1.get(2)).compareTo((String) v2.get(2));
        }
    }
}

class UserTypeComparator implements Comparator<Vector<Object>> {
    @Override
    public int compare(Vector<Object> v1, Vector<Object> v2) {
        return ((String) v1.get(1)).compareTo((String) v2.get(1));
    }
}

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
            String actionCommand = event.getActionCommand();
            if (actionCommand.equals("OPEN")) {
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
            } else if (actionCommand.equals("SAVE")) {
                try {
                    save();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, "Incorrect list: " + e.getMessage());
                }
            } else if (actionCommand.equals("SAVE_AS")) {
                File last = xmlFile;
                String fileName = JOptionPane.showInputDialog("Enter file name.");
                if (fileName != null && !fileName.equals("")) {
                    xmlFile = new File(fileName);
                    try {
                        save();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(frame, "Incorrect list: " + e.getMessage());
                        xmlFile = last;
                    }
                }
            } else if (actionCommand.equals("SORT_NAME")) {
                Collections.sort(users, new UserNameComparator());
            } else if (actionCommand.equals("SORT_TYPE")) {
                Collections.sort(users, new UserTypeComparator());
            } else if (actionCommand.equals("NEW_USER")) {
                Vector<Object> vector = new Vector<>();
                vector.add(0);
                vector.add(new String());
                vector.add(new String());
                vector.add(new String());
                vector.add(false);
                vector.add(0);
                users.add(vector);
                table.updateUI();
            } else if (actionCommand.equals("DELETE_USER")) {
                int num = table.getSelectedRow();
                if (num == -1) {
                    JOptionPane.showMessageDialog(frame, "No row has been selected.");
                }
                ((DefaultTableModel) table.getModel()).removeRow(num);
            }
        }

        public void save() {
            if (xmlFile == null) {
                JOptionPane.showMessageDialog(frame, "File for save don't open.");
                return;
            }
            ArrayList<User> usersList = new ArrayList<>();
            for (Vector<Object> vector : users) {
                int id = (Integer) vector.get(0);
                UserType userType = UserType.valueOf((String) vector.get(1));
                if (userType == null) {
                    throw new RuntimeException("Incorrect user type.");
                }
                UserName name = new UserName((String) vector.get(2), (String) vector.get(3));
                Permissions permissions = new Permissions();
                permissions.setRoot((Boolean) vector.get(4));
                permissions.setQuota((Integer) vector.get(5));
                User user = new User(id, userType, name, permissions);
                usersList.add(user);
            }
            xmlUserList.saveUsers(usersList, xmlFile);
        }

        public void updateTable(ArrayList<User> list) {
            users.clear();
            for (User user : list) {
                if (user == null) {
                    continue;
                }
                Vector row = new Vector();
                row.add(user.getId());
                row.add(user.getUserType() == null ? new String() : user.getUserType().toString());
                UserName name = user.getName();
                if (name == null) {
                    row.add(new String());
                    row.add(new String());
                } else {
                    row.add(name == null ? new String() : name.getFirstName());
                    row.add(name == null ? new String() : name.getLastName());
                }
                Permissions permissions = user.getPermissions();
                if (permissions == null) {
                    permissions = new Permissions();
                }
                row.add(permissions.isRoot());
                row.add(permissions.getQuota());
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
        JMenu sort = new JMenu("Sort");
        JMenuItem sortName = new JMenuItem("Name");
        sortName.setActionCommand("SORT_NAME");
        sortName.addActionListener(listener);
        sort.add(sortName);
        JMenuItem sortType = new JMenuItem("Type");
        sortType.setActionCommand("SORT_TYPE");
        sortType.addActionListener(listener);
        sort.add(sortType);
        menu.add(sort);
        JMenu edit = new JMenu("Edit");
        JMenuItem editNewUser = new JMenuItem("New user");
        editNewUser.setActionCommand("NEW_USER");
        editNewUser.addActionListener(listener);
        edit.add(editNewUser);
        JMenuItem editDeleteUser = new JMenuItem("Delete user");
        editDeleteUser.setActionCommand("DELETE_USER");
        editDeleteUser.addActionListener(listener);
        edit.add(editDeleteUser);
        menu.add(edit);
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
        )) {
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Integer.class;
                    case 1:
                        return String.class;
                    case 2:
                        return String.class;
                    case 3:
                        return String.class;
                    case 4:
                        return Boolean.class;
                    case 5:
                        return Integer.class;
                    default:
                        return String.class;
                }
            }
        };
        //table.setRowSorter(new TableRowSorter<>(table.getModel()));
        add(new JScrollPane(table));
    }
}