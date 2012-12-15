package ru.fizteh.fivt.students.fedyuninV.userList;

import ru.fizteh.fivt.bind.test.Permissions;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class UserTable extends AbstractTableModel{
    private final String[] columnNames;
    private Vector<Object[]> data;
    final DefaultCellEditor userTypeEditor;

    public UserTable()
    {
        columnNames = new String[]{"ID", "First name", "Second name", "User type", "is root?", "quota"};
        data = new Vector<>();
        userTypeEditor = new DefaultCellEditor(new JComboBox(UserType.values()));
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public int getRowCount() {
        return data.size();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data.get(row)[col];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return  true;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        data.get(row)[col] = value;
    }

    @Override
    public Class getColumnClass(int col) {
        switch (col) {
            case 0:
                return Integer.class;
            case 3:
                return UserType.class;
            case 4:
                return Boolean.class;
            case 5:
                return Integer.class;
            default:
                return String.class;
        }
    }

    public void removeRow(int row) {
        data.remove(row);
        fireTableDataChanged();
    }

    public void addRow() {
        data.add(arrayFromUser(new User(0, UserType.USER, new UserName("xxx", "xxx"), new Permissions())));
        fireTableDataChanged();
    }

    private User userFromArray(Object[] userArray) {
        Permissions permissions = new Permissions();
        permissions.setQuota((Integer) userArray[5]);
        permissions.setRoot((Boolean) userArray[4]);
        return new User((Integer) userArray[0],(UserType) userArray[3],
                new UserName((String) userArray[1],(String) userArray[2]), permissions);
    }

    private Object[] arrayFromUser(User user) {
        return new Object[]{user.getId(), user.getName().getFirstName(), user.getName().getLastName(),
                user.getUserType(), user.getPermissions().isRoot(), user.getPermissions().getQuota()};
    }

    public List<User> getUserList() {
        List<User> result = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            result.add(userFromArray(data.get(i)));
        }
        return result;
    }

    public void setData(List<User> userList) {
        Vector<Object[]> newData = new Vector<>();
        for (User user: userList) {
            newData.add(arrayFromUser(user));
        }
        data = newData;
        fireTableDataChanged();
    }
}
