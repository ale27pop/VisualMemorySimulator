package org.example.model;

import javax.swing.table.AbstractTableModel;

public class PMTable extends AbstractTableModel {
    private String[][] table;
    private int index;

    public PMTable() {
        int rowCount = 100;
        int columnCount = 2;
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
        return 2;
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
        if (c == 0) return "Physical Page";
        if (c == 1) return "Content";
        return null;
    }

    public void setSize(int rowCount) {
        int columnCount = 2;
        table = new String[rowCount][columnCount];

        for (int i = 0; i < rowCount; i++) {
            setValue((Integer.toHexString(i)).toUpperCase(), i, 0);
            for (int j = 1; j < columnCount; j++) {
                table[i][j] = "";
            }
        }
        fireTableDataChanged();
    }

    public int addValue(String s, int offset) {
        StringBuilder s2 = new StringBuilder();
        s2.append("Block ").append(s).append(" from 0-").append((int) (Math.pow(2, offset)));
        setValue(s2.toString(), index, 1); // Set the value at the current index.

        int currentIndex = index; // Store the current index to return later.

        index++; // Increment the index.
        if (index >= getRowCount()) { // Wrap around if we reach the end of the table.
            index = 0;
        }

        fireTableDataChanged(); // Notify the table of the changes.
        return currentIndex; // Return the current index where the value was added.
    }

    /**
     * Clear all values in the table, resetting it to its initial empty state.
     */
    public void clear() {
        for (int i = 0; i < table.length; i++) {
            for (int j = 1; j < getColumnCount(); j++) {
                table[i][j] = ""; // Clear the content column
            }
        }
        index = 0; // Reset the index to the starting point.
        fireTableDataChanged(); // Notify the table of the changes.
    }
}
