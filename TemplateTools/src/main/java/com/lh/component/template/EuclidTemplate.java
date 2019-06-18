package com.lh.component.template;

import com.lh.IPackage.IWriter;
import com.lh.component.common.*;
import com.lh.component.writer.DefaultWriter;

public class EuclidTemplate extends BaseTemplate {
    private IWriter mWriter;

    public EuclidTemplate(String dictionaryResource, String layoutResource, int numberOfPoints, IWriter writer) {
        super(dictionaryResource, layoutResource, numberOfPoints);
        this.mWriter = writer;
    }


    public EuclidTemplate(String layoutResource, int numberOfPoints) {
        this("vni_dic.txt", layoutResource, numberOfPoints);
    }

    public EuclidTemplate(String dictionaryResource, String layoutResource, int numberOfPoints) {
        this(dictionaryResource, layoutResource, numberOfPoints, new DefaultWriter());
    }

    @Override
    public void onWorking() {
        for (int i = 0; i < mUserTracking.size(); i++) {
            predict(mUserTracking.getUser(i));
        }
    }

    private void predict(User userTracking) {
        PredictorResult result = new PredictorResult();
        float xRange = 10f;
        float yRange = 20f;
        float minX = userTracking.swipeModel.getPoint(0).x() - xRange;
        float maxX = userTracking.swipeModel.getPoint(0).x() + xRange;
        float minY = userTracking.swipeModel.getPoint(0).y() - yRange;
        float maxY = userTracking.swipeModel.getPoint(0).y() + yRange;

        for (int i = 0; i < mDictionary.size(); i++) {
            Polyline baseModel = mDictionary.getTranslatedWord(i);
            String predictWord = mDictionary.getOriginalWord(i).getWord();
            if (baseModel.getPoint(0).x() >= minX && baseModel.getPoint(0).x() <= maxX &&
                    baseModel.getPoint(0).y() >= minY && baseModel.getPoint(0).y() <= maxY) {
                float avgDistance = 0f;
                for (int j = 0; j < baseModel.pointCount(); j++) {
                    avgDistance += EuclidDistance.calculate(userTracking.swipeModel.getPoint(j), baseModel.getPoint(j));
                }
                result.addResult(avgDistance / baseModel.pointCount(), predictWord);
            }
        }

        // Check if predict different than user.
        String[] nearestWord = result.getResult();
        for (String s : nearestWord) {
            if (userTracking.chosenWord.equals(s)) {
                mWriter.writeln("OK - ID: " + userTracking.trackId + " - user word: " + userTracking.chosenWord + " - predict: " + s);
                return;
            }
        }
        String predicted = nearestWord.length > 0 ? nearestWord[0] : "<undefined>";
        mWriter.writeln("WRONG - ID: " + userTracking.trackId + " - user word: " + userTracking.chosenWord + " - predict: " + predicted);
    }
}
