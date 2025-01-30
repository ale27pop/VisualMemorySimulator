package org.example.view;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private JTextField physicalPageSizeField, tlbSizeField, offsetField, virtualMemorySizeField;
    private JButton submitButton, resetButton; // Added reset button

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
        JLabel physicalPageLabel = new JLabel("<html>Physical Page Size<br>(Bytes - power of 2):</html>");
        physicalPageLabel.setName("Physical Page Size Label");
        add(physicalPageLabel, gbc);

        gbc.gridx = 1;
        physicalPageSizeField = new JTextField(20);
        physicalPageSizeField.setName("Physical Page Size");
        add(physicalPageSizeField, gbc);

        // Row 2: TLB Size
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel tlbSizeLabel = new JLabel("TLB Size:");
        tlbSizeLabel.setName("TLB Size Label");
        add(tlbSizeLabel, gbc);

        gbc.gridx = 1;
        tlbSizeField = new JTextField(20);
        tlbSizeField.setName("TLB Size");
        add(tlbSizeField, gbc);

        // Row 3: Offset (pre-filled and non-editable)
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel offsetLabel = new JLabel("Offset:");
        offsetLabel.setName("Offset Label");
        add(offsetLabel, gbc);

        gbc.gridx = 1;
        offsetField = new JTextField("2");
        offsetField.setEditable(false);
        offsetField.setName("Offset");
        add(offsetField, gbc);

        // Row 4: Virtual Memory Size
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel virtualMemoryLabel = new JLabel("<html>Virtual Memory Size<br>(Bytes - power of 2):</html>");
        virtualMemoryLabel.setName("Virtual Memory Size Label");
        add(virtualMemoryLabel, gbc);

        gbc.gridx = 1;
        virtualMemorySizeField = new JTextField(20);
        virtualMemorySizeField.setName("Virtual Memory Size");
        add(virtualMemorySizeField, gbc);

        // Row 5: Submit Button
        gbc.gridx = 1;
        gbc.gridy = 4;
        submitButton = new JButton("Submit");
        submitButton.setName("Submit Button");
        submitButton.addActionListener(e -> {
            try {
                validateInputs();
                submitButton.setEnabled(false); // Disable the submit button after pressing
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(submitButton, gbc);

        // Row 6: Reset Button
        gbc.gridx = 0;
        gbc.gridy = 4;
        resetButton = new JButton("Reset");
        resetButton.setName("Reset Button");
        resetButton.addActionListener(e -> {
            resetFields(); // Reset the input fields
            submitButton.setEnabled(true); // Re-enable the submit button
        });
        add(resetButton, gbc);
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
            int physicalPageSize = getPhysicalPageSize();
            int virtualMemorySize = getVirtualMemorySize();

            // Check if Physical Page Size and Virtual Memory Size are powers of 2
            if (!isPowerOfTwo(physicalPageSize)) {
                throw new IllegalArgumentException("Physical Page Size must be a power of 2.");
            }

            if (!isPowerOfTwo(virtualMemorySize)) {
                throw new IllegalArgumentException("Virtual Memory Size must be a power of 2.");
            }

            getTlbSize();
            getOffset();
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("All inputs must be valid integers.");
        }
    }

    /**
     * Utility method to check if a number is a power of 2.
     *
     * @param n The number to check.
     * @return True if the number is a power of 2, false otherwise.
     */
    private boolean isPowerOfTwo(int n) {
        return (n > 0) && ((n & (n - 1)) == 0);
    }

    /**
     * Attach functionality to the Reset button.
     *
     * @param action A Runnable action to execute when the reset button is clicked.
     */
    public void setResetButtonFunction(Runnable action) {
        resetButton.addActionListener(e -> {
            resetFields(); // Reset the input fields
            submitButton.setEnabled(true); // Re-enable the submit button
            action.run(); // Execute the provided action
        });
    }

    /**
     * Attach functionality to the Submit button.
     *
     * @param action A Runnable action to execute when the submit button is clicked.
     */
    public void setSubmitButtonFunction(Runnable action) {
        submitButton.addActionListener(e -> {
            try {
                validateInputs(); // Validate the inputs before proceeding
                submitButton.setEnabled(false); // Disable the submit button after it's clicked
                action.run(); // Execute the provided action
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

}
