package com.lh.component.common;

import com.lh.IPackage.IWord;

public class SingleWord implements IWord<String> {
    private String word;

    public SingleWord(String word) {
        this.word = word;
    }

    @Override
    public String getWord() {
        return word;
    }
}
