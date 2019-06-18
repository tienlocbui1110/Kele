package com.lh.component.common;

import com.lh.IPackage.IDictionary;
import com.lh.IPackage.IResourceManager;

import java.util.ArrayList;
import java.util.Comparator;

public class WordDictionary implements IDictionary<SingleWord, SingleWord> {
    private ArrayList<Pair<SingleWord, SingleWord>> mDictionary;

    public WordDictionary() {
        mDictionary = new ArrayList<>();
    }

    public WordDictionary(IResourceManager resource) {
        this();
        String[] lines = resource.readLines();
        for (String line : lines) {
            String[] words = line.split(" - ");
            if (words.length == 2) {
                addWord(new SingleWord(words[0]), new SingleWord(words[1]));
            }
        }
    }

    @Override
    public int size() {
        return mDictionary.size();
    }

    @Override
    public SingleWord getOriginalWord(int index) {
        return mDictionary.get(index).first;
    }

    @Override
    public SingleWord getTranslatedWord(int index) {
        return mDictionary.get(index).second;
    }

    @Override
    public void addWord(SingleWord original, SingleWord translated) {
        mDictionary.add(new Pair<>(original, translated));
        mDictionary.sort(Comparator.comparing(t -> t.first.getWord()));
    }

    @Override
    public void removeWord(int index) {
        mDictionary.remove(index);
    }
}
