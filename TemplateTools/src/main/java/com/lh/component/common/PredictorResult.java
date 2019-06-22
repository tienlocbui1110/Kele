package com.lh.component.common;

import java.util.ArrayList;

public class PredictorResult {
    private float mDistance = Float.MAX_VALUE;
    private ArrayList<String> predictWord;
    private static final float FLOAT_ERROR = 0.001f;

    public PredictorResult() {
        predictWord = new ArrayList<>();
    }

    public void addResult(float distance, String word) {
        if (Math.abs(mDistance - distance) <= FLOAT_ERROR) {
            predictWord.add(word);
        } else if (distance < mDistance) {
            predictWord.clear();
            predictWord.add(word);
            mDistance = distance;
        }
    }

    public String[] getResult() {
        return predictWord.toArray(new String[0]);
    }
}
