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
                float cosineDistance = calculateAverageCosineSimilarity(baseModel, userTracking.swipeModel);
                // Cosine nghịch biến trong khoảng từ 0deg -> 90deg
                // Do đó Deg giảm dần khi cosine tăng dần.
                // Mặt khác, cos chạy từ 0 -> 1. Do đó, ta sẽ lấy 1-cos làm khoảng cách cần so sánh.
                result.addResult(1 - cosineDistance, predictWord);
            }
        }
        // Check if predict different than user.
        String[] nearestWord = result.getResult();
        for (
                String s : nearestWord) {
            if (userTracking.chosenWord.equals(s)) {
                mWriter.writeln("1\t" + userTracking.chosenWord + "\t" + s);
                return;
            }
        }

        String predicted = nearestWord.length > 0 ? nearestWord[0] : "<undefined>";
        mWriter.writeln("0\t" + userTracking.chosenWord + "\t" + predicted);
    }

    // Xem modelA và modelB là 2 vector
    // Cấu trúc: model(x0,y0,x1,y1,...xn,yn)
    // Sử dụng thuật toán cosine similarity để tìm độ tương đồng về góc của 2 vector
    private float calculateAverageCosineSimilarity(Polyline modelA, Polyline modelB) {
        float z = 25f;
        List<Point> listA = modelA.getPoints();
        List<Point> listB = modelB.getPoints();

        // 2-dimensions
        float cosineSimilar = 0f;
        for (int i = 0; i < listA.size(); i++) {
            // Build vector
            float[] vectorA = {listA.get(i).x(), listA.get(i).y(), z};
            float[] vectorB = {listB.get(i).x(), listB.get(i).y(), z};
            // Calculate cosine
            cosineSimilar += cosine(vectorA, vectorB);
        }
        return cosineSimilar / listA.size();
    }

    // A, B is n-dimensions vector
    private double cosine(float[] A, float[] B) {
        float tuso = 0f;
        float mauA = 0, mauB = 0;
        for (int i = 0; i < A.length; i++) {
            tuso += A[i] * B[i];
            mauA += A[i] * A[i];
            mauB += B[i] * B[i];
        }
        return tuso / (Math.sqrt(mauA) * Math.sqrt(mauB));
    }
}
