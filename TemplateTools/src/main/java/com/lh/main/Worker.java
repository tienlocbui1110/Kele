package com.lh.main;

import com.lh.IPackage.ITemplate;

public class Worker {
    private ITemplate template;

    public Worker(ITemplate template) {
        this.template = template;
    }

    public void doWork() {
        template.onPreWork();
        template.onWorking();
        template.onPostWork();
    }
}
