package com.lh.component.template;

import com.lh.IPackage.IWriter;
import com.lh.component.common.Point;
import com.lh.component.common.Polyline;
import com.lh.component.common.PredictorResult;
import com.lh.component.common.User;
import com.lh.component.matrix.MatrixUtils;
import com.lh.component.writer.DefaultWriter;
import org.ejml.data.SingularMatrixException;
import org.ejml.simple.SimpleMatrix;

public class MahalanobisTemplate extends BaseTemplate {
    private IWriter mWriter;

    public MahalanobisTemplate(String dictionaryResource, String layoutResource, int numberOfPoints, IWriter writer) {
        super(dictionaryResource, layoutResource, numberOfPoints);
        this.mWriter = writer;
    }


    public MahalanobisTemplate(String layoutResource, int numberOfPoints) {
        this("vni_dic.txt", layoutResource, numberOfPoints);
    }

    public MahalanobisTemplate(String dictionaryResource, String layoutResource, int numberOfPoints) {
        this(dictionaryResource, layoutResource, numberOfPoints, new DefaultWriter());
    }

    @Override
    public void onWorking() {
        for (int i = 0; i < mUserTracking.size(); i++) {
            try {
                predict(mUserTracking.getUser(i));
            } catch (SingularMatrixException e) {
                mWriter.writeln("USER-TRACKING-FAILED");
            }
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

        // Building inv(cov(user))
        SimpleMatrix userMatrix = buildMatrix(userTracking.swipeModel);
        SimpleMatrix userInvertCov = MatrixUtils.covariance(userMatrix).invert();
        SimpleMatrix userMean = MatrixUtils.mean(userMatrix);

        for (int i = 0; i < mDictionary.size(); i++) {
            Polyline baseModel = mDictionary.getTranslatedWord(i);
            String predictWord = mDictionary.getOriginalWord(i).getWord();
            if (baseModel.getPoint(0).x() >= minX && baseModel.getPoint(0).x() <= maxX &&
                    baseModel.getPoint(0).y() >= minY && baseModel.getPoint(0).y() <= maxY) {
                float mahalanobis = 0f;
                SimpleMatrix baseMean = MatrixUtils.mean(buildMatrix(baseModel));
                SimpleMatrix point = new SimpleMatrix(new double[][]{
                        {baseMean.get(0, 0)},
                        {baseMean.get(1, 0)}
                });
                point = point.minus(userMean);
                SimpleMatrix res = point.transpose().mult(userInvertCov).mult(point);
                mahalanobis += Math.sqrt(res.get(0, 0));
                result.addResult(mahalanobis, predictWord);
            }
        }

        // Check if predict different than user.
        String[] nearestWord = result.getResult();
        for (
                String s : nearestWord) {
            if (userTracking.chosenWord.equals(s)) {
                mWriter.writeln("OK - ID: " + userTracking.trackId + " - user word: " + userTracking.chosenWord + " - predict: " + s);
                return;
            }
        }

        String predicted = nearestWord.length > 0 ? nearestWord[0] : "<undefined>";
        mWriter.writeln("WRONG - ID: " + userTracking.trackId + " - user word: " + userTracking.chosenWord + " - predict: " + predicted);
    }

    // x1 x2 x3 xn
    // y1 y2 y3 yn
    private SimpleMatrix buildMatrix(Polyline polyline) {
        SimpleMatrix matrix = new SimpleMatrix(2, polyline.pointCount());
        for (int i = 0; i < polyline.pointCount(); i++) {
            Point point = polyline.getPoint(i);
            matrix.setRow(0, i, point.x());
            matrix.setRow(1, i, point.y());
        }
        return matrix;
    }
}
