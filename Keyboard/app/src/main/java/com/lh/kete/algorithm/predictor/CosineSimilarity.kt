//package com.lh.kete.algorithm.predictor
//
//import com.lh.kete.activity.main.MainActivity
//import com.lh.kete.algorithm.Algorithm
//import com.lh.kete.algorithm.common.Path
//import com.lh.kete.algorithm.common.PolylineModel
//import com.lh.kete.data.KeteConfig
//import com.lh.kete.listener.OnWorkerThreadListener
//
//
//@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
//class CosineSimilarity(activity: MainActivity, kete: KeteConfig, listener: OnWorkerThreadListener) : Predictor(activity,
//        kete,
//        listener) {
//    init {
//        // Do nothing
//        listener.onCompleted()
//    }
//
//    override fun doCalculate(obj: Any?, path: Path, callback: Algorithm.Callback<PredictorResult>) {
//        doCalculateWithMem(obj, path, callback)
//    }
//
//    private fun doCalculateWithMem(obj: Any?, path: Path, callback: Algorithm.Callback<PredictorResult>) {
//        // Predict
//        val userModel = path.toPolylineModel()
//        val result = PredictorResult()
//        val xRange = 10f
//        val yRange = 20f
//        val minX = userModel.getPointList()[0].mX - xRange
//        val maxX = userModel.getPointList()[0].mX + xRange
//        val minY = userModel.getPointList()[0].mY - yRange
//        val maxY = userModel.getPointList()[0].mY + yRange
//        val model = getModel()
//
//        for (i in 0 until model.size) {
//            val baseModel = model[i].first
//            val predictWord = model[i].second
//            if (baseModel.getPointList()[0].mX in minX..maxX && baseModel.getPointList()[0].mY in minY..maxY) {
//                val avgDistance = calculateAverageCosineSimilarity(baseModel, userModel)
//                // Cosine nghịch biến trong khoảng từ 0deg -> 90deg
//                // Do đó Deg giảm dần khi cosine tăng dần.
//                // Mặt khác, cos chạy từ 0 -> 1. Do đó, ta sẽ lấy 1-cos làm khoảng cách cần so sánh.
//                result.addResult(predictWord, 1 - avgDistance)
//            }
//        }
//        callback.onDone(obj, result)
//    }
//
//
//    // Xem modelA và modelB là 2 vector
//    // Cấu trúc: model(x0,y0,x1,y1,...xn,yn)
//    // Sử dụng thuật toán cosine similarity để tìm độ tương đồng về góc của 2 vector
//    private fun calculateAverageCosineSimilarity(modelA: PolylineModel, modelB: PolylineModel): Float {
//        val z = 25f
//        val listA = modelA.getPointList()
//        val listB = modelB.getPointList()
//
//        // 2-dimensions
//        var cosineSimilar = 0f
//        for (i in listA.indices) {
//            // Build vector
//            val vectorA = floatArrayOf(listA[i].mX, listA[i].mY, z)
//            val vectorB = floatArrayOf(listB[i].mX, listB[i].mY, z)
//            // Calculate cosine
//            cosineSimilar += cosine(vectorA, vectorB).toFloat()
//        }
//        return cosineSimilar / listA.size
//    }
//
//    // A, B is n-dimensions vector
//    private fun cosine(A: FloatArray, B: FloatArray): Double {
//        var tuso = 0f
//        var mauA = 0f
//        var mauB = 0f
//        for (i in A.indices) {
//            tuso += A[i] * B[i]
//            mauA += A[i] * A[i]
//            mauB += B[i] * B[i]
//        }
//        return tuso / (Math.sqrt(mauA.toDouble()) * Math.sqrt(mauB.toDouble()))
//    }
//}