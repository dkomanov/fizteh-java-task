package ru.fizteh.fivt.students.almazNasibullin.userList;

import java.awt.Color;
import java.awt.Component;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;
import ru.fizteh.fivt.bind.test.Permissions;

/**
 * 9.12.12
 * @author almaz
 */

public class UserList extends JFrame {
    private JMenuBar menu;
    private JTable table;
    private File f = null;

    UserList() {
        super("UserList");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(450, 500);
        createMenu();
        createTable();
        setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UserList userList = new UserList();
        } catch (Exception e) {
            System.exit(1);
        }
    }

    public class MenuListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
            if (event.getActionCommand().equals("Open_file")) {
                JFileChooser jfs = new JFileChooser();
                int ret = jfs.showDialog(null, "Open file");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    f = jfs.getSelectedFile();
                    if (!f.exists()) {
                        showErrorMessage("File doesn't exist", "Open file");
                        f = null;
                    } else {
                        updateTable(new ReadUsers().readUsers(f));
                    }
                }
            } else if (event.getActionCommand().equals("Save")) {
                try {
                    if (f == null) {
                        showErrorMessage("No file to save", "Save file");
                    } else {
                        save(f);
                    }
                } catch (Exception e) {
                    showErrorMessage("The list could not be saved", "Save file");
                }
            } else if (event.getActionCommand().equals("Save_As")) {
                JFileChooser jfs = new JFileChooser();
                int ret = jfs.showDialog(null, "Save As");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = jfs.getSelectedFile();
                    if (!fileToSave.exists()) {
                        showErrorMessage("File doesn't exist", "Save As");
                    } else {
                        try {
                            save(fileToSave);
                        } catch (Exception e) {
                            showErrorMessage("The list could not be saved", "Save As");
                        }
                    }
                }
            } else if (event.getActionCommand().equals("Add")) {
                Vector<Object> user = new Vector<Object>();
                user.add(0);
                user.add(UserType.USER);
                user.add("");
                user.add("");
                user.add(false);
                user.add(10);
                ((MyTableModel)table.getModel()).addRow(user);
                table.updateUI();
            } else if (event.getActionCommand().equals("Delete")) {
                int row = table.getSelectedRow();
                if (row == -1) {
                    showErrorMessage("No row is selected", "Delete user");
                } else {
                    ((MyTableModel)table.getModel()).removeRow(row);
                }
                table.updateUI();
            }
        }

        public void save(File f) {
            List<User> users = new ArrayList<User>();
            for (Vector<Object> user : ((MyTableModel) table.getModel()).getData()) {
                int id = (Integer)user.get(0);
                UserType ut = (UserType)user.get(1);
                UserName un = new UserName((String)user.get(2), (String)user.get(3));
                Permissions p = new Permissions();
                p.setRoot((Boolean)user.get(4));
                p.setQuota((Integer)user.get(5));
                User u = new User(id, ut, un, p);
                users.add(u);
            }
            new WriteUsers().writeUsers(users, f);
        }

        public void updateTable(List<User> users) {
            ((MyTableModel)table.getModel()).clear();
            for (User user : users) {
                Vector row = new Vector();
                row.add(user.getId());
                if (user.getUserType() == null) {
                    row.add(UserType.USER);
                } else {
                    row.add(user.getUserType());
                }
                if (user.getName() == null) {
                    row.add("");
                    row.add("");
                } else {
                    row.add(user.getName().getFirstName());
                    row.add(user.getName().getLastName());
                }
                if (user.getPermissions() == null) {
                    row.add(false);
                    row.add(10);
                } else {
                    row.add(user.getPermissions().isRoot());
                    row.add(user.getPermissions().getQuota());
                }
                ((MyTableModel)table.getModel()).addRow(row);
            }
            table.updateUI();
        }

        private void showErrorMessage(String error, String dialogName) {
            JFrame jf = new JFrame();
            JOptionPane optionPane = new JOptionPane(error,
                    JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION);
            JDialog dialog = optionPane.createDialog(jf, dialogName);
            dialog.setVisible(true);
        }
    }

    private void createMenu() {
        MenuListener ml = new MenuListener();
        menu = new JMenuBar();
        
        JMenuItem openFile = new JMenuItem("Open_file");
        menu.add(openFile);
        openFile.addActionListener(ml);
        openFile.setActionCommand("Open_file");

        JMenuItem save = new JMenuItem("Save");
        menu.add(save);
        save.addActionListener(ml);
        save.setActionCommand("Save");

        JMenuItem saveAs = new JMenuItem("Save_As");
        menu.add(saveAs);
        saveAs.addActionListener(ml);
        saveAs.setActionCommand("Save_As");

        JMenuItem add = new JMenuItem("Add");
        menu.add(add);
        add.addActionListener(ml);
        add.setActionCommand("Add");

        JMenuItem delete = new JMenuItem("Delete");
        menu.add(delete);
        delete.addActionListener(ml);
        delete.setActionCommand("Delete");

        setJMenuBar(menu);
    }

    private void createTable() {
        Vector<String> names = new Vector<String>();
        names.add("id");
        names.add("UserType");
        names.add("First name");
        names.add("Last name");
        names.add("Root");
        names.add("Quota");
        final DefaultCellEditor editor = new DefaultCellEditor(new JComboBox(UserType.values()));
        table = new JTable(new MyTableModel(names, new Vector<Vector<Object> >())) {
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if (convertColumnIndexToModel(column) == 1) {
                    return editor;
                } else {
                    return super.getCellEditor(row, column);
                }
            }
        };

        TableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell;
                switch (column) {
                    case 0:
                        cell =  super.getTableCellRendererComponent(table,
                                (Integer)value, isSelected,hasFocus, row, column);
                        break;
                    case 1:
                        cell =  super.getTableCellRendererComponent(table,
                                (UserType)value, isSelected,hasFocus, row, column);
                        break;
                    case 2:
                        cell =  super.getTableCellRendererComponent(table,
                                (String)value, isSelected,hasFocus, row, column);
                        break;
                    case 3:
                        cell =  super.getTableCellRendererComponent(table,
                                (String)value, isSelected,hasFocus, row, column);
                        break;
                    case 4:
                        cell =  super.getTableCellRendererComponent(table,
                                (Boolean)value, isSelected,hasFocus, row, column);
                        break;
                    case 5:
                        cell =  super.getTableCellRendererComponent(table,
                                (Integer)value, isSelected,hasFocus, row, column);
                        break;
                    default:
                        cell =  super.getTableCellRendererComponent(table,
                                (String)value, isSelected,hasFocus, row, column);
                }
                if (table.getSelectedRow() == -1) {
                    setBackground(Color.white);
                } else {
                    if (isSelected) {
                        setBackground(Color.green);
                    } else {
                        String s = (String)table.getModel().getValueAt(row, 2);
                        String selected = (String)table.getModel().getValueAt(table.getSelectedRow(), 2);
                        if (s.equals(selected)) {
                            setBackground(Color.red);
                        } else {
                            setBackground(Color.white);
                        }
                    }
                }
                ((MyTableModel) table.getModel()).fireTableCellUpdated(row, column);
		return cell;
            }
        };

        for (int i = 0; i < table.getColumnCount(); ++i) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        
        ListSelectionModel selModel = table.getSelectionModel();
        selModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = table.getSelectedRow();
                for (int i = 0; i < table.getRowCount(); ++i) {
                    for (int j = 0; j < table.getColumnCount(); ++j) {
                        table.getCellRenderer(i, j).getTableCellRendererComponent
                                    (table, table.getModel().getValueAt(i, j), i == selectedRow,
                                    false, i, j);
                    }
                }
            }
        });
        add(new JScrollPane(table));
    }
}

class MyTableModel extends AbstractTableModel {
    Vector<String> names;
    Vector<Vector<Object> > users;
    final DefaultCellEditor editor;

    MyTableModel(Vector<String> names, Vector<Vector<Object>> users) {
        this.names = names;
        this.users = users;
        editor = new DefaultCellEditor(new JComboBox(UserType.values()));
    }

    @Override
    public int getColumnCount() {
        return names.size();
    }

    @Override
    public int getRowCount() {
        return users.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
        return users.get(row).get(col);
    }

    @Override
    public String getColumnName(int col) {
        return names.get(col);
    }

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
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        users.get(row).set(col, value);
        fireTableDataChanged();
    }

    public void removeRow(int row) {
        users.remove(row);
        fireTableDataChanged();
    }

    public void addRow(Vector<Object> row) {
        users.add(row);
        fireTableDataChanged();
    }

    public Vector<Vector<Object>> getData() {
        return users;
    }

    public void clear() {
        users.clear();
        fireTableDataChanged();
    }
}
