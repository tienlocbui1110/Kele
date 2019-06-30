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
    private ArrayList<ArrayList<double[][]>> mCovInvert;

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
            ArrayList<double[][]> mCovInvertItem = new ArrayList<>();
            for (int j = 0; j < baseModel.pointCount() - 1; j++) {
                Point pointA = baseModel.getPoint(j);
                Point pointB = baseModel.getPoint(j + 1);
                // Get current vector from point
                double[] currentVector = {pointB.x() - pointA.x(), pointB.y() - pointA.y()};
                double[] OyVector = {0, 1};
                // Get angle from Oy & current vector
                double angle = MatrixUtils.getAngleFromVectorAToB(OyVector, currentVector);
                // Scale matrix
                double width = 9.1f, height = 22.5f;
                double[][] S = {{width / 2, 0f}, {0f, height / 2}};
                // Get rotation matrix
                double[][] R = {{Math.cos(angle), -Math.sin(angle)},
                        {Math.sin(angle), Math.cos(angle)}};
//                float [][] R = {{1,0},{0,1}};
                //Get covarianceMatrix & it invert
                // Cov = R*S*S*R'
                double[][] RInvert = get22Invert(R);
                // next = R*S
                double[][] next = mult22(R, S);
                // next = next*S
                next = mult22(next, S);
                // cov = next*R'
                double[][] cov = mult22(next, RInvert);
                double[][] covInvert = get22Invert(cov);
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
                User user = mUserTracking.getUser(i);
                // Build standard polyline if user.rawData = true
                if (user.rawData) {
                    user.swipeModel.createEquidistant(numberOfPoints);
                    predict(user);
                }
            } catch (SingularMatrixException e) {
                mWriter.writeln("USER-TRACKING-FAILED");
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
                double mahalanobis = getMahalanobisDistance(mCovInvert.get(i), userTracking.swipeModel, baseModel);
                result.addResult(predictWord, mahalanobis);
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

    private double getMahalanobisDistance(ArrayList<double[][]> covInverts, Polyline user, Polyline base) {
        double mahalanobis = 0f;
        for (int i = 0; i < user.pointCount(); i++) {
            double[][] covInvert = covInverts.get(i);
            // Step 1: T = [x,y]
            double[] T = {user.getPoint(i).x() - base.getPoint(i).x(), user.getPoint(i).y() - base.getPoint(i).y()};
            // Step 2: Calculate mahalanobis
            // mahalanobis += T.transpose().mult(covMatrix[i].invert()).mult(T).get(0)
            // <=> maha = [x*cov[0,0] + y*cov[1,0], x*cov[0,1] + y*cov[1,1]] as res * T
            // <=> maha = res[0] * x + res[1]*y
            double[] tmp = {T[0] * covInvert[0][0] + T[1] * covInvert[1][0], T[0] * covInvert[0][1] + T[1] * covInvert[1][1]};
            mahalanobis += Math.sqrt(T[0] * tmp[0] + T[1] * tmp[1]);
        }
        return mahalanobis /= user.pointCount();
    }

    private double[][] get22Invert(double[][] matrix) {
        double det = matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        return new double[][]{
                {matrix[1][1] / det, -matrix[0][1] / det},
                {-matrix[1][0] / det, matrix[0][0] / det}
        };
    }

    private double[][] mult22(double[][] A, double[][] B) {
        return new double[][]{
                {A[0][0] * B[0][0] + A[0][1] * B[1][0], A[0][0] * B[0][1] + A[0][1] * B[1][1]},
                {A[1][0] * B[0][0] + A[1][1] * B[1][0], A[1][0] * B[0][1] + A[1][1] * B[1][1]}
        };
    }
}
