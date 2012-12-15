package ru.fizteh.fivt.students.yuliaNikonova.userList;

import java.awt.Color;
import java.awt.Component;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import ru.fizteh.fivt.bind.test.Permissions;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;

public class UserList extends JFrame implements ListSelectionListener {
    private JMenuBar menu;
    private JFrame frame = this;
    private JTable table;
    private File xmlFile;

    UserList() {
    super("UserList");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setSize(500, 700);
    createMenu();
    createTable();
    table.getSelectionModel().addListSelectionListener(this);
    setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UserList userList = new UserList();
        } catch (Throwable t) {
            System.exit(1);
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == table.getSelectionModel()
            && e.getFirstIndex() >= 0) {
            int row = table.getSelectedRow();
            if (row > -1 && row < table.getRowCount()) {

            for (int i = 0; i < table.getColumnCount(); ++i) {
                table.getColumnModel().getColumn(i)
                    .setCellRenderer(new MyRenderer());
            }
            for (int i = 0; i < table.getRowCount(); ++i) {
                for (int j = 0; j < table.getColumnCount(); ++j) {
                table.getCellRenderer(i, j)
                    .getTableCellRendererComponent(table,
                        table.getModel().getValueAt(i, j),
                        i == row, false, i, j);
                }
            }
            }
        }
    }

    private void createTable() {
        Vector<String> names = new Vector<String>();
        names.add("id");
        names.add("UserType");
        names.add("First name");
        names.add("Last name");
        names.add("Root");
        names.add("Quota");
        final DefaultCellEditor editor = new DefaultCellEditor(new JComboBox(
            UserType.values()));
        table = new JTable(
            new MyTableModel(names, new Vector<Vector<Object>>())) {
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
            if (convertColumnIndexToModel(column) == 1) {
                return editor;
            } else {
                return super.getCellEditor(row, column);
            }
            }
        };

        add(new JScrollPane(table));

        }

        class MyRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

            Component cell;
            switch (column) {
            case 0:
            cell = super.getTableCellRendererComponent(table,
                (Integer) value, isSelected, hasFocus, row, column);
            break;
            case 1:
            cell = super.getTableCellRendererComponent(table,
                (UserType) value, isSelected, hasFocus, row, column);
            break;
            case 2:
            cell = super.getTableCellRendererComponent(table,
                (String) value, isSelected, hasFocus, row, column);
            break;
            case 3:
            cell = super.getTableCellRendererComponent(table,
                (String) value, isSelected, hasFocus, row, column);
            break;
            case 4:
            cell = super.getTableCellRendererComponent(table,
                (Boolean) value, isSelected, hasFocus, row, column);
            break;
            case 5:
            cell = super.getTableCellRendererComponent(table,
                (Integer) value, isSelected, hasFocus, row, column);
            break;
            default:
            cell = super.getTableCellRendererComponent(table,
                (String) value, isSelected, hasFocus, row, column);
            }
            int selectRow = table.getSelectedRow();
            if (selectRow != -1) {
                if (selectRow == row) {
                    setBackground(Color.blue);
                } else {
                    String selectFirstName = (String) table.getValueAt(
                        selectRow, 2);
                    String selectLastName = (String) table.getValueAt(
                        selectRow, 3);
                    String firstName = (String) table.getValueAt(row, 2);
                    String lastName = (String) table.getValueAt(row, 3);
                    if (firstName.equals(selectFirstName)
                        && lastName.equals(selectLastName)) {
                    setBackground(Color.orange);
                    } else {
                    setBackground(Color.white);
                    }
                }
            }
            ((MyTableModel) table.getModel()).fireTableCellUpdated(row, column);
            return cell;
        }
    }

    private void createMenu() {
        MenuListener listener = new MenuListener();

        JMenuBar menu = new JMenuBar();
        JMenu file = menu.add(new JMenu("Menu"));
        JMenuItem addUser = file.add(new JMenuItem("Add user"));
        addUser.setActionCommand("ADD");
        addUser.addActionListener(listener);
        file.addSeparator();

        JMenuItem openFile = file.add(new JMenuItem("Open file"));
        openFile.setActionCommand("OPEN");
        openFile.addActionListener(listener);
        file.addSeparator();

        JMenuItem save = file.add(new JMenuItem("Save"));
        save.setActionCommand("SAVE");
        save.addActionListener(listener);
        file.addSeparator();

        JMenuItem saveAs = file.add(new JMenuItem("Save as"));
        saveAs.setActionCommand("SAVEAS");
        saveAs.addActionListener(listener);
        file.addSeparator();

        JMenuItem delete = file.add(new JMenuItem("Delete"));
        delete.setActionCommand("DELETE");
        delete.addActionListener(listener);
        setJMenuBar(menu);

    }

    private class MenuListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equalsIgnoreCase("OPEN")) {
            JFileChooser jfs = new JFileChooser();
            int ret = jfs.showDialog(null, "Open file");
            if (ret == JFileChooser.APPROVE_OPTION) {
                xmlFile = jfs.getSelectedFile();
                if (!xmlFile.exists()) {
                showError("File doesn't exist", "Open file");
                xmlFile = null;
                } else {
                try {
                    updateTable(new UsersReader().readUsers(xmlFile));
                } catch (Exception expt) {
                    showError(expt.getMessage(), "Open file");
                }
                }
            }

            } else if (e.getActionCommand().equalsIgnoreCase("SAVE")) {
                try {
                    if (xmlFile == null) {
                    showError("nothing to save", "Save file");
                    } else {
                    saveFile(xmlFile);
                    }
                } catch (Exception expt) {
                    showError("Problems with saving " + expt.getMessage(),
                        "Save file");
                }
            } else if (e.getActionCommand().equalsIgnoreCase("SAVEAS")) {
                JFileChooser jfs = new JFileChooser();
                int ret = jfs.showDialog(null, "Save As");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = jfs.getSelectedFile();
                    if (!fileToSave.exists()) {
                    try {
                        fileToSave.createNewFile();
                    } catch (Exception expt) {
                        showError(
                            "Can't save the list " + expt.getMessage(),
                            "Save As");
                    }
                    }
                    try {
                    saveFile(fileToSave);
                    } catch (Exception expt) {
                    showError("Can't save the list " + expt.getMessage(),
                        "Save As");
                    }
                }
            } else if (e.getActionCommand().equalsIgnoreCase("ADD")) {
                Vector<Object> vector = new Vector<Object>();
                vector.add(0);
                vector.add(UserType.USER);
                vector.add(new String());
                vector.add(new String());
                vector.add(false);
                vector.add(0);
                ((MyTableModel) table.getModel()).addRow(vector);
                table.updateUI();
            } else if (e.getActionCommand().equalsIgnoreCase("DELETE")) {
                int row = table.getSelectedRow();
                if (row == -1) {
                    showError("You don't select user", "Delete");
                }
                ((MyTableModel) table.getModel()).removeRow(row);
                table.updateUI();
            }
        }

        private void saveFile(File xmlFile) {
            if (xmlFile == null) {
                showError("File for save don't open.", "Save file");
                return;
            }
            ArrayList<User> usersList = new ArrayList<User>();
            Vector<Vector<Object>> users = ((MyTableModel) table.getModel())
                .getData();
            if (users.isEmpty()) {
                showError("List of users is empty", "Save");
            }
            for (Vector<Object> user : users) {
                int id = (Integer) user.get(0);
                UserType userType = (UserType) user.get(1);
                UserName userName = new UserName((String) user.get(2),
                    (String) user.get(3));
                Permissions permissions = new Permissions();
                permissions.setRoot((Boolean) user.get(4));
                permissions.setQuota((Integer) user.get(5));
                User userUserClass = new User(id, userType, userName,
                    permissions);
                usersList.add(userUserClass);
            }
            UserWriter.writeUsers(usersList, xmlFile);
        }

        private void updateTable(ArrayList<User> users) {
            ((MyTableModel) table.getModel()).clear();
            for (User user : users) {
                if (user == null) {
                    continue;
                }
                Vector row = new Vector();
                row.add(user.getId());
                row.add(user.getUserType() == null ? UserType.USER : user
                    .getUserType());
                UserName name = user.getName();
                if (name == null) {
                    row.add("");
                    row.add("");
                } else {
                    row.add(name == null ? "" : name.getFirstName());
                    row.add(name == null ? "" : name.getLastName());
                }
                Permissions permissions = user.getPermissions();
                if (permissions == null) {
                    permissions = new Permissions();
                }
                row.add(permissions.isRoot());
                row.add(permissions.getQuota());
                ((MyTableModel) table.getModel()).addRow(row);
            }
            table.updateUI();
        }
    }

    private void showError(String errorMessage, String windowName) {
    JOptionPane.showMessageDialog(frame, errorMessage, windowName,
        JOptionPane.ERROR_MESSAGE);

    }
}
