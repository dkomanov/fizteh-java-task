package ru.fizteh.fivt.students.tolyapro.userlist;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.sun.jmx.mbeanserver.JmxMBeanServerBuilder;

import ru.fizteh.fivt.bind.test.*;

public class UserList extends JFrame {

    private JFrame frame = this;
    private JTable table;
    private static File file = null;

    UserList() {
        super("Xml User List");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JFrame.setDefaultLookAndFeelDecorated(true);
        setSize(1368, 678);
        createMenu();
        createTable();
        setVisible(true);
    }

    private void upd(User user) {
        Vector<Object> vector = new Vector<>();
        vector.add(user.getId());
        vector.add(user.getUserType());
        vector.add(user.getName().getFirstName());
        vector.add(user.getName().getLastName());
        vector.add(user.getPermissions().isRoot());
        vector.add(user.getPermissions().getQuota());
        ((InteractiveTableModel) table.getModel()).addRow(vector);
        table.updateUI();
    }

    void createMenu() {
        Font font = new Font("Comic Sans MS", Font.PLAIN, 11); // #doesnt work

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenu userMenu = new JMenu("Users");

        userMenu.setFont(font);
        fileMenu.setFont(font);

        JMenuItem addItem = new JMenuItem("add");
        addItem.setFont(font);
        addItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                Vector<Object> vector = new Vector<>();
                vector.add(0);
                vector.add(UserType.USER);
                vector.add(new String());
                vector.add(new String());
                vector.add(false);
                vector.add(0);
                ((InteractiveTableModel) table.getModel()).addRow(vector);
                table.updateUI();
            }
        });
        JMenuItem delItem = new JMenuItem("delete");
        delItem.setFont(font);
        delItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int num = table.getSelectedRow();
                if (num == -1) {
                    JOptionPane.showMessageDialog(frame,
                            "You haven't selected a row");
                }
                ((InteractiveTableModel) table.getModel()).removeRow(num);
                table.updateUI();
            }
        });

        userMenu.add(addItem);
        userMenu.add(delItem);

        JMenuItem openItem = new JMenuItem("Open");
        openItem.setFont(font);
        fileMenu.add(openItem);
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                int ret = jFileChooser.showDialog(null, "Open file");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    file = jFileChooser.getSelectedFile();
                    if (!file.exists()) {
                        System.err.println("File doesn't exist");
                        file = null;
                    } else {
                        try {
                            // System.out.println("good");
                            UserReader userReader = new UserReader(file);
                            ArrayList<User> users = userReader.read();
                            for (int i = 0; i < users.size(); ++i) {
                                upd(users.get(i));
                            }
                        } catch (Exception exp) {
                            System.err.println(exp.getMessage());
                        }
                    }
                }
            }
        });

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.setFont(font);
        fileMenu.add(saveItem);
        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserWriter userWriter = new UserWriter(file);
                ArrayList<User> users = ((InteractiveTableModel) table
                        .getModel()).getData();
                userWriter.write(users);
            }
        });

        JMenuItem saveAsItem = new JMenuItem("Save as");
        saveAsItem.setFont(font);
        saveAsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File last = file;
                JFileChooser fileSave = new JFileChooser();
                int ret = fileSave.showDialog(frame, "Save as");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    file = fileSave.getSelectedFile();
                    ArrayList<User> users = ((InteractiveTableModel) table
                            .getModel()).getData();
                    UserWriter userWriter = new UserWriter(file);
                    userWriter.write(users);
                }

            }
        });
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setFont(font);
        fileMenu.add(exitItem);

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menuBar.add(fileMenu);
        menuBar.add(userMenu);
        frame.setJMenuBar(menuBar);
        // frame.pack();
        // frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    public static void main(String args[]) {
        UserList userList = new UserList();
    }

    private void createTable() {
        Vector<String> names = new Vector<>();
        names.add("ID");
        names.add("Type");
        names.add("First name");
        names.add("Last name");
        names.add("Root");
        names.add("Quota");
        final int columnCount = names.size();
        UserType[] types = UserType.values();
        JComboBox typeCombo = new JComboBox(types);
        final DefaultCellEditor editor = new DefaultCellEditor(typeCombo);
        table = new JTable(new InteractiveTableModel(names,
                new Vector<Vector<Object>>())) {
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                int modelColumn = convertColumnIndexToModel(column);

                if (modelColumn == 1)
                    return editor;
                else
                    return super.getCellEditor(row, column);
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
        };
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(
                table.getModel());
        table.setRowSorter(sorter);
        frame.add(table);
        add(new JScrollPane(table));
    }

    public class InteractiveTableModel extends AbstractTableModel {
        Vector<String> names;
        Vector<Vector<Object>> users;
        final DefaultCellEditor editor;

        InteractiveTableModel(Vector<String> names, Vector<Vector<Object>> users) {
            this.names = names;
            this.users = users;
            UserType[] types = UserType.values();
            JComboBox typeCombo = new JComboBox(types);
            editor = new DefaultCellEditor(typeCombo);
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
        public String getColumnName(int col) {
            return names.get(col);
        }

        @Override
        public Object getValueAt(int row, int col) {
            return users.get(row).get(col);
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

        public ArrayList<User> getData() {
            ArrayList<User> list = new ArrayList<>();
            for (Vector<Object> vector : users) {
                int id = (Integer) vector.get(0);
                UserType userType = (UserType) vector.get(1);
                UserName name = new UserName((String) vector.get(2),
                        (String) vector.get(3));
                Permissions permissions = new Permissions();
                permissions.setRoot((Boolean) vector.get(4));
                permissions.setQuota((Integer) vector.get(5));
                User user = new User(id, userType, name, permissions);
                list.add(user);
            }
            return list;
        }

        public void clear() {
            users.clear();
            fireTableDataChanged();
        }
    }
}
