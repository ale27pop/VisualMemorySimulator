package org.example.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Random;

public class LoadInstructionPanel extends JPanel {
    private JTextField binaryAddressField;     // Random Address (Binary)
    private JTextField hexAddressField;        // Random Address (Hex)
    private JTextField pageNumberHexField;     // Virtual Page Number (Hex)
    private JTextField pageNumberField;        // Virtual Page Number (Binary)
    private JTextField offsetField;            // Page Offset
    private JButton generateButton;            // Generate Button
    private JButton submitButton;              // Submit Button

    private final int OFFSET_BITS = 2;         // Fixed Offset Bits for demonstration purposes
    private Runnable submitFunction;           // Submit function for integration with the controller
    private int addressLength;                 // Address length calculated by the simulator GUI
    private String generatedHexAddress;        // Store the generated hex address

    public LoadInstructionPanel() {
        setBorder(BorderFactory.createTitledBorder("Load Instruction"));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Binary Address
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Random Address (Binary):"), gbc);

        gbc.gridx = 1;
        binaryAddressField = new JTextField(20);
        binaryAddressField.setEditable(false);
        add(binaryAddressField, gbc);

        // Row 2: Hex Address
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Random Address (Hex):"), gbc);

        gbc.gridx = 1;
        hexAddressField = new JTextField(20);
        hexAddressField.setEditable(false);
        add(hexAddressField, gbc);

        // Row 3: Virtual Page Number (Hex)
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Virtual Page Number (Hex):"), gbc);

        gbc.gridx = 1;
        pageNumberHexField = new JTextField(20);
        pageNumberHexField.setEditable(false);
        add(pageNumberHexField, gbc);

        // Row 4: Virtual Page Number (Binary)
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Virtual Page Number (Binary):"), gbc);

        gbc.gridx = 1;
        pageNumberField = new JTextField(20);
        pageNumberField.setEditable(false);
        add(pageNumberField, gbc);

        // Row 5: Page Offset
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Page Offset (Binary):"), gbc);

        gbc.gridx = 1;
        offsetField = new JTextField(20);
        offsetField.setEditable(false);
        add(offsetField, gbc);

        // Row 6: Buttons
        gbc.gridx = 0;
        gbc.gridy = 5;
        generateButton = new JButton("Generate");
        add(generateButton, gbc);

        gbc.gridx = 1;
        submitButton = new JButton("Submit");
        submitButton.setEnabled(false); // Disabled until address is generated
        add(submitButton, gbc);

        // Add listener to the Generate button
        generateButton.addActionListener(e -> generateRandomAddress());

        // Add listener to the Submit button
        submitButton.addActionListener(e -> {
            if (submitFunction != null) {
                submitFunction.run(); // Call the submit function when defined
            }
        });
    }

    /**
     * Initialize the Load Instruction Panel with the given address length.
     *
     * @param addressLength The address length (calculated by the simulator GUI).
     */
    public void initialize(int addressLength) {
        if (addressLength <= 0) {
            throw new IllegalArgumentException("Address length must be a positive integer.");
        }
        this.addressLength = addressLength;
        resetFields(); // Reset fields when re-initializing
    }

    /**
     * Generate a random address, transform it to hex, and compute the virtual page number and offset.
     */
    public void generateRandomAddress() {
        try {
            if (this.addressLength <= 0) {
                throw new IllegalStateException("Address length is not initialized.");
            }
            // Step 1: Generate Random Binary Address
            Random random = new Random();
            StringBuilder binaryAddress = new StringBuilder();
            for (int i = 0; i < this.addressLength; i++) {
                binaryAddress.append(random.nextInt(2)); // Append 0 or 1
            }
            String binaryAddressString = binaryAddress.toString();
            binaryAddressField.setText(binaryAddressString); // Fill binary address field
            // Step 2: Convert Binary Address to Hexadecimal
            generatedHexAddress = binaryToHex(binaryAddressString);
            hexAddressField.setText(generatedHexAddress); // Fill hex address field
            // Step 3: Split Binary Address into Page Number and Offset
            String pageNumberBinary = binaryAddressString.substring(0, binaryAddressString.length() - OFFSET_BITS); // Remove last OFFSET_BITS
            String offset = binaryAddressString.substring(binaryAddressString.length() - OFFSET_BITS);         // Last OFFSET_BITS
            pageNumberField.setText(pageNumberBinary);
            // Step 4: Convert Virtual Page Number to Hexadecimal
            String pageNumberHex = binaryToHex(pageNumberBinary);
            pageNumberHexField.setText(pageNumberHex); // Fill virtual page number hex field
            offsetField.setText(offset);
            // Enable the submit button now that an address is generated
            submitButton.setEnabled(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating address: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Convert a binary string to a hex string.
     * Ensures the binary string is padded to a multiple of 4 bits.
     */
    private String binaryToHex(String binary) {
        int length = binary.length();
        int paddedLength = ((length + 3) / 4) * 4; // Ensure multiple of 4
        String paddedBinary = String.format("%" + paddedLength + "s", binary).replace(' ', '0');

        int decimalValue = Integer.parseInt(paddedBinary, 2);
        return Integer.toHexString(decimalValue).toUpperCase();
    }

    /**
     * Get the generated Hex Address.
     */
    public String getGeneratedHexAddress() {
        return generatedHexAddress;
    }

    /**
     * Set the function to be called when the Submit button is clicked.
     */
    public void setSubmitFunction(Runnable submitFunction) {
        this.submitFunction = submitFunction;
    }

    /**
     * Reset the fields for a new initialization or generation cycle.
     */
    void resetFields() {
        binaryAddressField.setText("");
        hexAddressField.setText("");
        pageNumberHexField.setText("");
        pageNumberField.setText("");
        offsetField.setText("");
        generatedHexAddress = null;
        submitButton.setEnabled(false); // Disable submit until new address is generated
    }

    public void setGenerateAddressesAction(ActionListener actionListener) {
        generateButton.addActionListener(actionListener);
    }
}
