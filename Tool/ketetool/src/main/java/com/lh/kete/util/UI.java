package com.lh.kete.util;

import javax.swing.*;

public final class UI {
    public static void run(Runnable r) {
        if (SwingUtilities.isEventDispatchThread())
            r.run();
        else
            SwingUtilities.invokeLater(r);
    }
}
