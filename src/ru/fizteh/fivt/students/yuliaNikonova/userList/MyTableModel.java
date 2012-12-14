package ru.fizteh.fivt.students.yuliaNikonova.userList;

import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import ru.fizteh.fivt.bind.test.UserType;

public class MyTableModel extends AbstractTableModel {
	
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
