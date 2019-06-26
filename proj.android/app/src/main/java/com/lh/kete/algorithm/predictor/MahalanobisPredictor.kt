package com.lh.kete.algorithm.predictor

import com.lh.kete.activity.main.MainActivity
import com.lh.kete.algorithm.Algorithm
import com.lh.kete.algorithm.common.Path
import com.lh.kete.algorithm.common.PolylineModel
import com.lh.kete.data.KeteConfig
import com.lh.kete.listener.OnWorkerThreadListener


/**
 * Created by Tien Loc Bui on 26/06/2019.
 */

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class MahalanobisPredictor(activity: MainActivity, kete: KeteConfig, listener: OnWorkerThreadListener) : Predictor(activity,
        kete,
        listener) {
    // Word[Points[matrix]]
    private lateinit var mCovInvert: ArrayList<ArrayList<Array<FloatArray>>>

    init {
        // Build invertCov
        buildCovarianceInvertMatrix(kete, listener)
        listener.onCompleted()
    }

    override fun doCalculate(obj: Any?, path: Path, callback: Algorithm.Callback<PredictorResult>) {
        doCalculateWithMem(obj, path, callback)
    }

    private fun doCalculateWithMem(obj: Any?, path: Path, callback: Algorithm.Callback<PredictorResult>) {
        // Predict
        val userModel = path.toPolylineModel()
        val result = PredictorResult()
        val xRange = 10f
        val yRange = 20f
        val minX = userModel.getPointList()[0].x - xRange
        val maxX = userModel.getPointList()[0].x + xRange
        val minY = userModel.getPointList()[0].y - yRange
        val maxY = userModel.getPointList()[0].y + yRange
        val model = getModel()

        for (i in 0 until model.size) {
            val baseModel = model[i].first
            val predictWord = model[i].second
            if (baseModel.getPointList()[0].x in minX..maxX && baseModel.getPointList()[0].y in minY..maxY) {
                val mahalanobis = getMahalanobisDistance(mCovInvert[i], userModel, baseModel)
                result.addResult(predictWord, mahalanobis)
            }
        }
        callback.onDone(obj, result)
    }

    private fun getMahalanobisDistance(covInverts: ArrayList<Array<FloatArray>>, user: PolylineModel, base: PolylineModel): Float {
        var mahalanobis = 0f
        for (i in 0 until PolylineModel.N_POINTS) {
            val covInvert = covInverts[i]
            // Step 1: T = [x,y]
            val T = floatArrayOf(user.getPointList()[i].x - base.getPointList()[i].x, user.getPointList()[i].y - base.getPointList()[i].y)
            // Step 2: Calculate mahalanobis
            // mahalanobis += T.transpose().mult(covMatrix[i].invert()).mult(T).get(0)
            // <=> maha = [x*cov[0,0] + y*cov[1,0], x*cov[0,1] + y*cov[1,1]] as res * T
            // <=> maha = res[0] * x + res[1]*y
            val tmp = floatArrayOf(T[0] * covInvert[0][0] + T[1] * covInvert[1][0], T[0] * covInvert[0][1] + T[1] * covInvert[1][1])
            mahalanobis += T[0] * tmp[0] + T[1] * tmp[1]
        }
        return mahalanobis / PolylineModel.N_POINTS
    }

    private fun buildCovarianceInvertMatrix(kete: KeteConfig, listener: OnWorkerThreadListener) {
        val infoText = "Building covariance matrix"
        val len = getModel().size
        mCovInvert = ArrayList(len)
        for (i in 0 until len) {
            val baseModel = getModel()[i].first
            val mCovInvertItem = ArrayList<Array<FloatArray>>(PolylineModel.N_POINTS)
            for (j in 0 until PolylineModel.N_POINTS - 1) {
                val pointA = baseModel.getPointList()[j]
                val pointB = baseModel.getPointList()[j + 1]
                // Step 1: Get current vector from point ( Suppose this vector is eigenvectors)
                val currentVector = floatArrayOf(pointB.x - pointA.x, pointB.y - pointA.y)
                val OyVector = floatArrayOf(0f, 1f)
                // Step 2: Get angle from Oy & current vector
                val angle = getAngleFromVectorAToB(OyVector, currentVector)
                // Step 3: Get ScaleMatrix from width & height -- Scale Matrix symbol : S
                // Suppose that every button have same width & height
                val width = kete.buttonConfig[0].width
                val height = kete.buttonConfig[0].height
                val scale = (height / width) - 1
//                val S = arrayOf(floatArrayOf((width + (height - width) * Math.abs(Math.sin(angle))).toFloat(), 0f),
//                        floatArrayOf(0f, (width + (height - width) * Math.abs(Math.cos(angle))).toFloat()))
                val S = arrayOf(floatArrayOf(width, 0f),
                        floatArrayOf(0f, height))
                // Step 4: Get rotation matrix
                val R = arrayOf(floatArrayOf(Math.cos(angle).toFloat(), (-Math.sin(angle)).toFloat()),
                        floatArrayOf(Math.sin(angle).toFloat(), Math.cos(angle).toFloat()))
                // Step 5: Get covarianceMatrix & it invert
                // Cov = R*S*S*R'
                val RInvert = get22Invert(R)
                // next = R*S
                var next = mult22(R, S)
                // next = next*S
                next = mult22(next, S)
                // cov = next*R'       
                val cov = mult22(next, RInvert)
                val covInvert = get22Invert(cov)
                mCovInvertItem.add(covInvert)
            }
            mCovInvertItem.add(mCovInvertItem[mCovInvertItem.size - 1])
            mCovInvert.add(mCovInvertItem)
            listener.onUpdate((i.toFloat() * 100 / len).toInt(), infoText)
        }
    }

    private fun getAngleFromVectorAToB(a: FloatArray, b: FloatArray): Double {
        return Math.atan2(a[1].toDouble(), a[0].toDouble()) - Math.atan2(b[1].toDouble(), b[0].toDouble())
    }

    private fun get22Invert(matrix: Array<FloatArray>): Array<FloatArray> {
        val det = matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]
        return arrayOf(
                floatArrayOf(matrix[1][1] / det, -matrix[0][1] / det),
                floatArrayOf(-matrix[1][0] / det, matrix[0][0] / det)
        )
    }

    private fun mult22(A: Array<FloatArray>, B: Array<FloatArray>): Array<FloatArray> {
        return arrayOf(
                floatArrayOf(A[0][0] * B[0][0] + A[0][1] * B[1][0], A[0][0] * B[0][1] + A[0][1] * B[1][1]),
                floatArrayOf(A[1][0] * B[0][0] + A[1][1] * B[1][0], A[1][0] * B[0][1] + A[1][1] * B[1][1])
        )
    }
}