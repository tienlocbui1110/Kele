package com.lh.kete.main;

import com.android.ddmlib.AndroidDebugBridge;
import com.lh.kete.views.main.MainForm;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Kete tool");
        frame.setContentPane(new MainForm().$$$getRootComponent$$$());
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void init() {
        AndroidDebugBridge.init(false);
        AndroidDebugBridge.createBridge();
        try {
            UIManager.setLookAndFeel("Nimbus");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }
    }
}
