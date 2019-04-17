package com.lh.kete.main;

import com.android.ddmlib.AndroidDebugBridge;
import com.lh.kete.util.Command;
import com.lh.kete.views.main.MainForm;

import javax.swing.*;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        if (init()) {
            JFrame frame = new JFrame("Kete tool");
            frame.setContentPane(new MainForm().$$$getRootComponent$$$());
            frame.setSize(500, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }

    private static boolean init() {
        AndroidDebugBridge.init(false);
        byte[] result;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            result = Command.exec("where adb");
        } else {
            result = Command.exec("which adb");
        }
        if (result == null) {
            JOptionPane.showMessageDialog(null, "adb is not found in environment path.");
            return false;
        }
        String adbLocation = new String(result).trim();
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
