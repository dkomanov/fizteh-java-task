package ru.fizteh.fivt.students.altimin.userlist;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import ru.fizteh.fivt.bind.test.Permissions;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;
import ru.fizteh.fivt.students.altimin.binder.XmlBinder;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * User: altimin
 * Date: 12/14/12
 * Time: 12:56 AM
 */
public class UserList extends JFrame {
    public UserList() {
    }

    private static XmlBinder<User> binder = new XmlBinder<User>(User.class);

    private JTable table;

    private static void createGUI() {
        final UserList userList = new UserList();
        String[] columnNames = new String[fields.length];
        for (int i = 0; i < fields.length; i ++) {
            columnNames[i] = fields[i].get();
        }
        userList.table = new JTable(userList.tableModel);
        CellRenderer cellRenderer = userList.new CellRenderer();
        userList.table.setDefaultRenderer(Object.class, cellRenderer);
        userList.table.setCellSelectionEnabled(true);
        userList.table.setColumnSelectionAllowed(true);
        TableCellSelectionListener listener = userList.new TableCellSelectionListener();
        userList.table.getSelectionModel().addListSelectionListener(listener);
        userList.table.getColumnModel().getSelectionModel().addListSelectionListener(listener);
        JScrollPane scrollPane = new JScrollPane(userList.table);
        userList.table.setFillsViewportHeight(true);
        userList.table.getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "delete pressed");
        userList.table.getActionMap().put("delete pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int deletedRow = userList.table.getSelectedRow();
                if (deletedRow != -1) {
                    userList.tableModel.list.remove(deletedRow);
                    userList.tableModel.fireTableRowsDeleted(deletedRow, deletedRow);
                }
            }
        });
        userList.table.getInputMap().put(KeyStroke.getKeyStroke("INSERT"), "insert pressed");
        userList.table.getActionMap().put("insert pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int newRow = userList.tableModel.list.size();
                userList.tableModel.list.add(userList.new UserRepresentation(new User(0, UserType.USER, null, null)));
                userList.tableModel.fireTableRowsInserted(newRow, newRow);
            }
        });
        userList.add(scrollPane);
        userList.add(userList.new FileChooser(), BorderLayout.SOUTH);
        userList.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        userList.pack();
        userList.setVisible(true);
    }

    static class UserField {
        public String[] path;

        public String get() {
            return path[path.length - 1];
        }

        UserField(String[] path) {
            this.path = path;
        }
    }

    static class UserFieldTree {
        UserFieldTree[] children;

        int nodeIndex;
        String nodeName;

        UserFieldTree(UserFieldTree[] children, int nodeIndex, String nodeName) {
            this.children = children;
            this.nodeIndex = nodeIndex;
            this.nodeName = nodeName;
        }

        UserFieldTree(UserFieldTree[] children, String nodeName) {
            this.children = children;
            this.nodeName = nodeName;
            this.nodeIndex = -1;
        }
    }

    private static UserFieldTree tree;
    private static final UserField[] fields = getFields();

    private static UserFieldTree processXml(Element element, List<UserField> buffer, Stack<String> stack) {
        boolean ok = false;
        stack.add(element.getTagName());
        NodeList nodeList = element.getChildNodes();
        List<UserFieldTree> trees = new ArrayList<UserFieldTree>();
        for (int i = 0; i < nodeList.getLength(); i ++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                trees.add(processXml((Element) node, buffer, stack));
                ok = true;
            }
        }
        if (!ok) {
            int newNodeIndex = buffer.size();
            buffer.add(new UserField(stack.toArray(new String[0])));
            stack.pop();
            return new UserFieldTree(new UserFieldTree[0], newNodeIndex, element.getTagName());
        } else {
            stack.pop();
            return new UserFieldTree(trees.toArray(new UserFieldTree[0]), element.getTagName());
        }
    }

    private static UserField[] getFields() {
        try {
            List<UserField> list = new ArrayList<UserField>();
            User user = new User(1, UserType.USER, new UserName("first", "last"), new Permissions());
            XmlBinder<User> binder = new XmlBinder<User>(User.class);
            String binded = new String(binder.serialize(user));
            StringReader reader = new StringReader(binded);
            InputSource inputSource = new InputSource(reader);
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource);
            Stack<String> stack = new Stack<String>();
            tree = processXml(document.getDocumentElement(), list, stack);
            return list.toArray(new UserField[0]);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize fields of class User", e);
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createGUI();
            }
        });
    }

    private class UserRepresentation {
        public String[] values;

        public UserRepresentation() {
            this.values = new String[fields.length];
        }

        private Element getChildElement(Element element, String name) {
            NodeList nodeList = element.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i ++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element newElement = (Element) node;
                    if (newElement.getTagName().equals(name)) {
                        return newElement;
                    }
                }
            }
            return null;
        }

        private String get(Element element, UserField field) {
            for (int i = 1; i < field.path.length; i ++) { // miss first <User>
                if (element != null) {
                    element = getChildElement(element, field.path[i]);
                }
            }
            if (element == null) {
                return null;
            } else {
                return element.getTextContent();
            }
        }

        public UserRepresentation(User user) {
            this.values = new String[fields.length];
            byte[] bytes = binder.serialize(user);
            try {
                StringReader reader = new StringReader(new String(bytes));
                InputSource inputSource = new InputSource(reader);
                Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource);
                for (int i = 0; i < fields.length; i ++) {
                    this.values[i] = get(document.getDocumentElement(), fields[i]);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to get UserRepresentation from User");
            }
        }

        public UserRepresentation copy() {
            UserRepresentation result = new UserRepresentation();
            for (int i = 0; i < fields.length; i ++) {
                result.values[i] = this.values[i];
            }
            return result;
        }

        public String get(int value) {
            if (!(0 <= value && value < values.length)) {
                return null;
            }
            return values[value];
        }

        private boolean shouldGo(UserFieldTree tree) {
            if (tree.nodeIndex != -1) {
                return values[tree.nodeIndex] != null && values[tree.nodeIndex].length() > 0;
            }
            for (UserFieldTree child: tree.children) {
                if (shouldGo(child)) {
                    return true;
                }
            }
            return false;
        }

        private void go(StringBuilder buffer, UserFieldTree tree) {
            if (shouldGo(tree)) {
                buffer.append("<").append(tree.nodeName).append(">");
                if (tree.nodeIndex != -1) {
                    if (values[tree.nodeIndex] != null) {
                        buffer.append(values[tree.nodeIndex]);
                    }
                }
                for (UserFieldTree child: tree.children) {
                    go(buffer, child);
                }
                buffer.append("</").append(tree.nodeName).append(">");
            }
        }

        public String toXML() {
            StringBuilder buffer = new StringBuilder();
            go(buffer, tree);
            return buffer.toString();
        }

        public boolean isValid() {
            try {
                String xml = toXML();
                binder.deserialize(xml.getBytes());
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }

    private class TableModel extends AbstractTableModel {
        public List<UserRepresentation> list;

        private TableModel() {
            list = new ArrayList<UserRepresentation>();
        }

        @Override
        public int getRowCount() {
            return list.size();
        }

        @Override
        public int getColumnCount() {
            return fields.length;
        }

        @Override
        public Object getValueAt(int i, int j) {
            return list.get(i).get(j);
        }

        @Override
        public void setValueAt(Object newValue, int rowIndex, int columnIndex) {
            if (!newValue.getClass().equals(String.class)) {
                return;
            }
            UserRepresentation oldRepresentation = list.get(rowIndex);
            UserRepresentation newRepresentation = oldRepresentation.copy();
            newRepresentation.values[columnIndex] = (String) newValue;
            if (newRepresentation.isValid()) {
                list.set(rowIndex, newRepresentation);
            } else {
                System.err.println("Failed to change value: incorrect value");
            }
        }

        @Override
        public String getColumnName(int column) {
            return fields[column].get();
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }
    }
    private final TableModel tableModel = new TableModel();

    public class FileChooser extends JPanel implements ActionListener {
        JFileChooser fc;
        JButton openButton;
        JButton saveButton;

        public FileChooser() {
            //super(new BorderLayout());
            fc = new JFileChooser(new File("."));
            openButton = new JButton("Open file");
            openButton.addActionListener(this);
            saveButton = new JButton("Save file");
            saveButton.addActionListener(this);
            add(openButton);
            add(saveButton);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            //Handle open button action.
            if (actionEvent.getSource() == openButton) {
                int returnVal = fc.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    UserList.this.readFromFile(file);
                }
                //Handle save button action.
            } else if (actionEvent.getSource() == saveButton) {
                int returnVal = fc.showSaveDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    UserList.this.saveIntoFile(file);
                }
            }
        }
    }

    public void readFromFile(File file) {
        BufferedInputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            List<Byte> bytes = new ArrayList<Byte>();
            int value;
            while ((value = inputStream.read()) != -1) {
                bytes.add((byte) value);
            }
            byte[] result = new byte[bytes.size()];
            for (int i = 0; i < bytes.size(); i ++) {
                result[i] = bytes.get(i);
            }
            Object[] users = binder.deserializeObjects(result);
            tableModel.list = new ArrayList<UserRepresentation>();
            for (Object user: users) {
                tableModel.list.add(new UserRepresentation((User) user));
            }
            tableModel.fireTableDataChanged();
        } catch (Exception e) {
            System.err.println(e.toString());
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
            }
        }
    }

    public void saveIntoFile(File file) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("<Users>");
        for (UserRepresentation userRepresentation : tableModel.list) {
            buffer.append(userRepresentation.toXML());
        }
        buffer.append("</Users>");
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(buffer.toString());
        } catch (IOException e) {
            System.err.println("Failed to write data in file");
        } finally {
            try {
                fileWriter.close();
            } catch (Exception e) {
            }
        }
    }

    private static final String[] comparisonMethodNames = { "firstName", "secondName" };
    private static final Set<String> comparisonMethods = new HashSet<String>(Arrays.asList(comparisonMethodNames));

    private class CellRenderer extends DefaultTableCellRenderer {
        private boolean equals(String name1, String name2) {
            return name1 != null && name1.equals(name2);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int selectedRow = UserList.this.table.getSelectedRow();
            System.err.println(row + " " + selectedRow);
            List<UserRepresentation> list = UserList.this.tableModel.list;
            if (selectedRow != -1) {
                boolean ok = true;
                for (int i = 0; i < fields.length; i ++) {
                    if (comparisonMethods.contains(fields[i].get())) {
                        ok = ok && equals(list.get(row).values[i], list.get(selectedRow).values[i]);
                    }
                }
                if (ok) {
                    setBackground(Color.YELLOW);
                } else {
                    setBackground(Color.WHITE);
                }
            }
            return c;
        }
    }

    private class TableCellSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent listSelectionEvent) {
            UserList.this.table.updateUI();
        }
    }
}
