package com.lh.component.matrix;

import org.ejml.simple.SimpleMatrix;

/**
 * Class MatrixUtils will handle matrix with n X variable.
 * Receive X = [X1, X2, XN].transpose
 */
@SuppressWarnings("WeakerAccess")
public class MatrixUtils {

    /**
     * This is sample function that implement mahalanobis distance.
     * @param args
     */
    private static void main(String[] args) {
        SimpleMatrix x = new SimpleMatrix(new double[][]{
                {0, 0, 1, 2},
                {0, 1, 1, 1}
        });
        SimpleMatrix cov = covariance(x);
        cov = cov.invert();
        // --------------------- //
        SimpleMatrix needCalculate = new SimpleMatrix(new double[][]{{0}, {1}});
        SimpleMatrix m = mean(x);
        needCalculate = needCalculate.minus(m);
        SimpleMatrix res = needCalculate.transpose().mult(cov).mult(needCalculate);
        res.print("[mahalanobis]^2 = %f");
    }

    /**
     * @param X : [X1, X2, ... Xn].transpose
     *          X has m row, n column.
     * @return covariance matrix
     */
    public static SimpleMatrix covariance(SimpleMatrix X) {
        int m = X.numRows();
        int n = X.numCols();
        // Step 1: Mean: mx1
        SimpleMatrix Xmean = mean(X);
        SimpleMatrix XClone = X.createLike();
        // Step 2: every Xi -> x[i] = x[i] - mean(X)
        for (int i = 0; i < m; i++) {
            XClone.equation("XC(i,:) = X(i,:) - Xmean", X, "X", XClone, "XC", Xmean.get(i, 0), "Xmean", i, "i");
        }
        // Covariance
        SimpleMatrix Cov = new SimpleMatrix(m, m);
        SimpleMatrix tmp = new SimpleMatrix(1, n);
        for (int i = 0; i < m; i++)
            for (int j = 0; j < m; j++) {
                Cov.equation("tmp(0,:) = X(i,:).*X(j,:)", tmp, "tmp", XClone, "X", i, "i", j, "j");
                Cov.set(i, j, tmp.elementSum() / (n - 1));
            }
        return Cov;
    }

    public static SimpleMatrix mean(SimpleMatrix X) {
        SimpleMatrix result = new SimpleMatrix(X.numRows(), 1);

        for (int i = 0; i < X.numRows(); i++) {
            result.setRow(i, 0, X.rows(i, i + 1).elementSum() / X.numCols());
        }
        return result;
    }
}
