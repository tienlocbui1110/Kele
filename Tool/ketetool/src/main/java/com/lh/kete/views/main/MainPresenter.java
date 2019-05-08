package com.lh.kete.views.main;

import com.lh.kete.base.Presenter;

abstract class MainPresenter extends Presenter<MainView> {
    public MainPresenter(MainView view) {
        super(view);
    }

    abstract void onPreview();
}
