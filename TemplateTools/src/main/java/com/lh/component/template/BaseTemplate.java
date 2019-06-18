package com.lh.component.template;

import com.lh.IPackage.IDictionary;
import com.lh.IPackage.ILayoutManager;
import com.lh.IPackage.ITemplate;
import com.lh.component.common.*;

public abstract class BaseTemplate implements ITemplate {
    private String mDictionaryResource;
    private String mLayoutResource;
    private int numberOfPoints;
    private static final String DEFAULT_RESOURCE = "vni_dic.txt";

    protected IDictionary<SingleWord, Polyline> mDictionary;
    protected ILayoutManager mLayoutManager;
    protected UserTracking mUserTracking;

    public BaseTemplate(String layoutResource, int numberOfPoints) {
        this(DEFAULT_RESOURCE, layoutResource, numberOfPoints);
    }

    public BaseTemplate(String dictionaryResource, String layoutResource, int numberOfPoints) {
        this.mDictionaryResource = dictionaryResource;
        this.mLayoutResource = layoutResource;
        this.numberOfPoints = numberOfPoints;
    }

    @Override
    public void onPreWork() {
        ResourceReader dictionaryResourceReader = new ResourceReader(mDictionaryResource);
        ResourceReader layoutResourceReader = new ResourceReader(mLayoutResource);

        WordDictionary wordDictionary = new WordDictionary(dictionaryResourceReader);
        mLayoutManager = new LayoutManager(layoutResourceReader);
        PolylineBuilder polylineBuilder = new PolylineBuilder(mLayoutManager);
        mUserTracking = new UserTracking(mLayoutManager, numberOfPoints);
        mDictionary = new PolylineDictionary();

        for (int i = 0; i < wordDictionary.size(); i++) {
            SingleWord translatedWord = wordDictionary.getTranslatedWord(i);
            Polyline translatedLines = polylineBuilder.from(translatedWord.getWord(), numberOfPoints);
            mDictionary.addWord(wordDictionary.getOriginalWord(i), translatedLines);
        }
    }

    @Override
    public void onPostWork() {
        // Do nothing
    }
}
