package com.lh.kete.algorithm.predictor

import android.content.Context
import android.util.Log
import com.lh.kete.algorithm.Algorithm
import com.lh.kete.algorithm.common.Path
import com.lh.kete.algorithm.common.PolylineModel
import com.lh.kete.data.KeteConfig
import com.lh.kete.listener.OnWorkerThreadListener
import com.lh.kete.utils.KeteUtils

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class EuclidPredictor(context: Context, kete: KeteConfig, listener: OnWorkerThreadListener) : Predictor(context,
        kete,
        listener) {
    init {
        // Do nothing
        listener.onCompleted()
    }

    override fun doCalculate(obj: Any?, path: Path, callback: Algorithm.Callback<PredictorResult>) {
        val begin = System.currentTimeMillis()
        doCalculateWithMem(obj, path, callback)
        val end = System.currentTimeMillis()
        Log.d("Predictor", (end-begin).toString())
    }

    private fun doCalculateWithMem(obj: Any?, path: Path, callback: Algorithm.Callback<PredictorResult>) {
        // Predict
        val userModel = path.toPolylineModel()
        val result = PredictorResult()
        val xRange = 10f
        val yRange = 20f
        val minX = userModel.getPointList()[0].mX - xRange
        val maxX = userModel.getPointList()[0].mX + xRange
        val minY = userModel.getPointList()[0].mY - yRange
        val maxY = userModel.getPointList()[0].mY + yRange
        val model = getModel()

        for (i in 0 until model.size) {
            val baseModel = model[i].first
            val predictWord = model[i].second
            if (baseModel.getPointList()[0].mX in minX..maxX && baseModel.getPointList()[0].mY in minY..maxY) {
                var avgDistance = 0f
                for (j in 0 until PolylineModel.N_POINTS) {
                    avgDistance += KeteUtils.distance(
                            userModel.getPointList()[j].mX,
                            userModel.getPointList()[j].mY,
                            baseModel.getPointList()[j].mX,
                            baseModel.getPointList()[j].mY
                    )
                }
                result.addResult(predictWord, avgDistance / PolylineModel.N_POINTS)
            }
        }
        callback.onDone(obj, result)
    }
}