package com.lh.kete.algorithm.predictor

import com.lh.kete.activity.main.MainActivity
import com.lh.kete.algorithm.Algorithm
import com.lh.kete.algorithm.common.Path
import com.lh.kete.algorithm.common.PolylineModel
import com.lh.kete.data.KeteConfig
import com.lh.kete.listener.OnWorkerThreadListener

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class CosineSimilarity(activity: MainActivity, kete: KeteConfig, listener: OnWorkerThreadListener) : Predictor(activity,
        kete,
        listener) {
    init {
        // Do nothing
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
                val avgDistance = calculateAverageCosineSimilarity(baseModel, userModel)
                // Cosine nghịch biến trong khoảng từ 0deg -> 90deg
                // Do đó Deg giảm dần khi cosine tăng dần.
                // Mặt khác, cos chạy từ 0 -> 1. Do đó, ta sẽ lấy 1-cos làm khoảng cách cần so sánh.
                result.addResult(predictWord, 1 - avgDistance)
            }
        }
        callback.onDone(obj, result)
    }


    // Xem modelA và modelB là 2 vector
    // Cấu trúc: model(x0,y0,x1,y1,...xn,yn)
    // Sử dụng thuật toán cosine similarity để tìm độ tương đồng về góc của 2 vector
    private fun calculateAverageCosineSimilarity(modelA: PolylineModel, modelB: PolylineModel): Float {
        val listA = modelA.getPointList()
        val listB = modelB.getPointList()

        var scalar = 0f
        var sumX = 0f
        var sumY = 0f

        for (i in 0 until PolylineModel.N_POINTS) {
            scalar += (listA[i].x * listB[i].x + listA[i].y * listB[i].y)
            sumX += (listA[i].x * listA[i].x + listA[i].y * listA[i].y)
            sumY += (listB[i].x * listB[i].x + listB[i].y * listB[i].y)
        }
        return scalar / (Math.sqrt(sumX.toDouble()) * Math.sqrt(sumY.toDouble())).toFloat()
    }
}