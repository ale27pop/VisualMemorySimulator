package org.example.view;

import org.example.model.TLBTable;
import org.example.model.PageTable;
import org.example.model.PMTable;

import javax.swing.*;
import java.awt.*;

public class MemoryPanel extends JPanel {
    private JTable tlbTable;
    private JTable pageTable;
    private JTable physicalMemoryTable;

    public MemoryPanel() {
        setLayout(new GridLayout(1, 3, 10, 10));
        setBorder(BorderFactory.createTitledBorder("Memory Visualization"));

        // Initialize tables
        tlbTable = new JTable();
        pageTable = new JTable();
        physicalMemoryTable = new JTable();

        // Add scrollable tables to panel
        add(createScrollPane(tlbTable, "Translation Lookaside Buffer"));
        add(createScrollPane(pageTable, "Page Table"));
        add(createScrollPane(physicalMemoryTable, "Physical Memory"));
    }

    /**
     * Helper method to create a scroll pane for a table with a title.
     */
    private JScrollPane createScrollPane(JTable table, String title) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(title));
        return scrollPane;
    }

    /**
     * Update tables with new models for TLB, Page Table, and Physical Memory.
     */
    public void updateTables(TLBTable tlbTableModel, PageTable pageTableModel, PMTable pmTableModel) {
        tlbTable.setModel(tlbTableModel);
        pageTable.setModel(pageTableModel);
        physicalMemoryTable.setModel(pmTableModel);
    }
}
