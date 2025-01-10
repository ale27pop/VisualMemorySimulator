package org.example.view;

import org.example.controller.MemoryController;
import org.example.model.MyPMTable;
import org.example.model.MyPageTable;
import org.example.model.MyTLB;

import javax.swing.*;
import java.awt.*;

public class SimulatorGUI extends JFrame {
    private SettingsPanel settingsPanel;
    private LoadInstructionPanel loadInstructionPanel;
    private MemoryPanel memoryPanel;
    private EventLogPanel eventLogPanel;
    private StatusPanel statusPanel;

    private MyTLB tlbModel;
    private MyPageTable pageTableModel;
    private MyPMTable pmTableModel;
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
        add(titleLabel);

        // Initialize Models
        tlbModel = new MyTLB();
        pageTableModel = new MyPageTable();
        pmTableModel = new MyPMTable();

        // Settings Panel
        settingsPanel = new SettingsPanel();
        settingsPanel.setBounds(20, 70, 400, 300); // Adjusted position below title
        add(settingsPanel);

        // Load Instruction Panel
        loadInstructionPanel = new LoadInstructionPanel();
        loadInstructionPanel.setBounds(20, 380, 400, 240); // Minimized height
        add(loadInstructionPanel);

        // Memory Panel
        memoryPanel = new MemoryPanel();
        memoryPanel.setBounds(450, 70, 950, 550); // Center for memory visualization
        add(memoryPanel);

        // Status Panel
        statusPanel = new StatusPanel();
        statusPanel.setBounds(1420, 70, 200, 550); // To the right of MemoryPanel
        add(statusPanel);

        // Event Log Panel
        eventLogPanel = new EventLogPanel();
        eventLogPanel.setBounds(20, 620, 1600, 170); // Increased height by saved space
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

            tlbModel.setSize(tlbSize);
            pageTableModel.setSize(pageTableSize);
            pmTableModel.setSize(physicalMemoryRows);

            memoryPanel.updateTables(tlbModel, pageTableModel, pmTableModel);

            int addressLength = (int) (Math.log(virtualMemorySize) / Math.log(2));
            loadInstructionPanel.initialize(addressLength);

            memoryController = new MemoryController(
                    physicalMemoryRows,
                    tlbSize,
                    null, // No initial address
                    new String[0],
                    tlbModel,
                    pageTableModel,
                    pmTableModel,
                    addressLength
            );

            eventLogPanel.appendLog("Memory visualization initialized successfully.\n");
            eventLogPanel.appendLog("TLB Size: " + tlbSize + "\n");
            eventLogPanel.appendLog("Page Table Rows: " + pageTableSize + "\n");
            eventLogPanel.appendLog("Physical Memory Rows: " + physicalMemoryRows + "\n");
            eventLogPanel.appendLog("Address Length: " + addressLength + " bits\n");

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
                simulationStep = 0;

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
            boolean stepCompleted = memoryController.processSimulationStep(currentAddress, simulationStep, eventLogPanel);

            if (stepCompleted) {
                simulationStep++;
                if (simulationStep > 5) { // Reset for the next address
                    simulationStep = 0;
                    currentAddressIndex++;
                }
            }

            memoryPanel.updateTables(tlbModel, pageTableModel, pmTableModel);

            if (currentAddressIndex >= addressArray.length) {
                eventLogPanel.appendLog("Simulation complete for all addresses.\n");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error during simulation step: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new SimulatorGUI();
    }
}
