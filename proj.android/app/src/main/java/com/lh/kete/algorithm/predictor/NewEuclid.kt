//package com.lh.kete.algorithm.predictor
//
//import android.app.Activity
//import com.lh.kete.activity.main.MainActivity
//import com.lh.kete.algorithm.Algorithm
//import com.lh.kete.algorithm.common.Path
//import com.lh.kete.algorithm.common.Point
//import com.lh.kete.algorithm.common.Polyline
//import com.lh.kete.algorithm.common.PolylineModel
//import com.lh.kete.data.KeteConfig
//import com.lh.kete.db.KeteContract
//import com.lh.kete.db.SQLiteHelper
//import com.lh.kete.listener.OnWorkerThreadListener
//import com.lh.kete.utils.KeteUtils
//import it.unisa.di.cluelab.polyrec.Gesture
//import it.unisa.di.cluelab.polyrec.PolyRecognizerGSS
//import it.unisa.di.cluelab.polyrec.TPoint
//import java.lang.Exception
//
//
///**
// * Created by Tien Loc Bui on 27/06/2019.
// */
//class NewEuclid(activity: MainActivity,
//                kete: KeteConfig,
//                listener: OnWorkerThreadListener) : Predictor(activity, kete, listener) {
//
//    private val buttonMapper = HashMap<String, Point>()
//    private val memModel = ArrayList<Pair<Polyline, String>>()
//
//    init {
//        //kete to point"
//        for (i in 0 until kete.buttonConfig.size) {
//            val button = kete.buttonConfig[i]
//            if (button.char == null)
//                continue
//            val fX = button.x + button.width / 2
//            val fY = button.y + button.height / 2
//            buttonMapper[button.computingChar ?: button.char] = Point(fX, fY)
//        }
//        buildBaseModel(activity, listener)
//        listener.onCompleted()
//    }
//
//    private fun buildBaseModel(activity: Activity, listener: OnWorkerThreadListener) {
//        val dbHelper = SQLiteHelper(activity)
//        val db = dbHelper.writableDatabase
//
//        // Alternative Dictionary Cursor
//        val altDicCursor = db.rawQuery("""
//                SELECT ${KeteContract.AltDictionary.COL_ALT_WORD}, ${KeteContract.AltDictionary.COL_ORG_WORD}
//                FROM ${KeteContract.TABLE_ALTERNATIVE_DICTIONARY}
//            """.trimIndent(), null)
//        // Với mỗi từ trong bảng AltDictionary, xây dựng Mem model từ đây.
//        val alternativeIndex = altDicCursor.getColumnIndex(KeteContract.AltDictionary.COL_ALT_WORD)
//        val originIndex = altDicCursor.getColumnIndex(KeteContract.AltDictionary.COL_ORG_WORD)
//        val infoText = "Building swipe model"
//        try {
//            db.beginTransaction()
//            while (!altDicCursor.isLast) {
//                altDicCursor.moveToNext()
//                val alternativeWord = altDicCursor.getString(alternativeIndex)
//                val originWord = altDicCursor.getString(originIndex)
//                val model = buildPolylineModelFromString(alternativeWord)
//                memModel.add(Pair(model, originWord))
//                if (altDicCursor.position % 100 == 0) {
//                    listener.onUpdate(altDicCursor.position * 100 / altDicCursor.count, infoText)
//                }
//            }
//            altDicCursor.close()
//            db.setTransactionSuccessful()
//        } finally {
//            db.endTransaction()
//        }
//
//        dbHelper.close()
//    }
//
//    private fun buildPolylineModelFromString(charSeq: String): Polyline {
//        val pathBuilder = Path.Builder()
//        for (i in 0 until charSeq.length) {
//            val character = charSeq[i].toString()
//            buttonMapper[character]?.let {
//                pathBuilder.appendPoint(it)
//            }
//        }
//
//        val path = pathBuilder.build()
//        val polyline = Polyline()
//        path.getPointList().forEach {
//            polyline.addPoint(it)
//        }
//        return polyline
//    }
//
//    override fun doCalculate(obj: Any?, path: Path, callback: Algorithm.Callback<PredictorResult>) {
//        // Bước 1: reduceNoise
//        val userPolyline = Polyline()
//        path.getPointList().forEach {
//            userPolyline.addPoint(it)
//        }
//        doCalculateWithMem(obj, userPolyline, callback)
//    }
//
//    private fun doCalculateWithMem(obj: Any?, path: Polyline, callback: Algorithm.Callback<PredictorResult>) {
//        // Predict
//        val userModel = path.clone()
//        userModel.createEquidistant(PolylineModel.N_POINTS)
//        val result = PredictorResult()
//        val xRange = 10f
//        val yRange = 20f
//        val minX = userModel.getPoint(0).mX - xRange
//        val maxX = userModel.getPoint(0).mX + xRange
//        val minY = userModel.getPoint(0).mY - yRange
//        val maxY = userModel.getPoint(0).mY + yRange
//        val model = getModel()
//
//        for (i in 0 until model.size) {
//            val baseModel = model[i].first
//            val predictWord = model[i].second
//            if (baseModel.getPointList()[0].mX in minX..maxX && baseModel.getPointList()[0].mY in minY..maxY) {
//                var avgDistance = 0f
//                for (j in 0 until PolylineModel.N_POINTS) {
//                    avgDistance += KeteUtils.distance(
//                            userModel.getPoint(j).mX,
//                            userModel.getPoint(j).mY,
//                            baseModel.getPointList()[j].mX,
//                            baseModel.getPointList()[j].mY
//                    )
//                }
//                result.addResult(predictWord, avgDistance / PolylineModel.N_POINTS)
//            }
//        }
//
//        // Calculate again
//        val listRes = result.getResult()
//        var recognizerError = false
//        val recognizer = PolyRecognizerGSS()
//        try {
//            listRes.forEach { res ->
//                for (i in 0 until model.size) {
//                    if (model[i].second == res.second) {
//                        // Lấy Polyline gốc của model
//                        val basePolyline = memModel[i].first.clone()
//                        val gesture = Gesture()
//                        basePolyline.points.forEach { p ->
//                            gesture.addPoint(TPoint(p.x, p.y, 0))
//                        }
//                        recognizer.addTemplate(memModel[i].second, gesture)
//                        break
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            recognizerError = true
//        }
//
//
//        // recognizer
//        val userPolyline = path.clone()
//        val userGesture = Gesture()
//        userPolyline.points.forEach { p ->
//            userGesture.addPoint(TPoint(p.x, p.y, 0))
//        }
//        if (!recognizerError) {
//            var newDistance = 0.0
//            var strRes = ""
//            val recognizeResult = recognizer.recognize(userGesture)
//            listRes.forEach {
//                if (recognizeResult.name == it.second) {
//                    newDistance = 0.9 * it.first + 0.1 * (1 - recognizeResult.score)
//                    strRes = it.second
//                }
//            }
//            result.remove(strRes)
//            result.addResult(strRes, newDistance.toFloat())
//        }
//        callback.onDone(obj, result)
//    }
//}
