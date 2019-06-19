package com.lh.component.common;

import com.lh.IPackage.IDictionary;

import java.util.ArrayList;
import java.util.Comparator;

public class SortDictionary<T> implements IDictionary<SingleWord, T> {
    private ArrayList<Pair<SingleWord, T>> mDictionary;

    public SortDictionary() {
        mDictionary = new ArrayList<>();
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
    public T getTranslatedWord(int index) {
        return mDictionary.get(index).second;
    }

    @Override
    public void addWord(SingleWord original, T translated) {
        mDictionary.add(new Pair<>(original, translated));
        mDictionary.sort(Comparator.comparing(t -> t.first.getWord()));
    }

    @Override
    public void removeWord(int index) {
        mDictionary.remove(index);
    }
}
