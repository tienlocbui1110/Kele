package com.lh.kete.views.loader;

import javax.swing.*;
import java.awt.*;

public final class Loader {
    private static Loader sInstance;
    private final static Object LOCK = new Object();

    private boolean ready = true;
    private final JDialog loaderDialog;

    private Loader() {
        loaderDialog = new JDialog();
        loaderDialog.setMaximumSize(new Dimension(1920, 200));
        loaderDialog.setMinimumSize(new Dimension(200, 200));
    }

    public static void show() {
        init();
        if (!sInstance.ready)
            return;
        synchronized (LOCK) {
            sInstance.ready = false;
        }
    }

    public static void hide() {
        init();
        if (sInstance.ready)
            return;
        synchronized (LOCK) {
            init();
            sInstance.ready = true;
        }
    }

    public static void updateText(String text) {
        init();
        if (sInstance.ready)
            return;
        synchronized (LOCK) {
            init();
        }
    }

    public static boolean isReady() {
        init();
        synchronized (LOCK) {
            return sInstance.ready;
        }
    }

    private static void init() {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new Loader();
                }
            }
        }
    }
}
