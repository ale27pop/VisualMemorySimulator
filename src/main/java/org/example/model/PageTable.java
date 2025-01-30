package org.example.model;

import javax.swing.table.AbstractTableModel;

public class PageTable extends AbstractTableModel {

    private String[][] table;

    public PageTable() {
        int rowCount = 100;
        int columnCount = 3;
        table = new String[rowCount][columnCount];

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                table[i][j] = "";
            }
        }
    }

    @Override
    public int getRowCount() {
        return table.length;
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < table.length && columnIndex < getColumnCount()) {
            return table[rowIndex][columnIndex];
        }
        return null;
    }

    public void setValue(Object s, int r, int c) {
        if (r < table.length && c < getColumnCount()) {
            table[r][c] = s.toString();
        }
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int c) {
        if (c == 0) return "Index";
        if (c == 1) return "Valid";
        if (c == 2) return "Physical Page";
        return null;
    }

    public void setSize(int rowCount) {
        int columnCount = 3;
        table = new String[rowCount][columnCount];

        for (int i = 0; i < rowCount; i++) {
            setValue((Integer.toHexString(i)).toUpperCase(), i, 0);
            table[i][1] = "0";
            for (int j = 2; j < columnCount; j++) {
                table[i][j] = "";
            }
        }
        fireTableDataChanged();
    }

    public int searchPageTable(String n) {
        if (table[Integer.parseInt(n, 16)][1].equals("1")) return 1;
        return -1;
    }

    /**
     * Clear all values in the table, resetting it to its initial empty state.
     */
    public void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i][1] = "0"; // Reset valid bit
            for (int j = 2; j < getColumnCount(); j++) {
                table[i][j] = ""; // Clear physical page and other data
            }
        }
        fireTableDataChanged();
    }
}
