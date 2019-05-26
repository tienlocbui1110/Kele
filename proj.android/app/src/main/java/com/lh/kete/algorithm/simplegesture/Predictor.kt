package com.lh.kete.algorithm.simplegesture

import android.app.Activity
import android.util.Log
import com.lh.kete.MainApplication
import com.lh.kete.activity.main.MainActivity
import com.lh.kete.algorithm.Algorithm
import com.lh.kete.algorithm.common.Path
import com.lh.kete.algorithm.common.Point
import com.lh.kete.algorithm.common.PolylineModel
import com.lh.kete.data.KeteConfig
import com.lh.kete.db.KeteContract
import com.lh.kete.db.SQLiteHelper
import com.lh.kete.listener.OnWorkerThreadListener
import com.lh.kete.utils.KeteUtils
import java.lang.StringBuilder

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class Predictor : Algorithm<Path, PredictorResult> {
    private val buttonMapper = HashMap<String, Point>()
    private var nOperation = 0
    private val FLOAT_ERROR = 0.03f

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
        buildBaseModel(activity, kete, listener)
        listener.onCompleted()
    }

    override fun doCalculate(path: Path, callback: Algorithm.Callback<PredictorResult>) {
        val userModel = path.toPolylineModel()
        val result = PredictorResult()
        val db = SQLiteHelper(MainApplication.getAppContext()).readableDatabase
        val xRange = 10f
        val yRange = 20f
        val minX = userModel.getPointList()[0].x - xRange
        val maxX = userModel.getPointList()[0].x + xRange
        val minY = userModel.getPointList()[0].y - yRange
        val maxY = userModel.getPointList()[0].y + yRange

        // Get nearly model
        val cursor = db.rawQuery("""
            SELECT ${KeteContract.PointModel.COL_MODEL}, ${KeteContract.AlternativeDictionary.COL_ORG_WORD}
            FROM ${KeteContract.TABLE_POINT_MODEL} model JOIN ${KeteContract.TABLE_ALTERNATIVE_DICTIONARY} dic
            ON ${KeteContract.PointModel.COL_FIRSTX} BETWEEN ? AND ?
                AND ${KeteContract.PointModel.COL_FIRSTY} BETWEEN ? AND ?
                AND model.${KeteContract.PointModel.COL_WORD} = dic.${KeteContract.AlternativeDictionary.COL_ALT_WORD}
                AND model.${KeteContract.PointModel.COL_INPUT_METHOD} = dic.${KeteContract.AlternativeDictionary.COL_INPUT_METHOD}
        """.trimIndent(), arrayOf(minX.toString(), maxX.toString(), minY.toString(), maxY.toString()))
        Log.d(Predictor::class.java.simpleName, "Number of found model: ${cursor.count}")
        val modelIdx = cursor.getColumnIndex(KeteContract.PointModel.COL_MODEL)
        val originIdx = cursor.getColumnIndex(KeteContract.AlternativeDictionary.COL_ORG_WORD)
        if (cursor.count == 0)
            return
        while (!cursor.isLast) {
            cursor.moveToNext()
            val baseModel = PolylineModel.fromString(cursor.getString(modelIdx))
            val predictWord = cursor.getString(originIdx)
            var avgDistance = 0f
            for (i in 0 until PolylineModel.N_POINTS) {
                avgDistance += KeteUtils.distance(
                        userModel.getPointList()[i].x,
                        userModel.getPointList()[i].y,
                        baseModel.getPointList()[i].x,
                        baseModel.getPointList()[i].y
                )
            }
            result.addResult(predictWord, avgDistance / PolylineModel.N_POINTS)
        }
        cursor.close()
        db.close()
        callback.onDone(result)
    }

    private val sampleBuilder = StringBuilder()

    private fun buildBaseModel(activity: Activity, kete: KeteConfig, listener: OnWorkerThreadListener) {
        val db = SQLiteHelper(activity)
        // Check if current config have Point Model
        val pointCursor = db.readableDatabase.rawQuery("""
            SELECT * FROM ${KeteContract.TABLE_POINT_MODEL}
            WHERE ${KeteContract.PointModel.COL_N_POINTS} = ${PolylineModel.N_POINTS} AND
            ${KeteContract.PointModel.COL_LAYOUT} = "${kete.id}"
        """.trimIndent(), null)
        // Database doesn't have model
        if (pointCursor.count == 0) {
            // Alternative Dictionary Cursor
            val altDicCursor = db.readableDatabase.rawQuery("""
                SELECT ${KeteContract.AlternativeDictionary.COL_ALT_WORD}, ${KeteContract.AlternativeDictionary.COL_INPUT_METHOD}
                FROM ${KeteContract.TABLE_ALTERNATIVE_DICTIONARY}
            """.trimIndent(), null)
            // Với mỗi từ trong bảng AlternativeDictionary, xây dựng PointModel từ đây.
            val alternativeIndex = altDicCursor.getColumnIndex(KeteContract.AlternativeDictionary.COL_ALT_WORD)
            val inputMethodIndex = altDicCursor.getColumnIndex(KeteContract.AlternativeDictionary.COL_INPUT_METHOD)
            val infoText = "Building swipe model"
            try {
                db.writableDatabase.beginTransaction()
                while (!altDicCursor.isLast) {
                    altDicCursor.moveToNext()
                    val alternativeWord = altDicCursor.getString(alternativeIndex)
                    val inputMethod = altDicCursor.getInt(inputMethodIndex)
                    val model = buildPolylineModelFromString(alternativeWord)
                    if (model.getPointList().size > 1)
                        db.insertToModel(model, PolylineModel.N_POINTS, kete.id!!, model.getPointList()[0].x,
                                         model.getPointList()[0].y, alternativeWord, inputMethod)
                    if (altDicCursor.position % 100 == 0) {
                        listener.onUpdate(altDicCursor.position * 100 / altDicCursor.count, infoText)
                    }
                }
                altDicCursor.close()
                db.writableDatabase.setTransactionSuccessful()
            } finally {
                db.writableDatabase.endTransaction()
            }
        }
        pointCursor.close()
        db.close()
    }

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