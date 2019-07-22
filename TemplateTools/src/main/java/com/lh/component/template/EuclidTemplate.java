package com.lh.component.template;

import com.lh.IPackage.IWriter;
import com.lh.component.common.EuclidDistance;
import com.lh.component.common.Polyline;
import com.lh.component.common.PredictorResult;
import com.lh.component.common.User;
import com.lh.component.writer.DefaultWriter;

public class EuclidTemplate extends BaseTemplate {
    private IWriter mWriter;
    private float epsilon;
    private boolean useForEpsilon;

    public EuclidTemplate(String dictionaryResource, String layoutResource, int numberOfPoints, IWriter writer, float epsilon, boolean useForEpsilon) {
        super(dictionaryResource, layoutResource, numberOfPoints);
        this.mWriter = writer;
        this.epsilon = epsilon;
        this.useForEpsilon = useForEpsilon;
    }


    public EuclidTemplate(String layoutResource, int numberOfPoints) {
        this("vni_dic.txt", layoutResource, numberOfPoints);
    }

    public EuclidTemplate(String dictionaryResource, String layoutResource, int numberOfPoints) {
        this(dictionaryResource, layoutResource, numberOfPoints, new DefaultWriter(), 0, false);
    }

    @Override
    public void onWorking() {
        for (int i = 0; i < mUserTracking.size(); i++) {
            User user = mUserTracking.getUser(i);
            // Build standard polyline if user.rawData = true
            if (user.rawData) {
                if (useForEpsilon)
                    user.swipeModel.reducing(epsilon);
                user.swipeModel.createEquidistant(numberOfPoints);
                predict(user);
            } else {
                if (user.swipeModel.pointCount() == numberOfPoints && !useForEpsilon) {
                    predict(user);
                }
            }
        }
    }

    private void predict(User userTracking) {
        PredictorResult result = new PredictorResult();
        double xRange = 10f;
        double yRange = 20f;
        double minX = userTracking.swipeModel.getPoint(0).x() - xRange;
        double maxX = userTracking.swipeModel.getPoint(0).x() + xRange;
        double minY = userTracking.swipeModel.getPoint(0).y() - yRange;
        double maxY = userTracking.swipeModel.getPoint(0).y() + yRange;

        for (int i = 0; i < mDictionary.size(); i++) {
            Polyline baseModel = mDictionary.getTranslatedWord(i);
            String predictWord = mDictionary.getOriginalWord(i).getWord();
            if (baseModel.getPoint(0).x() >= minX && baseModel.getPoint(0).x() <= maxX &&
                    baseModel.getPoint(0).y() >= minY && baseModel.getPoint(0).y() <= maxY) {
                double avgDistance = 0f;
                for (int j = 0; j < baseModel.pointCount(); j++) {
                    avgDistance += EuclidDistance.calculate(userTracking.swipeModel.getPoint(j), baseModel.getPoint(j));
                }
                result.addResult(predictWord, avgDistance / baseModel.pointCount());
            }
        }

        // Check if predict different than user.
//        List<Pair<Double, String>> nearestWord = result.getResult();
//        for (int i = 0; i < nearestWord.size(); i++) {
//            if (i > 0 && !nearestWord.get(i).first.equals(nearestWord.get(i - 1).first))
//                break;
//            if (userTracking.chosenWord.equals(nearestWord.get(i).second)) {
//                mWriter.writeln("1\t" + userTracking.chosenWord + "\t" + nearestWord.get(i).second);
//                return;
//            }
//        }
//
//        if (nearestWord.size() == 0)
//            mWriter.writeln("0\t" + userTracking.chosenWord + "\t" + "NULL");
//        else
//            mWriter.writeln("0\t" + userTracking.chosenWord + "\t" + nearestWord.get(0).second);

        if (result.getResult().size() == 0)
            mWriter.writeln("WRONG");
        else if (result.getResult().get(0).second.equals(userTracking.chosenWord)) {
            mWriter.writeln("CORRECT");
        } else {
            mWriter.writeln("WRONG");
        }
    }
}
