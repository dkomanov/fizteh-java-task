package ru.fizteh.fivt.students.fedyuninV.userList;

import ru.fizteh.fivt.bind.test.UserType;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.Vector;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class UserTable extends AbstractTableModel{
    private final String[] columnNames;
    private Vector<Object[]> data;
    private final DefaultCellEditor cellEditor;


    public UserTable()
    {
        columnNames = new String[]{"First name", "Second name", "User type", "is root?", "quota"};
        cellEditor = new DefaultCellEditor(new JComboBox(UserType.values()));
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

    public void setValueAt(int row, int col, Object value) {
        data.get(row)[col] = value;
    }

    public void removeRow(int row) {
        data.remove(row);
        fireTableDataChanged();
    }
}
