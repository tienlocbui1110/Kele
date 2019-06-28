package com.lh.kete.algorithm.predictor

import android.app.Activity
import android.util.Log
import com.lh.kete.MainApplication
import com.lh.kete.activity.main.MainActivity
import com.lh.kete.algorithm.Algorithm
import com.lh.kete.algorithm.common.Path
import com.lh.kete.algorithm.common.Point
import com.lh.kete.algorithm.common.PolylineModel
import com.lh.kete.data.Information
import com.lh.kete.data.KeteConfig
import com.lh.kete.db.KeteContract
import com.lh.kete.db.SQLiteHelper
import com.lh.kete.listener.OnWorkerThreadListener

abstract class Predictor : Algorithm<Path, PredictorResult> {

    private val buttonMapper = HashMap<String, Point>()
    private val memModel = ArrayList<Pair<PolylineModel, String>>()

    constructor(activity: MainActivity, kete: KeteConfig, listener: OnWorkerThreadListener) {
        //kete to point"
        for (i in 0 until kete.buttonConfig.size) {
            val button = kete.buttonConfig[i]
            if (button.char == null)
                continue
            val fX = button.x + button.width / 2
            val fY = button.y + button.height / 2
            buttonMapper[button.computingChar ?: button.char] = Point(fX, fY)
        }


        Log.d("Predictor", System.currentTimeMillis().toString())
        buildBaseModel(activity, listener)
        Log.d("Predictor", System.currentTimeMillis().toString())
    }

    fun getModel(): List<Pair<PolylineModel, String>> {
        return memModel
    }

    private fun buildBaseModel(activity: Activity, listener: OnWorkerThreadListener) {
        val dbHelper = SQLiteHelper(activity)
        val db = dbHelper.writableDatabase

        // Alternative Dictionary Cursor
        val altDicCursor = db.rawQuery("""
                SELECT ${KeteContract.AltDictionary.COL_ALT_WORD}, ${KeteContract.AltDictionary.COL_ORG_WORD}
                FROM ${KeteContract.TABLE_ALTERNATIVE_DICTIONARY}
            """.trimIndent(), null)
        // Với mỗi từ trong bảng AltDictionary, xây dựng Mem model từ đây.
        val alternativeIndex = altDicCursor.getColumnIndex(KeteContract.AltDictionary.COL_ALT_WORD)
        val originIndex = altDicCursor.getColumnIndex(KeteContract.AltDictionary.COL_ORG_WORD)
        val infoText = "Building swipe model"
        try {
            db.beginTransaction()
            while (!altDicCursor.isLast) {
                altDicCursor.moveToNext()
                val alternativeWord = altDicCursor.getString(alternativeIndex)
                val originWord = altDicCursor.getString(originIndex)
                val model = buildPolylineModelFromString(alternativeWord)
                if (model.isValid()) {
                    memModel.add(Pair(model, originWord))
                }

                if (altDicCursor.position % 100 == 0) {
                    listener.onUpdate(altDicCursor.position * 100 / altDicCursor.count, infoText)
                }
            }
            altDicCursor.close()
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }

        dbHelper.close()
    }


    // ------------------- USE SQLite, poor performance --------------- //
//    private fun buildBaseModel(activity: Activity, listener: OnWorkerThreadListener) {
//        val dbHelper = SQLiteHelper(activity)
//        val db = dbHelper.writableDatabase
//        // Check if current config have Point Model
//        val pointCursor = db.rawQuery("""
//            SELECT * FROM ${KeteContract.TABLE_POINT_MODEL}
//            WHERE ${KeteContract.PointModel.COL_LAYOUT} = "${Information.LAYOUT_ID}"
//        """.trimIndent(), null)
//        // Database doesn't have model
//        if (pointCursor.count == 0) {
//            // Alternative Dictionary Cursor
//            val altDicCursor = db.rawQuery("""
//                SELECT ${KeteContract.AltDictionary.COL_ALT_WORD}, ${KeteContract.AltDictionary.COL_INPUT_METHOD}
//                FROM ${KeteContract.TABLE_ALTERNATIVE_DICTIONARY}
//            """.trimIndent(), null)
//            // Với mỗi từ trong bảng AltDictionary, xây dựng PointModel từ đây.
//            val alternativeIndex = altDicCursor.getColumnIndex(KeteContract.AltDictionary.COL_ALT_WORD)
//            val inputMethodIndex = altDicCursor.getColumnIndex(KeteContract.AltDictionary.COL_INPUT_METHOD)
//            val infoText = "Building swipe model"
//            try {
//                db.beginTransaction()
//                while (!altDicCursor.isLast) {
//                    altDicCursor.moveToNext()
//                    val alternativeWord = altDicCursor.getString(alternativeIndex)
//                    val inputMethod = altDicCursor.getInt(inputMethodIndex)
//                    val model = buildPolylineModelFromString(alternativeWord)
//                    if (model.isValid())
//                        dbHelper.insertToModel(model, PolylineModel.N_POINTS, Information.LAYOUT_ID!!, model.getPointList()[0].mX,
//                                model.getPointList()[0].mY, alternativeWord, inputMethod)
//                    if (altDicCursor.position % 100 == 0) {
//                        listener.onUpdate(altDicCursor.position * 100 / altDicCursor.count, infoText)
//                    }
//                }
//                altDicCursor.close()
//                db.setTransactionSuccessful()
//            } finally {
//                db.endTransaction()
//            }
//        }
//        pointCursor.close()
//        dbHelper.close()
//    }
//
//    private fun buildMemModel() {
//
//        val db = SQLiteHelper(MainApplication.getAppContext()).readableDatabase
//        db.setMaxSqlCacheSize(100)
//        db.setMaximumSize(200000000000)
//
//        // Get nearly model
//        val cursor = db.rawQuery("""
//            SELECT ${KeteContract.PointModel.COL_ID}, ${KeteContract.PointModel.COL_N}, ${KeteContract.AltDictionary.COL_ORG_WORD}
//            FROM ${KeteContract.TABLE_POINT_MODEL} model JOIN ${KeteContract.TABLE_ALTERNATIVE_DICTIONARY} dic
//            ON model.${KeteContract.PointModel.COL_WORD} = dic.${KeteContract.AltDictionary.COL_ALT_WORD}
//                AND model.${KeteContract.PointModel.COL_INPUT_METHOD} = dic.${KeteContract.AltDictionary.COL_INPUT_METHOD}
//            """.trimIndent(), arrayOf())
//        val modelIdIdx = cursor.getColumnIndex(KeteContract.PointModel.COL_ID)
//        val nIdx = cursor.getColumnIndex(KeteContract.PointModel.COL_N)
//        val originIdx = cursor.getColumnIndex(KeteContract.AltDictionary.COL_ORG_WORD)
//        if (cursor.count == 0) {
//            cursor.close()
//            return
//        }
//        // Run at every suitable model
//        while (!cursor.isLast) {
//            cursor.moveToNext()
//            val pointId = cursor.getInt(modelIdIdx)
//            // Query point at polyline
//            val cursorDetails = db.rawQuery("""
//            SELECT ${KeteContract.PointModelDetails.COL_INDEX}, ${KeteContract.PointModelDetails.COL_X},
//                    ${KeteContract.PointModelDetails.COL_Y}
//            FROM ${KeteContract.TABLE_POINT_MODEL_DETAILS}
//            WHERE ${KeteContract.PointModelDetails.COL_MODEL}=?
//        """.trimIndent(), arrayOf(pointId.toString()))
//            if (cursorDetails.count == 0) {
//                cursorDetails.close()
//                continue
//            }
//
//            // Construct ArrayList
//            val points = arrayListOf<Point>()
//            for (i in 0 until cursor.getInt(nIdx))
//                points.add(Point(0f, 0f))
//
//
//            val iIdx = cursorDetails.getColumnIndex(KeteContract.PointModelDetails.COL_INDEX)
//            val xIdx = cursorDetails.getColumnIndex(KeteContract.PointModelDetails.COL_X)
//            val yIdx = cursorDetails.getColumnIndex(KeteContract.PointModelDetails.COL_Y)
//            while (!cursorDetails.isLast) {
//                cursorDetails.moveToNext()
//                val x = cursorDetails.getFloat(xIdx)
//                val y = cursorDetails.getFloat(yIdx)
//                val idx = cursorDetails.getInt(iIdx)
//                points[idx] = Point(x, y)
//            }
//
//            // Predict
//            //val memModel = PolylineModel.Builder(points).build()
//
//            cursorDetails.close()
//        }
//        cursor.close()
//        db.close()
//    }

    private fun buildPolylineModelFromString(charSeq: String): PolylineModel {
        val pathBuilder = Path.Builder()
        for (i in 0 until charSeq.length) {
            val character = charSeq[i].toString()
            buttonMapper[character]?.let {
                pathBuilder.appendPoint(it)
            }
        }
        return pathBuilder.build().toPolylineModel()
    }
}