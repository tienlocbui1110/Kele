package com.lh.component.template;

import com.lh.IPackage.IWriter;
import com.lh.component.common.*;
import com.lh.component.writer.DefaultWriter;

import java.util.List;

public class CosineTemplate extends BaseTemplate {

    private IWriter mWriter;

    public CosineTemplate(String layoutResource, int numberOfPoints) {
        this("vni_dic.txt", layoutResource, numberOfPoints);
    }

    public CosineTemplate(String dictionaryResource, String layoutResource, int numberOfPoints) {
        this(dictionaryResource, layoutResource, numberOfPoints, new DefaultWriter());
    }

    public CosineTemplate(String dictionaryResource, String layoutResource, int numberOfPoints, IWriter writer) {
        super(dictionaryResource, layoutResource, numberOfPoints);
        this.mWriter = writer;
    }

    @Override
    public void onWorking() {
        for (int i = 0; i < mUserTracking.size(); i++) {
            User user = mUserTracking.getUser(i);
            // Build standard polyline if user.rawData = true
            if (user.rawData) {
                user.swipeModel.createEquidistant(numberOfPoints);
                predict(user);
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
                double cosineDistance = calculateAverageCosineSimilarity(baseModel, userTracking.swipeModel);
                // Cosine nghịch biến trong khoảng từ 0deg -> 90deg
                // Do đó Deg giảm dần khi cosine tăng dần.
                // Mặt khác, cos chạy từ 0 -> 1. Do đó, ta sẽ lấy 1-cos làm khoảng cách cần so sánh.
                result.addResult(predictWord, 1 - cosineDistance);
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

    // Xem modelA và modelB là 2 vector
    // Cấu trúc: model(x0,y0,x1,y1,...xn,yn)
    // Sử dụng thuật toán cosine similarity để tìm độ tương đồng về góc của 2 vector
    private double calculateAverageCosineSimilarity(Polyline modelA, Polyline modelB) {
        double z = 25f;
        List<Point> listA = modelA.getPoints();
        List<Point> listB = modelB.getPoints();

        // 2-dimensions
        double cosineSimilar = 0f;
        for (int i = 0; i < listA.size(); i++) {
            // Build vector
            double[] vectorA = {listA.get(i).x(), listA.get(i).y(), z};
            double[] vectorB = {listB.get(i).x(), listB.get(i).y(), z};
            // Calculate cosine
            cosineSimilar += cosine(vectorA, vectorB);
        }
        return cosineSimilar / listA.size();
    }

    // A, B is n-dimensions vector
    private double cosine(double[] A, double[] B) {
        double tuso = 0f;
        double mauA = 0, mauB = 0;
        for (int i = 0; i < A.length; i++) {
            tuso += A[i] * B[i];
            mauA += A[i] * A[i];
            mauB += B[i] * B[i];
        }
        return tuso / (Math.sqrt(mauA) * Math.sqrt(mauB));
    }
}
