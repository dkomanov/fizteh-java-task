package ru.fizteh.fivt.students.dmitriyBelyakov.userList;

import ru.fizteh.fivt.bind.test.Permissions;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
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
        return ((UserType) v1.get(1)).compareTo((UserType) v2.get(1));
    }
}

class UserNameComparatorRev implements Comparator<Vector<Object>> {
    @Override
    public int compare(Vector<Object> v2, Vector<Object> v1) {
        if (v1.get(2).equals(v2.get(2))) {
            return ((String) v1.get(3)).compareTo((String) v2.get(3));
        } else {
            return ((String) v1.get(2)).compareTo((String) v2.get(2));
        }
    }
}

class UserTypeComparatorRev implements Comparator<Vector<Object>> {
    @Override
    public int compare(Vector<Object> v2, Vector<Object> v1) {
        return ((UserType) v1.get(1)).compareTo((UserType) v2.get(1));
    }
}

public class UserList extends JFrame {
    private JMenuBar menu;
    private JFrame frame = this;
    private JTable table;
    private XmlUserList xmlUserList;
    private File xmlFile;
    private Vector<Vector<Object>> users;
    private boolean namesSortedAscending;
    private boolean typesSortedAscending;

    UserList() {
        super("UserList");
        namesSortedAscending = false;
        typesSortedAscending = false;
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
            t.printStackTrace();
            System.exit(1);
        }
    }

    public class Listener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            String actionCommand = event.getActionCommand();
            if (actionCommand.equals("OPEN")) {
                JFileChooser fileOpen = new JFileChooser();
                int ret = fileOpen.showDialog(frame, "Open");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    xmlFile = fileOpen.getSelectedFile();
                    if (!xmlFile.exists()) {
                        JOptionPane.showMessageDialog(frame, "Cannot find file '" + xmlFile.getName() + "'");
                        xmlFile = null;
                    } else {
                        updateTable(xmlUserList.loadUsers(xmlFile));
                    }
                }
                sortByNames();
                sortByTypes();
            } else if (actionCommand.equals("SAVE")) {
                try {
                    save();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, "Incorrect list: " + e.getMessage());
                }
            } else if (actionCommand.equals("SAVE_AS")) {
                File last = xmlFile;
                JFileChooser fileSave = new JFileChooser();
                int ret = fileSave.showDialog(frame, "Save as");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    xmlFile = fileSave.getSelectedFile();
                    try {
                        save();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(frame, "Incorrect list: " + e.getMessage());
                        xmlFile = last;
                    }
                }
            } else if (actionCommand.equals("SORT_NAME")) {
                sortByNames();
            } else if (actionCommand.equals("SORT_TYPE")) {
                sortByTypes();
            } else if (actionCommand.equals("NEW_USER")) {
                Vector<Object> vector = new Vector<>();
                vector.add(0);
                vector.add(new String());
                vector.add(new String());
                vector.add(new String());
                vector.add(false);
                vector.add(0);
                users.add(vector);
                sortByNames();
                sortByTypes();
                menu.updateUI();
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
                UserType userType = (UserType) vector.get(1);
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
                row.add(user.getUserType() == null ? UserType.USER : user.getUserType());
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

    public Listener newListener() {
        return new Listener();
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
        JMenu sortedByNames = new JMenu("");
        menu.add(sortedByNames);
        JMenu sortedByTypes = new JMenu("");
        menu.add(sortedByTypes);
        setJMenuBar(menu);
    }

    private void sortByNames() {
        if (!namesSortedAscending) {
            Collections.sort(users, new UserNameComparator());
            namesSortedAscending = true;
        } else {
            Collections.sort(users, new UserNameComparatorRev());
            namesSortedAscending = false;
        }
        menu.getMenu(3).setText("Names sorted: " + (namesSortedAscending ? "ascending." : "descending."));
        menu.updateUI();
        table.updateUI();
    }

    private void sortByTypes() {
        if (!typesSortedAscending) {
            Collections.sort(users, new UserTypeComparator());
            typesSortedAscending = true;
        } else {
            Collections.sort(users, new UserTypeComparatorRev());
            typesSortedAscending = false;
        }
        menu.getMenu(4).setText("Types sorted: " + (typesSortedAscending ? "ascending." : "descending."));
        menu.updateUI();
        table.updateUI();
    }

    private void createTable() {
        Vector<String> names = new Vector<>();
        names.add("ID");
        names.add("Type");
        names.add("First name");
        names.add("Last name");
        names.add("Root");
        names.add("Quota");
        UserType[] types = UserType.values();
        JComboBox typeCombo = new JComboBox(types);
        final DefaultCellEditor editor = new DefaultCellEditor(typeCombo);
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
                        return UserType.class;
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

            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                int modelColumn = convertColumnIndexToModel(column);

                if (modelColumn == 1)
                    return editor;
                else
                    return super.getCellEditor(row, column);
            }
        };
        add(new JScrollPane(table));
    }
}