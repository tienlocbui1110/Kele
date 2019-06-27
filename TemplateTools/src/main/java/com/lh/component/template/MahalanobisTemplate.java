package com.lh.component.template;

import com.lh.IPackage.IWriter;
import com.lh.component.common.*;
import com.lh.component.matrix.MatrixUtils;
import com.lh.component.writer.DefaultWriter;
import org.ejml.data.SingularMatrixException;

import java.util.ArrayList;
import java.util.List;

public class MahalanobisTemplate extends BaseTemplate {
    private IWriter mWriter;
    private ArrayList<ArrayList<float[][]>> mCovInvert;

    public MahalanobisTemplate(String dictionaryResource, String layoutResource, int numberOfPoints, IWriter writer) {
        super(dictionaryResource, layoutResource, numberOfPoints);
        this.mWriter = writer;
        mCovInvert = new ArrayList<>();
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
            ArrayList<float[][]> mCovInvertItem = new ArrayList<>();
            for (int j = 0; j < baseModel.pointCount() - 1; j++) {
                Point pointA = baseModel.getPoint(j);
                Point pointB = baseModel.getPoint(j + 1);
                // Get current vector from point
                float[] currentVector = {pointB.x() - pointA.x(), pointB.y() - pointA.y()};
                float[] OyVector = {0, 1};
                // Get angle from Oy & current vector
                double angle = MatrixUtils.getAngleFromVectorAToB(OyVector, currentVector);
                // Scale matrix
                float width = 9.1f, height = 22.5f;
                float[][] S = {{width / 2, 0f}, {0f, height / 2}};
                // Get rotation matrix
                float[][] R = {{(float) Math.cos(angle), (float) -Math.sin(angle)},
                        {(float) Math.sin(angle), (float) Math.cos(angle)}};
//                float [][] R = {{1,0},{0,1}};
                //Get covarianceMatrix & it invert
                // Cov = R*S*S*R'
                float[][] RInvert = get22Invert(R);
                // next = R*S
                float[][] next = mult22(R, S);
                // next = next*S
                next = mult22(next, S);
                // cov = next*R'
                float[][] cov = mult22(next, RInvert);
                float[][] covInvert = get22Invert(cov);
                mCovInvertItem.add(covInvert);
            }
            mCovInvertItem.add(mCovInvertItem.get(mCovInvertItem.size() - 1));
            mCovInvert.add(mCovInvertItem);
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
                float mahalanobis = getMahalanobisDistance(mCovInvert.get(i), userTracking.swipeModel, baseModel);
                result.addResult(predictWord, mahalanobis);
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

    private float getMahalanobisDistance(ArrayList<float[][]> covInverts, Polyline user, Polyline base) {
        float mahalanobis = 0f;
        for (int i = 0; i < user.pointCount(); i++) {
            float[][] covInvert = covInverts.get(i);
            // Step 1: T = [x,y]
            float[] T = {user.getPoint(i).x() - base.getPoint(i).x(), user.getPoint(i).y() - base.getPoint(i).y()};
            // Step 2: Calculate mahalanobis
            // mahalanobis += T.transpose().mult(covMatrix[i].invert()).mult(T).get(0)
            // <=> maha = [x*cov[0,0] + y*cov[1,0], x*cov[0,1] + y*cov[1,1]] as res * T
            // <=> maha = res[0] * x + res[1]*y
            float[] tmp = {T[0] * covInvert[0][0] + T[1] * covInvert[1][0], T[0] * covInvert[0][1] + T[1] * covInvert[1][1]};
            mahalanobis += Math.sqrt(T[0] * tmp[0] + T[1] * tmp[1]);
        }
        return mahalanobis /= user.pointCount();
    }

    private float[][] get22Invert(float[][] matrix) {
        float det = matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        return new float[][]{
                {matrix[1][1] / det, -matrix[0][1] / det},
                {-matrix[1][0] / det, matrix[0][0] / det}
        };
    }

    private float[][] mult22(float[][] A, float[][] B) {
        return new float[][]{
                {A[0][0] * B[0][0] + A[0][1] * B[1][0], A[0][0] * B[0][1] + A[0][1] * B[1][1]},
                {A[1][0] * B[0][0] + A[1][1] * B[1][0], A[1][0] * B[0][1] + A[1][1] * B[1][1]}
        };
    }
}
