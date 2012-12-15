package ru.fizteh.fivt.students.fedyuninV.userList;

import org.w3c.dom.Document;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;
import ru.fizteh.fivt.students.fedyuninV.bind.XmlBinder;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class UserList extends JFrame {
    private JMenuBar menu;
    private JTable table;
    private JFrame myFrame;
    private XmlBinder<User> binder;
    private File currFile;
    private JScrollPane scrollPane;

    public UserList() {
        currFile = null;
        binder = new XmlBinder<User>(User.class);
        initTable();
        initMenu();
        scrollPane = new JScrollPane(table);
        add(scrollPane);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        myFrame = this;
        setVisible(true);
    }

    static class NameIncComparator implements Comparator<User> {
        @Override
        public int compare(User user, User user1) {
            UserName x = user.getName();
            UserName y = user1.getName();
            if (x == null) {
                return -1;
            }
            if (y == null) {
                return  1;
            }
            if (x.getFirstName() == null) {
                return -1;
            }
            if (y.getFirstName() == null) {
                return 1;
            }
            int firstNameCompareResult = x.getFirstName().compareTo(y.getFirstName());
            if (firstNameCompareResult == 0) {
                if (x.getLastName() == null) {
                    return -1;
                }
                if (y.getLastName() == null) {
                    return 1;
                }
                return x.getLastName().compareTo(y.getLastName());
            }
            return firstNameCompareResult;
        }
    }

    static class NameDecComparator implements Comparator<User> {
        @Override
        public int compare(User user, User user1) {
            NameIncComparator nameIncComparator = new NameIncComparator();
            return nameIncComparator.compare(user, user1) * -1;
        }
    }

    private static int getIntFromType (UserType x) {
        switch (x){
            case USER:
                return 0;
            case ADVANCED:
                return 1;
            case MODERATOR:
                return 2;
            default:
                return -1;
        }
    }

    static class TypeIncComparator implements Comparator<User> {
        @Override
        public int compare(User user, User user1) {
            UserType x = user.getUserType();
            UserType y = user1.getUserType();
            return getIntFromType(x) - getIntFromType(y);
        }
    }

    static class TypeDecComparator implements Comparator<User> {
        @Override
        public int compare(User user, User user1) {
            TypeIncComparator typeIncComparator = new TypeIncComparator();
            return typeIncComparator.compare(user, user1) * -1;
        }
    }

    private void initTable() {
        table = new JTable(new UserTable()) {
            @Override
            public TableCellEditor getCellEditor(int row, int col) {
                switch (col) {
                    case 3:
                        return ((UserTable) getModel()).userTypeEditor;
                    default:
                        return super.getCellEditor(row, col);
                }
            }
        };
        add(new JScrollPane(table));
    }
    private void initMenu() {
        UserListMenuListener listener = new UserListMenuListener();
        menu = new JMenuBar();

        JMenu file = new JMenu("File");
        JMenuItem fileOpen = new JMenuItem("Open file");
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
        menu.add(file);

        JMenu edit = new JMenu("Edit");
        JMenuItem editAdd = new JMenuItem("Add");
        editAdd.setActionCommand("ADD");
        editAdd.addActionListener(listener);
        edit.add(editAdd);
        JMenuItem editRemove = new JMenuItem("Remove");
        editRemove.setActionCommand("REMOVE");
        editRemove.addActionListener(listener);
        edit.add(editRemove);
        JMenuItem editNameSort = new JMenuItem("Sort by name");
        editNameSort.setActionCommand("SORT_BY_NAME");
        editNameSort.addActionListener(listener);
        edit.add(editNameSort);
        JMenuItem editNameSortDec = new JMenuItem("Sort by name dec");
        editNameSortDec.setActionCommand("SORT_BY_NAME_DEC");
        editNameSortDec.addActionListener(listener);
        edit.add(editNameSortDec);
        JMenuItem editTypeSort = new JMenuItem("Sort by type");
        editTypeSort.setActionCommand("SORT_BY_TYPE");
        editTypeSort.addActionListener(listener);
        edit.add(editTypeSort);
        JMenuItem editTypeSortDec = new JMenuItem("Sort by type dec");
        editTypeSortDec.setActionCommand("SORT_BY_TYPE_DEC");
        editTypeSortDec.addActionListener(listener);
        edit.add(editTypeSortDec);
        menu.add(edit);
        setJMenuBar(menu);
    }

    class UserListMenuListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String command = actionEvent.getActionCommand();
            if (command.equals("OPEN")) {
                JFileChooser openFile = new JFileChooser();
                int result = openFile.showDialog(myFrame, "Open file");
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = openFile.getSelectedFile();
                    try {
                        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
                        ((UserTable) table.getModel()).setData(binder.getUserList(document));
                        currFile = file;
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(myFrame, "Can't open " + file.getName());
                    }
                }
            } else if (command.equals("SAVE")) {
                if (currFile == null) {
                    JOptionPane.showMessageDialog(myFrame, "You should open file at first");
                } else {
                    try {
                        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                        binder.writeUserList(document, ((UserTable) table.getModel()).getUserList());
                        TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(currFile));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(myFrame, "Failed in saving file");
                    }
                }
            } else if (command.equals("SAVE_AS")) {
                JFileChooser openFile = new JFileChooser();
                int result = openFile.showDialog(myFrame, "Save to file");
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = openFile.getSelectedFile();
                    try {
                        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                        binder.writeUserList(document, ((UserTable) table.getModel()).getUserList());
                        if(!file.exists()) {
                            file.createNewFile();
                        }
                        TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(file));
                        currFile = file;
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(myFrame, ex.getMessage() + "Can't save to " + file.getName());
                    }
                }
            } else if (command.equals("ADD")) {
                ((UserTable) table.getModel()).addRow();
                JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
                scrollBar.setValue(scrollBar.getMaximum());
                table.setRowSelectionInterval(table.getRowCount() - 1, table.getRowCount() - 1);
            } else if (command.equals("REMOVE")) {
                if (table.getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(myFrame, "Select user before remove");
                } else {
                    ((UserTable) table.getModel()).removeRow(table.getSelectedRow());
                }
            } else if (command.equals("SORT_BY_NAME")) {
                List<User> userList = ((UserTable) table.getModel()).getUserList();
                Collections.sort(userList, new NameIncComparator());
                ((UserTable) table.getModel()).setData(userList);
            } else if (command.equals("SORT_BY_NAME_DEC")) {
                List<User> userList = ((UserTable) table.getModel()).getUserList();
                Collections.sort(userList, new NameDecComparator());
                ((UserTable) table.getModel()).setData(userList);
            } else if (command.equals("SORT_BY_TYPE")) {
                List<User> userList = ((UserTable) table.getModel()).getUserList();
                Collections.sort(userList, new TypeIncComparator());
                ((UserTable) table.getModel()).setData(userList);
            } else if (command.equals("SORT_BY_TYPE_DEC")) {
                List<User> userList = ((UserTable) table.getModel()).getUserList();
                Collections.sort(userList, new TypeDecComparator());
                ((UserTable) table.getModel()).setData(userList);
            }
        }
    }

    public static void main(String[] args) {
        UserList userList = new UserList();
    }
}
