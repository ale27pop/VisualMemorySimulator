package org.example.view;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private JTextField physicalPageSizeField, tlbSizeField, offsetField, virtualMemorySizeField;
    private JButton submitButton;

    public SettingsPanel() {
        // Set up the panel with a titled border and GridBagLayout
        setBorder(BorderFactory.createTitledBorder("Please Introduce the Setup of the Simulation"));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Physical Page Size
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("<html>Physical Page Size<br>(Bytes - power of 2):</html>"), gbc);

        gbc.gridx = 1;
        physicalPageSizeField = new JTextField(20);
        add(physicalPageSizeField, gbc);

        // Row 2: TLB Size
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("TLB Size:"), gbc);

        gbc.gridx = 1;
        tlbSizeField = new JTextField(20);
        add(tlbSizeField, gbc);

        // Row 3: Offset (pre-filled and non-editable)
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Offset:"), gbc);

        gbc.gridx = 1;
        offsetField = new JTextField("2");
        offsetField.setEditable(false);
        add(offsetField, gbc);

        // Row 4: Virtual Memory Size
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("<html>Virtual Memory Size<br>(Bytes - power of 2):</html>"), gbc);

        gbc.gridx = 1;
        virtualMemorySizeField = new JTextField(20);
        add(virtualMemorySizeField, gbc);

        // Row 5: Submit Button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        submitButton = new JButton("Submit");
        add(submitButton, gbc);
    }

    /**
     * Get the Physical Page Size entered by the user.
     *
     * @return Physical Page Size as an integer.
     * @throws NumberFormatException if the input is not a valid integer.
     */
    public int getPhysicalPageSize() throws NumberFormatException {
        return Integer.parseInt(physicalPageSizeField.getText());
    }

    /**
     * Get the TLB Size entered by the user.
     *
     * @return TLB Size as an integer.
     * @throws NumberFormatException if the input is not a valid integer.
     */
    public int getTlbSize() throws NumberFormatException {
        return Integer.parseInt(tlbSizeField.getText());
    }

    /**
     * Get the Offset value.
     *
     * @return Offset as an integer. Always returns 2 (pre-filled value).
     */
    public int getOffset() {
        return Integer.parseInt(offsetField.getText()); // Offset is fixed at 2
    }

    /**
     * Get the Virtual Memory Size entered by the user.
     *
     * @return Virtual Memory Size as an integer.
     * @throws NumberFormatException if the input is not a valid integer.
     */
    public int getVirtualMemorySize() throws NumberFormatException {
        return Integer.parseInt(virtualMemorySizeField.getText());
    }

    /**
     * Attach functionality to the Submit button.
     *
     * @param action A Runnable action to execute when the button is clicked.
     */
    public void setSubmitButtonFunction(Runnable action) {
        submitButton.addActionListener(e -> action.run());
    }

    /**
     * Reset all input fields to their default values.
     * This method can be used to clear inputs after submission or initialization.
     */
    public void resetFields() {
        physicalPageSizeField.setText("");
        tlbSizeField.setText("");
        offsetField.setText("2"); // Reset offset to default value
        virtualMemorySizeField.setText("");
    }

    /**
     * Validate that all required fields are filled and contain valid values.
     *
     * @throws IllegalArgumentException if any field is empty or invalid.
     */
    public void validateInputs() {
        if (physicalPageSizeField.getText().isEmpty() || tlbSizeField.getText().isEmpty() ||
                offsetField.getText().isEmpty() || virtualMemorySizeField.getText().isEmpty()) {
            throw new IllegalArgumentException("All fields must be filled in.");
        }

        try {
            getPhysicalPageSize();
            getTlbSize();
            getOffset();
            getVirtualMemorySize();
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("All inputs must be valid integers.");
        }
    }
}
