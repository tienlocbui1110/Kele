package com.lh.IPackage;

public interface IDictionary<A extends IWord, B extends IWord> {
    int size();

    A getOriginalWord(int index);

    B getTranslatedWord(int index);

    void addWord(A original, B translated);

    void removeWord(int index);
}
