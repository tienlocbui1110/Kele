package com.lh.kete.views.loader;

public final class Loader {
    private static Loader sInstance;
    private final static Object LOCK = new Object();

    private boolean ready = true;

    public static void show() {
        init();
        synchronized (LOCK) {
            sInstance.ready = false;
        }
    }

    public static void hide() {
        init();
        synchronized (LOCK) {
            init();
            sInstance.ready = true;
        }
    }

    public static void updateText(String text) {
        init();
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
