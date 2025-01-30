package org.example.view;

import org.example.controller.MemoryController;
import org.example.model.PMTable;
import org.example.model.PageTable;
import org.example.model.TLBTable;

import javax.swing.*;
import java.awt.*;

public class SimulatorGUI extends JFrame {
    private SettingsPanel settingsPanel;
    private LoadInstructionPanel loadInstructionPanel;
    private MemoryPanel memoryPanel;
    private EventLogPanel eventLogPanel;
    private StatusPanel statusPanel;

    public TLBTable tlbTableModel;
    public PageTable pageTableModel;
    public PMTable pmTableModel;
    private MemoryController memoryController;

    private int simulationStep = 0;
    private String[] addressArray;
    private int currentAddressIndex = 0;

    public SimulatorGUI() {
        // Frame setup
        setTitle("Virtual Memory Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null); // Absolute positioning for precise layout
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen

        // Add Title Label
        JLabel titleLabel = new JLabel("VIRTUAL MEMORY SIMULATOR", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(0, 0, 1700, 50); // Full width for center alignment
        titleLabel.setName("Title Label"); // Add name for testing
        add(titleLabel);

        // Initialize Models
        tlbTableModel = new TLBTable();
        pageTableModel = new PageTable();
        pmTableModel = new PMTable();

        // Settings Panel
        settingsPanel = new SettingsPanel();
        settingsPanel.setBounds(20, 70, 400, 280);
        settingsPanel.setName("Settings Panel"); // Add name for testing
        settingsPanel.setResetButtonFunction(this::resetSimulator); // Attach reset functionality
        add(settingsPanel);

        // Load Instruction Panel
        loadInstructionPanel = new LoadInstructionPanel();
        loadInstructionPanel.setBounds(20, 380, 400, 240);
        loadInstructionPanel.setName("Load Instruction Panel"); // Add name for testing
        add(loadInstructionPanel);

        // Memory Panel
        memoryPanel = new MemoryPanel();
        memoryPanel.setBounds(450, 70, 930, 550);
        memoryPanel.setName("Memory Panel"); // Add name for testing
        add(memoryPanel);

        // Status Panel
        statusPanel = new StatusPanel();
        statusPanel.setBounds(1390, 70, 130, 550);
        statusPanel.setName("Status Panel"); // Add name for testing
        add(statusPanel);

        // Event Log Panel
        eventLogPanel = new EventLogPanel();
        eventLogPanel.setBounds(20, 620, 1500, 170);
        eventLogPanel.setName("Event Log Panel"); // Add name for testing
        add(eventLogPanel);

        // Add functionality to the SettingsPanel submit button
        settingsPanel.setSubmitButtonFunction(this::initializeMemoryVisualization);

        // Add functionality to LoadInstructionPanel buttons
        loadInstructionPanel.setGenerateAddressesAction(e -> generateRandomAddress());
        loadInstructionPanel.setSubmitFunction(this::submitGeneratedHexAddress);

        // Add functionality to EventLogPanel next button
        eventLogPanel.setNextButtonAction(this::processNextStep);

        // Finalize frame
        setVisible(true);
    }

    private void initializeMemoryVisualization() {
        try {
            int physicalMemorySize = settingsPanel.getPhysicalPageSize();
            int virtualMemorySize = settingsPanel.getVirtualMemorySize();
            int tlbSize = settingsPanel.getTlbSize();
            int offset = settingsPanel.getOffset();

            if (physicalMemorySize <= 0 || virtualMemorySize <= 0 || tlbSize <= 0 || offset <= 0) {
                throw new IllegalArgumentException("All values must be positive integers!");
            }

            int pageTableSize = (int) (virtualMemorySize / Math.pow(2, offset));
            int physicalMemoryRows = (int) (physicalMemorySize / Math.pow(2, offset));

            tlbTableModel.setSize(tlbSize);
            pageTableModel.setSize(pageTableSize);
            pmTableModel.setSize(physicalMemoryRows);

            memoryPanel.updateTables(tlbTableModel, pageTableModel, pmTableModel);

            int addressLength = (int) (Math.log(virtualMemorySize) / Math.log(2));
            int pmAddressLength = (int) (Math.log(physicalMemorySize) / Math.log(2));

            loadInstructionPanel.initialize(addressLength);

            memoryController = new MemoryController(
                    physicalMemoryRows,
                    tlbSize,
                    null, // No initial address
                    new String[0],
                    tlbTableModel,
                    pageTableModel,
                    pmTableModel,
                    addressLength,
                    eventLogPanel,
                    statusPanel
            );

            memoryController.resetStatistics();

            eventLogPanel.appendLog("Memory visualization initialized successfully.\n");
            eventLogPanel.appendLog("TLB Size: " + tlbSize);
            eventLogPanel.appendLog("Page Table Rows: Virtual Memory Size / 2^ offset =  " + pageTableSize);
            eventLogPanel.appendLog("Physical Memory Rows: Physical Memory Size / 2^ offset = " + physicalMemoryRows);
            eventLogPanel.appendLog("Virtual Address Length: log 2 ( Virtual Memory Size ) = " + addressLength + " bits");
            eventLogPanel.appendLog("Physical Address Length: log 2 ( Physical Memory Size ) = " + pmAddressLength + " bits\n");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input! Please enter valid integers.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateRandomAddress() {
        try {
            loadInstructionPanel.generateRandomAddress();
            eventLogPanel.appendLog("Random address generated successfully.\n");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating address: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void submitGeneratedHexAddress() {
        try {
            String generatedHexAddress = loadInstructionPanel.getGeneratedHexAddress();
            if (generatedHexAddress != null && !generatedHexAddress.isEmpty()) {
                addressArray = new String[]{generatedHexAddress};
                currentAddressIndex = 0;
                simulationStep = 1; // Start simulation at step 1

                memoryController.setInstructionArray(addressArray);

                eventLogPanel.appendLog("Submitted Address (Hex): " + generatedHexAddress + "\n");
            } else {
                throw new IllegalStateException("No address has been generated yet. Please generate an address first.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error submitting address: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processNextStep() {
        try {
            if (addressArray == null || addressArray.length == 0 || currentAddressIndex >= addressArray.length) {
                throw new IllegalStateException("No addresses to process. Generate and submit an address first.");
            }

            String currentAddress = addressArray[currentAddressIndex];
            boolean isHit = memoryController.processSimulationStep(currentAddress, simulationStep);

            if (isHit || simulationStep == 4) {
                simulationStep = 1; // Reset for the next address
                currentAddressIndex++;
            } else {
                simulationStep++;
            }

            memoryPanel.updateTables(tlbTableModel, pageTableModel, pmTableModel);

            if (currentAddressIndex >= addressArray.length) {
                eventLogPanel.appendLog("Simulation complete for all addresses.\n");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error during simulation step: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetSimulator() {
        // Step 1: Set table sizes to 0 rows
        tlbTableModel.setSize(0); // Reset TLB to no rows
        pageTableModel.setSize(0); // Reset Page Table to no rows
        pmTableModel.setSize(0); // Reset Physical Memory Table to no rows

        // Step 2: Clear table data and reset internal counters
        tlbTableModel.clear(); // Reset TLB entries and index
        pageTableModel.clear(); // Reset Page Table entries and index
        pmTableModel.clear(); // Reset Physical Memory Table entries and index

        // Step 3: Reset UI components
        loadInstructionPanel.resetFields(); // Reset Load Instruction fields
        eventLogPanel.clearLog(); // Clear Event Logs
        statusPanel.resetStatus(); // Reset Statistics Panel
        eventLogPanel.appendLog("Simulator reset successfully.\n");

        // Step 4: Reset internal simulation variables
        simulationStep = 0;
        addressArray = null;
        currentAddressIndex = 0;

        // Step 5: Refresh Memory Panel display
        memoryPanel.updateTables(tlbTableModel, pageTableModel, pmTableModel); // Update table visuals
    }



    public static void main(String[] args) {
        new SimulatorGUI();
    }
}
