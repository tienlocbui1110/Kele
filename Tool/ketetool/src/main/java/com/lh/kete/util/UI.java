package com.lh.kete.util;

import javax.swing.*;
import java.awt.*;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;

public final class UI {
    private static Component root;

    public static void setRoot(Component component) {
        root = component;
    }

    public static void run(Runnable r) {
        if (SwingUtilities.isEventDispatchThread())
            r.run();
        else
            SwingUtilities.invokeLater(r);
    }

    public static void showMessage(String title, String message) {
        UI.run(() -> JOptionPane.showMessageDialog(root, message, title, INFORMATION_MESSAGE));
    }

    public static void showError(String title, String message) {
        UI.run(() -> JOptionPane.showMessageDialog(root, message, title, ERROR_MESSAGE));
    }
}
