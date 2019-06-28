package com.lh.component.template;

import com.lh.IPackage.IWriter;
import com.lh.component.common.*;
import com.lh.component.writer.DefaultWriter;

import java.util.List;

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
            User user = mUserTracking.getUser(i);
            predict(user);
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
                result.addResult(predictWord, avgDistance / baseModel.pointCount());
            }
        }

        // Check if predict different than user.
        List<Pair<Float, String>> nearestWord = result.getResult();
        for (int i = 0; i < nearestWord.size(); i++) {
            if (i > 0 && !nearestWord.get(i).first.equals(nearestWord.get(i - 1).first))
                break;
            if (userTracking.chosenWord.equals(nearestWord.get(i).second)) {
                mWriter.writeln("1\t" + userTracking.chosenWord + "\t" + nearestWord.get(i).second);
                return;
            }
        }

        mWriter.writeln("0\t" + userTracking.chosenWord + "\t" + nearestWord.get(0).second);
    }
}
