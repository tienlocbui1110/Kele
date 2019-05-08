package com.lh.kete.base;

public abstract class Presenter<V extends View> {
    private V mView;

    private Presenter() {
    }

    public Presenter(V view) {
        this.mView = view;
    }

    public V getView() {
        return mView;
    }
}
