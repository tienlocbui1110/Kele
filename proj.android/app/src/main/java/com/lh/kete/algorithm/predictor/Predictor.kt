package com.lh.kete.algorithm.predictor

import android.app.Activity
import com.lh.kete.activity.main.MainActivity
import com.lh.kete.algorithm.Algorithm
import com.lh.kete.algorithm.common.Path
import com.lh.kete.algorithm.common.Point
import com.lh.kete.algorithm.common.PolylineModel
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
        buildBaseModel(activity, listener)
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