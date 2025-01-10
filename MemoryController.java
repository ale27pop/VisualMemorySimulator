package org.example.controller;

import org.example.model.MyPMTable;
import org.example.model.MyPageTable;
import org.example.model.MyTLB;
import org.example.view.EventLogPanel;

public class MemoryController {
    public int physicalPageSize;
    public int tlbSize;
    public int addressLength;
    public String[] instructionArray;
    MyTLB tlb;
    MyPMTable pmTable;
    MyPageTable pageTable;

    public MemoryController(int physicalPage, int tlbSize, String instruction, String[] array, MyTLB tlb, MyPageTable pageTable, MyPMTable pmTable, int addressLength) {
        this.instructionArray = array;
        this.physicalPageSize = physicalPage;
        this.tlbSize = tlbSize;
        this.addressLength = addressLength;

        this.tlb = tlb;
        this.pmTable = pmTable;
        this.pageTable = pageTable;
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

    public String binaryToHex(String s) {
        if (s != null && !s.isEmpty()) {
            int decimalValue = Integer.parseInt(s, 2);
            return Integer.toHexString(decimalValue).toUpperCase();
        } else {
            throw new IllegalArgumentException("Invalid binary string");
        }
    }

    public void initialize(int physicalPageSize, int tlbSize, int offset, int addressLength) {
        this.physicalPageSize = physicalPageSize;
        this.tlbSize = tlbSize;
        this.addressLength = addressLength;

        tlb.setSize(tlbSize);
        pageTable.setSize((int) Math.pow(2, addressLength - offset));
        pmTable.setSize((int) Math.pow(2, addressLength - offset));
    }

    public boolean processSimulationStep(String currentAddress, int simulationStep, EventLogPanel eventLogPanel) {
        String binaryAddress = hexToBinary(currentAddress, addressLength);
        String offsetBits = binaryAddress.substring(binaryAddress.length() - 2);
        String virtualPageNumber = binaryAddress.substring(0, binaryAddress.length() - 2);
        String virtualPageNumberHex = binaryToHex(virtualPageNumber);

        switch (simulationStep) {
            case 0:
                eventLogPanel.appendLog("Step 0: Breaking down the address.\n");
                eventLogPanel.appendLog("Binary Address: " + binaryAddress + "\n");
                eventLogPanel.appendLog("Virtual Page Number (Binary): " + virtualPageNumber + "\n");
                eventLogPanel.appendLog("Virtual Page Number (Hex): " + virtualPageNumberHex + "\n");
                eventLogPanel.appendLog("Offset Bits: " + offsetBits + "\n");
                return true;

            case 1:
                eventLogPanel.appendLog("Step 1: Checking TLB for Virtual Page Number (Hex): " + virtualPageNumberHex + "\n");
                int tlbIndex = tlb.searchTLB(virtualPageNumberHex);
                if (tlbIndex != -1) {
                    eventLogPanel.appendLog("TLB Hit! Physical Page: " + tlb.getValueAt(tlbIndex, 2) + "\n");
                    return false; // Skip further steps if TLB hit
                } else {
                    eventLogPanel.appendLog("TLB Miss! Proceeding to Page Table.\n");
                }
                return true;

            case 2:
                eventLogPanel.appendLog("Step 2: Searching Page Table for Virtual Page Number (Hex): " + virtualPageNumberHex + "\n");
                if (pageTable.searchPageTable(virtualPageNumberHex) == -1) {
                    eventLogPanel.appendLog("Page Table Miss! Loading from secondary memory.\n");
                    int physicalFrameIndex = pmTable.addValue(virtualPageNumberHex, 2);
                    pageTable.setValue("1", Integer.parseInt(virtualPageNumberHex, 16), 1);
                    pageTable.setValue(Integer.toHexString(physicalFrameIndex).toUpperCase(), Integer.parseInt(virtualPageNumberHex, 16), 2);
                    tlb.addValue(virtualPageNumberHex, Integer.toHexString(physicalFrameIndex).toUpperCase());
                } else {
                    eventLogPanel.appendLog("Page Table Hit!\n");
                }
                return true;

            case 3:
                eventLogPanel.appendLog("Step 3: Retrieving Data from Physical Memory.\n");
                return true;

            case 4:
                eventLogPanel.appendLog("Step 4: Simulation complete for this address. Moving to next address.\n");
                return true;

            default:
                return false;
        }
    }

    public void setInstructionArray(String[] addressArray) {
        this.instructionArray = addressArray;
    }
}
