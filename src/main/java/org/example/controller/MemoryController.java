package org.example.controller;

import org.example.model.PMTable;
import org.example.model.PageTable;
import org.example.model.TLBTable;
import org.example.view.EventLogPanel;
import org.example.view.StatusPanel;

public class MemoryController {
    private int physicalPageSize;
    private int tlbSize;
    private int addressLength;
    private String[] instructionArray;
    private TLBTable tlbTable;
    private PMTable pmTable;
    private PageTable pageTable;
    private EventLogPanel eventLogPanel;
    private StatusPanel statusPanel;

    private int hitCount;
    private int missCount;

    public MemoryController(int physicalPage, int tlbSize, String instruction, String[] array, TLBTable tlbTable, PageTable pageTable, PMTable pmTable, int addressLength, EventLogPanel eventLogPanel, StatusPanel statusPanel) {
        this.instructionArray = array;
        this.physicalPageSize = physicalPage;
        this.tlbSize = tlbSize;
        this.addressLength = addressLength;
        this.tlbTable = tlbTable;
        this.pmTable = pmTable;
        this.pageTable = pageTable;
        this.eventLogPanel = eventLogPanel;
        this.statusPanel = statusPanel;
        this.hitCount = 0;
        this.missCount = 0;
    }

    public String hexToBinary(String hex, int binaryLength) {
        if (hex.isEmpty()) {
            return "";
        }

        StringBuilder binary = new StringBuilder();
        String[] hexToBinaryMap = {
                "0000", "0001", "0010", "0011",
                "0100", "0101", "0110", "0111",
                "1000", "1001", "1010", "1011",
                "1100", "1101", "1110", "1111"
        };

        for (char hexChar : hex.toCharArray()) {
            if (!Character.isDigit(hexChar) && (hexChar < 'A' || hexChar > 'F')) {
                throw new IllegalArgumentException("Invalid hexadecimal character: " + hexChar);
            }

            if (Character.isDigit(hexChar)) {
                binary.append(hexToBinaryMap[Integer.parseInt(String.valueOf(hexChar))]);
            } else {
                binary.append(hexToBinaryMap[Character.toUpperCase(hexChar) - 'A' + 10]);
            }
        }

        int currentLength = binary.length();
        if (currentLength < binaryLength) {
            binary.insert(0, "0".repeat(binaryLength - currentLength));
        } else if (currentLength > binaryLength) {
            binary.delete(0, currentLength - binaryLength);
        }
        return binary.toString();
    }

    public String binaryToHex(String binary) {
        if (binary != null && !binary.isEmpty()) {
            int decimalValue = Integer.parseInt(binary, 2);
            return Integer.toHexString(decimalValue).toUpperCase();
        } else {
            throw new IllegalArgumentException("Invalid binary string");
        }
    }

    public void initialize(int physicalPageSize, int tlbSize, int offset, int addressLength) {
        this.physicalPageSize = physicalPageSize;
        this.tlbSize = tlbSize;
        this.addressLength = addressLength;

        tlbTable.setSize(tlbSize);
        pageTable.setSize((int) Math.pow(2, addressLength - offset));
        pmTable.setSize((int) Math.pow(2, addressLength - offset));
    }

    public boolean processSimulationStep(String currentAddress, int simulationStep) {
        String binaryAddress = hexToBinary(currentAddress, addressLength);
        String offsetBits = binaryAddress.substring(binaryAddress.length() - 2); // Last 2 bits for offset
        String virtualPageNumber = binaryAddress.substring(0, binaryAddress.length() - 2);
        String virtualPageNumberHex = binaryToHex(virtualPageNumber);

        int offsetBitsLength = 2; // Fixed number of offset bits
        int physicalAddressLength = (int) (Math.log(physicalPageSize) / Math.log(2)); // log2(Physical Memory Size)
        int physicalPageBits = Math.max(physicalAddressLength - offsetBitsLength, 0); // Ensure non-negative value

        boolean isHit = false;

        switch (simulationStep) {
            case 1: // Step 1: Check TLB
                eventLogPanel.appendLog("Step 1: Checking TLB for Virtual Page Number (Hex): " + virtualPageNumberHex);
                int tlbIndex = tlbTable.searchTLB(virtualPageNumberHex);
                if (tlbIndex != -1) {
                    String physicalPage = (String) tlbTable.getValueAt(tlbIndex, 2);
                    String physicalPageBinary = hexToBinary(physicalPage, physicalPageBits); // Physical Page in required bits

                    eventLogPanel.appendLog("TLB Hit! Virtual Address " + currentAddress + " (Binary: " + binaryAddress + "),");
                    isHit = true;
                    hitCount++;
                } else {
                    eventLogPanel.appendLog("TLB Miss! Proceeding to Page Table.");
                }
                break;

            case 2: // Step 2: Check Page Table
                if (!isHit) {
                    eventLogPanel.appendLog("Step 2: Checking Page Table for Virtual Page Number (Hex): " + virtualPageNumberHex);
                    int pageTableIndex = pageTable.searchPageTable(virtualPageNumberHex);
                    if (pageTableIndex != -1) {
                        String physicalPage = (String) pageTable.getValueAt(pageTableIndex, 2);
                        String physicalPageBinary = hexToBinary(physicalPage, physicalPageBits); // Physical Page in required bits

                        eventLogPanel.appendLog("Page Table HIT! At Virtual Address " + currentAddress + " (Binary: " + binaryAddress + "), ");
                        isHit = true;
                        hitCount++;
                    } else {
                        eventLogPanel.appendLog("Page Table Miss! Loading from secondary memory.");
                        missCount++;
                    }
                }
                break;

            case 3: // Step 3: Load from Secondary Memory
                if (!isHit) {
                    eventLogPanel.appendLog("Step 3: Data will be loaded from Secondary Memory.");
                    int physicalFrameIndex = pmTable.addValue(virtualPageNumberHex, 2);
                    pageTable.setValue("1", Integer.parseInt(virtualPageNumberHex, 16), 1);
                    pageTable.setValue(Integer.toHexString(physicalFrameIndex).toUpperCase(), Integer.parseInt(virtualPageNumberHex, 16), 2);
                    tlbTable.addValue(virtualPageNumberHex, Integer.toHexString(physicalFrameIndex).toUpperCase());
                }
                break;

            case 4: // Final Step
                eventLogPanel.appendLog("Final Step: Simulation complete for this address. Submit another one!");
                break;

            default:
                throw new IllegalStateException("Invalid simulation step: " + simulationStep);
        }

        // Update statistics on StatusPanel
        statusPanel.updateStatistics(hitCount, missCount);

        return isHit;
    }

    public void setInstructionArray(String[] addressArray) {
        this.instructionArray = addressArray;
    }

    public void resetStatistics() {
        hitCount = 0;
        missCount = 0;
        statusPanel.updateStatistics(hitCount, missCount);
    }
}
