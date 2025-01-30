package org.example.view;

import javax.swing.*;
import java.awt.*;

public class StatusPanel extends JPanel {
    private JLabel hitRateLabel;
    private JLabel missRateLabel;

    public StatusPanel() {
        setBorder(BorderFactory.createTitledBorder("Statistics"));
        setLayout(new GridLayout(2, 1, 5, 5));

        // Initialize Labels
        hitRateLabel = new JLabel("Hit Rate: [ 0% ]");
        missRateLabel = new JLabel("Miss Rate: [ 0% ]");

        // Add Labels to Panel
        add(hitRateLabel);
        add(missRateLabel);
    }

    /**
     * Update the hit and miss rates in the statistics panel.
     *
     * @param hitCount  The number of hits.
     * @param missCount The number of misses.
     */
    public void updateStatistics(int hitCount, int missCount) {
        int totalCount = hitCount + missCount;

        if (totalCount == 0) {
            hitRateLabel.setText("Hit Rate: [ 0% ]");
            missRateLabel.setText("Miss Rate: [ 0% ]");
        } else {
            double hitRate = (hitCount / (double) totalCount) * 100;
            double missRate = (missCount / (double) totalCount) * 100;

            hitRateLabel.setText(String.format("Hit Rate: [ %.2f%% ]", hitRate));
            missRateLabel.setText(String.format("Miss Rate: [ %.2f%% ]", missRate));
        }
    }

    /**
     * Resets the hit and miss rates to their default values (0%).
     */
    public void resetStatus() {
        hitRateLabel.setText("Hit Rate: [ 0% ]");
        missRateLabel.setText("Miss Rate: [ 0% ]");
    }

}

