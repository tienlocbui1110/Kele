package com.lh.component.common;

import com.lh.IPackage.IDictionary;

import java.util.ArrayList;
import java.util.Comparator;

public class PolylineDictionary implements IDictionary<SingleWord, Polyline> {
    private ArrayList<Pair<SingleWord, Polyline>> mDictionary;

    public PolylineDictionary() {
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
    public Polyline getTranslatedWord(int index) {
        return mDictionary.get(index).second;
    }

    @Override
    public void addWord(SingleWord original, Polyline translated) {
        mDictionary.add(new Pair<>(original, translated));
        mDictionary.sort(Comparator.comparing(t -> t.first.getWord()));
    }

    @Override
    public void removeWord(int index) {
        mDictionary.remove(index);
    }
}
