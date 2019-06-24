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

import java.util.ArrayList;

public class MahalanobisTemplate extends BaseTemplate {
    private IWriter mWriter;
    private ArrayList<ArrayList<SimpleMatrix>> mCov;

    public MahalanobisTemplate(String dictionaryResource, String layoutResource, int numberOfPoints, IWriter writer) {
        super(dictionaryResource, layoutResource, numberOfPoints);
        this.mWriter = writer;
        mCov = new ArrayList<>();
    }


    public MahalanobisTemplate(String layoutResource, int numberOfPoints) {
        this("vni_dic.txt", layoutResource, numberOfPoints);
    }

    public MahalanobisTemplate(String dictionaryResource, String layoutResource, int numberOfPoints) {
        this(dictionaryResource, layoutResource, numberOfPoints, new DefaultWriter());
    }

    @Override
    public void onPreWork() {
        super.onPreWork();
        // from dictionary -> S-1


        for (int i = 0; i < mDictionary.size(); i++) {
            Polyline baseModel = mDictionary.getTranslatedWord(i);
            ArrayList<SimpleMatrix> covs = new ArrayList<>();
            for (int j = 0; j < baseModel.pointCount() - 1; j++) {
                Point pointA = baseModel.getPoint(j);
                Point pointB = baseModel.getPoint(j + 1);
                // Get current vector from point
                float[] currentVector = {pointB.x() - pointA.x(), pointB.y() - pointA.y()};
                float[] OyVector = {0, 1};
                // Get angle from Oy & current vector
                double angle = MatrixUtils.getAngleFromVectorAToB(OyVector, currentVector);
                // TODO: calculate Scale Matrix
                SimpleMatrix scaleMatrix = new SimpleMatrix(new float[][]{
                        {(float) (10 + Math.sin(angle)), 0}, {0, (float) (10 + Math.cos(angle))}
                });
                // Get rotation matrix
                SimpleMatrix R = new SimpleMatrix(new double[][]{
                        {Math.cos(angle), -Math.sin(angle)},
                        {Math.sin(angle), Math.cos(angle)}
                });
                // Get covarianceMatrix of point I.
                SimpleMatrix cov = R.mult(scaleMatrix).mult(scaleMatrix).mult(R.invert());
                covs.add(cov);
            }
            covs.add(covs.get(covs.size() - 1));
            mCov.add(covs);
        }
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

        for (int i = 0; i < mDictionary.size(); i++) {
            Polyline baseModel = mDictionary.getTranslatedWord(i);
            String predictWord = mDictionary.getOriginalWord(i).getWord();
            if (baseModel.getPoint(0).x() >= minX && baseModel.getPoint(0).x() <= maxX &&
                    baseModel.getPoint(0).y() >= minY && baseModel.getPoint(0).y() <= maxY) {
                float mahalanobis = getMahalanobisDistance(mCov.get(i), userTracking.swipeModel, baseModel);
                result.addResult((float) Math.sqrt(mahalanobis), predictWord);
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

    private float getMahalanobisDistance(ArrayList<SimpleMatrix> covMatrix, Polyline user, Polyline base) {
        float mahalanobis = 0f;
        for (int i = 0; i < user.pointCount(); i++) {
            // Step 1: Build (user - base) matrix
            SimpleMatrix T = new SimpleMatrix(new float[][]{
                    {user.getPoint(i).x() - base.getPoint(i).x()},
                    {user.getPoint(i).y() - base.getPoint(i).y()}
            });
            // Step 2: Calculate mahalanobis
            mahalanobis += T.transpose().mult(covMatrix.get(i).invert()).mult(T).get(0);
        }
        return mahalanobis /= user.pointCount();
    }
}
