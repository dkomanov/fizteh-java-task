package ru.fizteh.fivt.students.frolovNikolay.userList;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.IdentityHashMap;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.fizteh.fivt.bind.test.Permissions;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;
import ru.fizteh.fivt.students.frolovNikolay.Closer;
import ru.fizteh.fivt.students.frolovNikolay.xmlBinder.XmlBinder;

class UsersTable extends AbstractTableModel {
    private static final String[] colNames =  {"id", "type", "first name", "last name", "root", "quota"};
    private ArrayList<ArrayList<Object>> usersDatas = new ArrayList<ArrayList<Object>>();

    @Override
    public int getColumnCount() {
        return colNames.length;
    }

    @Override
    public int getRowCount() {
        return usersDatas.size();
    }
    
    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }
    
    @Override
    public Object getValueAt(int row, int col) {
        return usersDatas.get(row).get(col);
    }

    @Override
    public String getColumnName(int col) {
        return colNames[col];
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        usersDatas.get(row).set(col, value);
        fireTableDataChanged();
    }

    public void removeRow(int row) {
        usersDatas.remove(row);
        fireTableDataChanged();
    }

    public void addRow(ArrayList<Object> row) {
        usersDatas.add(row);
        fireTableDataChanged();
    }

    public ArrayList<ArrayList<Object>> getData() {
        return usersDatas;
    }

    public void clear() {
        usersDatas.clear();
        fireTableDataChanged();
    }
}

public class UserList extends JFrame {
    private JTable usersTable;
    private XmlBinder<User> xmlBinder = new XmlBinder<User>(User.class);
    private File currentXmlFile = null;
    private JFrame mainFrame = this;
    
    UserList() {
        super("UserList");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(640, 480);
        buildMenu();
        buildTable();
        setVisible(true);
    }
    
    private class ActionHandler implements ActionListener {
        
        @Override
        public void actionPerformed(ActionEvent event) {
            switch (event.getActionCommand()) {
                case "Open": {
                    JFileChooser fileChooser = new JFileChooser();
                    if (fileChooser.showDialog(mainFrame, "Open") == JFileChooser.APPROVE_OPTION) {
                        currentXmlFile = fileChooser.getSelectedFile();
                        if (!currentXmlFile.exists()) {
                            JOptionPane.showMessageDialog(mainFrame, "Can't open file: " + currentXmlFile.getName());
                            currentXmlFile = null;
                        } else {
                            getUsersFromFile(); 
                        }
                    }
                    break;
                }
                case "Save": {
                    save();
                    break;
                }
                case "Save as": {
                    JFileChooser fileChooser = new JFileChooser();
                    if (fileChooser.showDialog(mainFrame, "Save as") == JFileChooser.APPROVE_OPTION) {
                        File lastXmlFile = currentXmlFile;
                        currentXmlFile = new File(fileChooser.getSelectedFile().getAbsolutePath() + ".xml");
                        if (!save()) {
                            JOptionPane.showMessageDialog(mainFrame, "Can't save as file: " + currentXmlFile.getName()); 
                            currentXmlFile = lastXmlFile;
                        }
                    }
                    break;
                }
                case "Delete": {
                    int currentRow = usersTable.getSelectedRow();
                    if (currentRow == -1) {
                        JOptionPane.showMessageDialog(mainFrame, "Choose user for delet before");
                    } else {
                        ((UsersTable) usersTable.getModel()).removeRow(currentRow);
                        usersTable.updateUI();
                    }
                    break;
                }
                case "Add": {
                    ArrayList<Object> newUser = new ArrayList<Object>();
                    newUser.add(0);
                    newUser.add(UserType.USER);
                    newUser.add(new String());
                    newUser.add(new String());
                    newUser.add(false);
                    newUser.add(0);
                    ((UsersTable) usersTable.getModel()).addRow(newUser);
                    usersTable.updateUI();
                    break;
                }
                default: {
                    // impossible case
                }
            }
        }
        
        public boolean save() {
            if (currentXmlFile == null) {
                JOptionPane.showMessageDialog(mainFrame, "No open files");
                return true;
            }
            try {
                ArrayList<User> savedUsers = new ArrayList<User>();
                for (ArrayList<Object> user : ((UsersTable) usersTable.getModel()).getData()) {
                    if (((String) user.get(2)).isEmpty() || ((String) user.get(3)).isEmpty()) {
                        JOptionPane.showMessageDialog(mainFrame, "Can't save: one of users have empty firstname or lastname");
                    }
                    UserName name = new UserName((String) user.get(2),(String) user.get(3));
                    Permissions perm = new Permissions();
                    perm.setQuota((int) user.get(5));
                    perm.setRoot((boolean) user.get(4));
                    savedUsers.add(new User((int) user.get(0), (UserType) user.get(1), name, perm));
                }
                saveUsers(savedUsers);
            } catch (Throwable error) {
                return false;
            }
            return true;
        }
        
        public void saveUsers(ArrayList<User> savedUsers) {
            FileWriter output = null;
            try {
                output = new FileWriter(currentXmlFile);
                XMLStreamWriter xmlOutput = XMLOutputFactory.newInstance().createXMLStreamWriter(output);
                xmlOutput.writeStartElement("users");
                for (User user : savedUsers) {
                    IdentityHashMap<Object, Object> cycleLinkInterrupter = new IdentityHashMap<Object, Object>();
                    xmlOutput.writeStartElement("user");
                    //xmlBinder.serializeToStream(user, xmlOutput, cycleLinkInterrupter);
                    xmlOutput.writeEndElement();
                }
                xmlOutput.writeEndElement();
            } catch (Throwable error) {
                throw new RuntimeException("Can't save to file: " + currentXmlFile.getName());
            } finally {
                Closer.close(output);
            }
        }
    
        public void getUsersFromFile() {
            if (currentXmlFile == null) {
                throw new RuntimeException("No open files");
            }
            ArrayList<User> users = new ArrayList<User>();
            try {
                Element root = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(currentXmlFile).getDocumentElement();
                if (!root.getTagName().equals("users")) {
                    throw new RuntimeException("Unknown type in file");
                }
                NodeList childNodes = root.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); ++i) {
                    Node node = childNodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE && ((Element) node).getTagName().equals("user")) {
                        //users.add((User) xmlBinder.objectDeserialize((Element) node, User.class));
                    }
                }
            } catch (Throwable expt) {
                throw new RuntimeException("Can't load users from file: " + currentXmlFile.getName());
            }
            ((UsersTable) usersTable.getModel()).clear();
            for (User user : users) {
                if (user == null) {
                    continue;
                }
                ArrayList<Object> row = new ArrayList<Object>();
                row.add(user.getId());
                row.add(user.getUserType() == null ? UserType.USER : user.getUserType());
                UserName name = user.getName();
                row.add((name == null || name.getFirstName() == null) ? new String() : name.getFirstName());
                row.add((name == null || name.getLastName() == null) ? new String() : name.getLastName());
                Permissions permissions = user.getPermissions();
                if (permissions == null) {
                    permissions = new Permissions();
                }
                row.add(permissions.isRoot());
                row.add(permissions.getQuota());
                ((UsersTable) usersTable.getModel()).addRow(row);
            }
            usersTable.updateUI();
        }
    }
    
    private class TableCellRenderer extends DefaultTableCellRenderer {
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
            Component cell = null;
            switch (column) {
            case 0: 
                cell = super.getTableCellRendererComponent(table, (Integer)value, isSelected, hasFocus, row, column);
                break;
            case 1:
                cell = super.getTableCellRendererComponent(table, (UserType)value, isSelected, hasFocus, row, column);
                break;
            case 2:
                cell = super.getTableCellRendererComponent(table, (String)value, isSelected, hasFocus, row, column);
                break;
            case 3:
                cell = super.getTableCellRendererComponent(table, (String)value, isSelected, hasFocus, row, column);
                break;
            case 4:
                cell = super.getTableCellRendererComponent(table, (Boolean)value, isSelected, hasFocus, row, column);
                break;
            case 5:
                cell = super.getTableCellRendererComponent(table, (Integer)value, isSelected, hasFocus, row, column);
                break;
            default:
                // impossible case
            }
            String currentFirstName = (String) usersTable.getValueAt(row, 2);
            String currentLastName = (String) usersTable.getValueAt(row, 3);
            if (usersTable.getSelectedRow() == -1) {
                if (currentFirstName.isEmpty() || currentLastName.isEmpty()) {
                    setBackground(Color.GRAY);
                } else { 
                    setBackground(Color.WHITE);
                }
            } else {
                String selectedFirstName = (String) usersTable.getValueAt(usersTable.getSelectedRow(), 2);
                String selectedLastName = (String) usersTable.getValueAt(usersTable.getSelectedRow(), 3);
                
                if (!selectedFirstName.isEmpty() && !selectedLastName.isEmpty()) {
                    if (currentFirstName.equals(selectedFirstName) && !currentLastName.isEmpty()) {
                        setBackground(Color.CYAN);
                    } else if (currentFirstName.isEmpty() || currentLastName.isEmpty()) {
                        setBackground(Color.GRAY);
                    } else {
                        setBackground(Color.WHITE);
                    }
                } else {
                    if (currentLastName.isEmpty() || currentFirstName.isEmpty()) {
                        setBackground(Color.GRAY);
                    } else {
                        setBackground(Color.WHITE);
                    }
                }
            }
            ((UsersTable) usersTable.getModel()).fireTableCellUpdated(row, column);
            return cell;
        }
    }

    private void buildMenu() {
        ActionHandler actionHandler = new ActionHandler();
        JMenuBar menu = new JMenuBar();
        
        JMenuItem open = new JMenuItem("Open");
        open.setActionCommand("Open");
        open.addActionListener(actionHandler);
        menu.add(open);
        
        JMenuItem save = new JMenuItem("Save");
        save.setActionCommand("Save");
        save.addActionListener(actionHandler);
        menu.add(save);
        
        JMenuItem saveAs = new JMenuItem("Save as");
        saveAs.setActionCommand("Save as");
        saveAs.addActionListener(actionHandler);
        menu.add(saveAs);
        
        JMenuItem add = new JMenuItem("Add");
        add.setActionCommand("Add");
        add.addActionListener(actionHandler);
        menu.add(add);
        
        JMenuItem delete = new JMenuItem("Delete");
        delete.setActionCommand("Delete");
        delete.addActionListener(actionHandler);
        menu.add(delete);
        
        setJMenuBar(menu);
    }
    
    private void buildTable() {
        final DefaultCellEditor editor = new DefaultCellEditor(new JComboBox<>(UserType.values()));
        usersTable = new JTable(new UsersTable()) {
            
            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if (convertColumnIndexToModel(column) == 1) {
                    return editor;
                }
                return super.getCellEditor(row, column);
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
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
        
        for (int i = 0; i < usersTable.getColumnCount(); ++i) {
            usersTable.getColumnModel().getColumn(i).setCellRenderer(new TableCellRenderer());
        }
        ListSelectionModel selectionModel = usersTable.getSelectionModel();
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            
            @Override
            public void valueChanged(ListSelectionEvent event) {
               for (int col = 0; col < usersTable.getColumnCount(); ++col) {
                   usersTable.getColumnModel().getColumn(col).setCellRenderer(new TableCellRenderer());
               }
               for (int row = 0; row < usersTable.getRowCount(); ++row) {
                   for (int col = 0; col < usersTable.getColumnCount(); ++col) {
                       usersTable.getCellRenderer(row, col).getTableCellRendererComponent(usersTable,
                               usersTable.getModel().getValueAt(row, col), row == usersTable.getSelectedRow(),
                               false, row, col);
                   }
               }
            }
        });
        add(new JScrollPane(usersTable));
    }
}
