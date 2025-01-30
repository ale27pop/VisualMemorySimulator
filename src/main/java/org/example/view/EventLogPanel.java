package org.example.view;

import javax.swing.*;
import java.awt.*;

public class EventLogPanel extends JPanel {
    private JTextArea logArea;
    private JButton nextButton;

    public EventLogPanel() {
        setLayout(new BorderLayout()); // Use BorderLayout for dynamic resizing
        setBorder(BorderFactory.createTitledBorder("Event Log"));

        // Log Area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);

        // Add a JScrollPane with always-visible vertical scrollbar
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Always show vertical scrollbar
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); // Horizontal scrollbar if needed
        add(scrollPane, BorderLayout.CENTER); // Centered scroll pane to occupy full space

        // Next Button
        nextButton = new JButton("Next");
        add(nextButton, BorderLayout.SOUTH); // Add the button at the bottom
    }

    // Method to append log messages to the text area
    public void appendLog(String logMessage) {
        logArea.append(logMessage + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength()); // Scroll to the latest entry
    }

    // Method to clear the logs
    public void clearLog() {
        logArea.setText("");
    }

    // Method to set action for the Next button
    public void setNextButtonAction(Runnable action) {
        nextButton.addActionListener(e -> action.run());
    }
}
