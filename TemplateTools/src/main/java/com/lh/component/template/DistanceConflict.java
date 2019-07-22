package com.lh.component.template;

import com.lh.IPackage.IWriter;
import com.lh.component.common.*;
import com.lh.component.writer.DefaultWriter;

public class DistanceConflict extends BaseTemplate {
    private IWriter mWriter;
    private final double error = 0.00000001;
    private boolean isConflict;

    public DistanceConflict(String dictionaryResource, String layoutResource, int numberOfPoints, IWriter writer, boolean isConflict) {
        super(dictionaryResource, layoutResource, numberOfPoints);
        this.mWriter = writer;
        this.isConflict = isConflict;
    }


    public DistanceConflict(String layoutResource, int numberOfPoints) {
        this("vni_dic.txt", layoutResource, numberOfPoints);
    }

    public DistanceConflict(String dictionaryResource, String layoutResource, int numberOfPoints) {
        this(dictionaryResource, layoutResource, numberOfPoints, new DefaultWriter(), true);
    }

    @Override
    public void onWorking() {
        boolean[] check = new boolean[mDictionary.size()];
        for (int i = 0; i < mDictionary.size() - 1; i++)
            for (int j = i + 1; j < mDictionary.size(); j++) {
                double euclid = getEuclid(mDictionary.getTranslatedWord(i), mDictionary.getTranslatedWord(j));
                if (isConflict) {
                    // distance = 0
                    if (euclid <= error) {
                        check[i] = true;
                        check[j] = true;
                    }
                } else {
                    // distance 0 <= distance <= 0.5
                    if (euclid > error && euclid <= 0.5 + error) {
                        check[i] = true;
                        check[j] = true;
                    }
                }
            }
        int count = 0;
        for (boolean b : check) {
            if (b == true)
                count++;
        }
        mWriter.write(String.valueOf(count));
    }

    private double getEuclid(Polyline A, Polyline B) {
        double avgDistance = 0f;
        for (int i = 0; i < A.pointCount(); i++) {
            avgDistance += EuclidDistance.calculate(A.getPoint(i), B.getPoint(i));
        }
        return avgDistance / A.pointCount();
    }

}
