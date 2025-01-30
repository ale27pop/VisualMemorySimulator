package org.example;

import org.example.model.PageTable;
import org.example.model.PMTable;
import org.example.model.TLBTable;
import org.example.view.SimulatorGUI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class test {

    private SimulatorGUI simulatorGUI;
    private PageTable pageTable;
    private TLBTable tlbTable;
    private PMTable pmTable;

    @BeforeEach
    public void setUp() {
        simulatorGUI = new SimulatorGUI();

        // Access models for direct validation
        pageTable = simulatorGUI.pageTableModel;
        tlbTable = simulatorGUI.tlbTableModel;
        pmTable = simulatorGUI.pmTableModel;

        // Set simulation parameters
        setSimulationParameters(8, 4, 2, 16);
    }

    @Test
    public void testEndToEndSimulation() {
        // Binary addresses: "0000", "0100", "1000", "1100", "10000", "1000"
        simulateAddress("0000", false, "0", "0");
        simulateAddress("0100", false, "1", "1");
        simulateAddress("1000", false, "2", "0");
        simulateAddress("1100", false, "3", "1");
        simulateAddress("10000", false, "4", "0");
        simulateAddress("1000", true, "2", "0");
    }

    private void setSimulationParameters(int physicalPageSize, int tlbSize, int offset, int virtualMemorySize) {
        JTextField physicalPageSizeField = findComponent(simulatorGUI, "Physical Page Size");
        JTextField tlbSizeField = findComponent(simulatorGUI, "TLB Size");
        JTextField virtualMemorySizeField = findComponent(simulatorGUI, "Virtual Memory Size");

        physicalPageSizeField.setText(String.valueOf(physicalPageSize));
        tlbSizeField.setText(String.valueOf(tlbSize));
        virtualMemorySizeField.setText(String.valueOf(virtualMemorySize));

        JButton submitButton = findComponent(simulatorGUI, "Submit");
        submitButton.doClick();
    }

    private void simulateAddress(String binaryAddress, boolean expectedHit, String expectedVirtualPageHex, String expectedPhysicalPageHex) {
        JTextField binaryAddressField = findComponent(simulatorGUI, "Random Address (Binary)");
        JTextField hexAddressField = findComponent(simulatorGUI, "Random Address (Hex)");
        JButton generateButton = findComponent(simulatorGUI, "Generate");
        JButton submitButton = findComponent(simulatorGUI, "Submit");

        // Manually set the binary and hex fields
        binaryAddressField.setText(binaryAddress);
        hexAddressField.setText(binaryToHex(binaryAddress));

        // Trigger actions
        generateButton.doClick();
        submitButton.doClick();

        // Validate simulator state after processing
        validateSimulatorState(binaryAddress, expectedHit, expectedVirtualPageHex, expectedPhysicalPageHex);
    }

    private void validateSimulatorState(String binaryAddress, boolean expectedHit, String expectedVirtualPageHex, String expectedPhysicalPageHex) {
        String virtualPageBinary = binaryAddress.substring(0, binaryAddress.length() - 2);
        String virtualPageHex = Integer.toHexString(Integer.parseInt(virtualPageBinary, 2)).toUpperCase();

        // Validate Page Table
        int virtualPageIndex = Integer.parseInt(virtualPageHex, 16);
        String validBit = (String) pageTable.getValueAt(virtualPageIndex, 1);
        assertEquals("1", validBit, "Page table valid bit mismatch");
        String physicalPageFromPageTable = (String) pageTable.getValueAt(virtualPageIndex, 2);
        assertEquals(expectedPhysicalPageHex, physicalPageFromPageTable, "Page table physical page mismatch");

        // Validate Physical Memory
        int physicalPageIndex = Integer.parseInt(expectedPhysicalPageHex, 16);
        String physicalPageContent = (String) pmTable.getValueAt(physicalPageIndex, 1);
        assertTrue(physicalPageContent.contains(virtualPageHex), "Physical memory content mismatch");

        // Validate TLB
        int tlbIndex = tlbTable.searchTLB(virtualPageHex);
        if (expectedHit) {
            assertTrue(tlbIndex >= 0, "Expected TLB hit, but got a miss");
            String physicalPageFromTLB = (String) tlbTable.getValueAt(tlbIndex, 2);
            assertEquals(expectedPhysicalPageHex, physicalPageFromTLB, "TLB physical page mismatch");
        } else {
            assertEquals(-1, tlbIndex, "Expected TLB miss, but got a hit");
        }
    }

    private String binaryToHex(String binary) {
        return Integer.toHexString(Integer.parseInt(binary, 2)).toUpperCase();
    }

    @SuppressWarnings("unchecked")
    private <T extends Component> T findComponent(Container container, String name) {
        for (Component component : container.getComponents()) {
            if (component instanceof JComponent) {
                String componentName = ((JComponent) component).getName();
                if (name.equals(componentName)) {
                    return (T) component;
                }
            }
            if (component instanceof Container) {
                T result = findComponent((Container) component, name);
                if (result != null) {
                    return result;
                }
            }
        }
        throw new IllegalArgumentException("Component with name '" + name + "' not found.");
    }
}
