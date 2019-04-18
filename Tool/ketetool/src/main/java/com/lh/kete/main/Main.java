package com.lh.kete.main;

import com.android.ddmlib.AndroidDebugBridge;
import com.lh.kete.util.Command;
import com.lh.kete.util.UI;
import com.lh.kete.views.main.MainForm;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        if (init()) {
            JFrame frame = new JFrame("Kete tool");
            frame.setContentPane(new MainForm().$$$getRootComponent$$$());
            frame.setSize(500, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            UI.setRoot(frame);
        }
    }

    private static boolean init() {
        AndroidDebugBridge.init(false);
        String adbLocation;
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                adbLocation = Command.exec("where adb");
            } else {
                adbLocation = Command.exec("which adb");
            }
        } catch (Exception e) {
            adbLocation = null;
        }
        if (adbLocation == null) {
            JOptionPane.showMessageDialog(null, "adb is not found in environment path.");
            return false;
        }
        adbLocation = adbLocation.trim();
        AndroidDebugBridge.createBridge(adbLocation, false);
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        return true;
    }
}
