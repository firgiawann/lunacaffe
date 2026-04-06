package com.lunacaffe;

import com.formdev.flatlaf.FlatLightLaf;
import com.lunacaffe.view.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Setup FlatLaf theme
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Gagal menginisialisasi FlatLaf");
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame appFrame = new MainFrame();
            appFrame.setVisible(true);
        });
    }
}
