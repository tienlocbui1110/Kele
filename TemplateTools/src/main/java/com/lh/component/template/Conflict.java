package com.lh.component.template;

import com.lh.IPackage.IWriter;
import com.lh.component.common.*;
import com.lh.component.writer.DefaultWriter;
import org.ejml.simple.SimpleMatrix;

public class Conflict extends BaseTemplate {
    private IWriter mWriter;

    public Conflict(String dictionaryResource, String layoutResource, int numberOfPoints, IWriter writer) {
        super(dictionaryResource, layoutResource, numberOfPoints);
        this.mWriter = writer;
    }


    public Conflict(String layoutResource, int numberOfPoints) {
        this("vni_dic.txt", layoutResource, numberOfPoints);
    }

    public Conflict(String dictionaryResource, String layoutResource, int numberOfPoints) {
        this(dictionaryResource, layoutResource, numberOfPoints, new DefaultWriter());
    }

    @Override
    public void onWorking() {
        for (int i = 0; i < mDictionary.size() - 1; i++)
            for (int j = i + 1; j < mDictionary.size(); j++) {
                float euclid = getEuclid(mDictionary.getTranslatedWord(i), mDictionary.getTranslatedWord(j));
                if (euclid < 0.02f) {
                    mWriter.writeln(mDictionary.getOriginalWord(i) + " - " + mDictionary.getOriginalWord(j));
                }
            }
    }

    private float getEuclid(Polyline A, Polyline B) {
        float avgDistance = 0f;
        for (int i = 0; i < A.pointCount(); i++) {
            avgDistance += EuclidDistance.calculate(A.getPoint(i), B.getPoint(i));
        }
        return avgDistance /= A.pointCount();
    }

}
