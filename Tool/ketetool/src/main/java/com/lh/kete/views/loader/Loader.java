package com.lh.kete.views.loader;

import com.lh.kete.util.UI;

import javax.swing.*;
import java.awt.*;

public final class Loader {
    private static Loader sInstance;
    private final static Object LOCK = new Object();

    private boolean ready = true;
    private final JDialog loaderDialog;
    private final LoaderView mView;

    private Loader() {
        loaderDialog = new JDialog();
        loaderDialog.setMaximumSize(new Dimension(1920, 200));
        loaderDialog.setMinimumSize(new Dimension(200, 200));
        loaderDialog.setModalityType(Dialog.ModalityType.MODELESS);
        loaderDialog.setLocationRelativeTo(null);
        loaderDialog.setAlwaysOnTop(true);
        mView = new LoaderView();
        loaderDialog.setContentPane(mView.$$$getRootComponent$$$());
    }

    public static void show() {
        init();
        if (!sInstance.ready)
            return;
        synchronized (LOCK) {
            sInstance.ready = false;
            UI.run(() -> sInstance.loaderDialog.setVisible(true));
        }
    }

    public static void hide() {
        init();
        if (sInstance.ready)
            return;
        synchronized (LOCK) {
            sInstance.ready = true;
            UI.run(() -> sInstance.loaderDialog.setVisible(false));
        }
    }

    public static void updateText(String text) {
        init();
        if (sInstance.ready)
            return;
        synchronized (LOCK) {
            UI.run(() -> sInstance.mView.text.setText(text));
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
