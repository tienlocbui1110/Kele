package com.lh.kete.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.lh.kete.algorithm.common.PolylineModel
import com.lh.kete.data.Information
import com.lh.kete.data.KeteConfig
import com.lh.kete.listener.OnWorkerThreadListener
import java.lang.RuntimeException

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    companion object {
        private const val DB_NAME = "KeteDB"
        private const val DB_VERSION = 1

        private const val DICTIONARY_PATH = "dictionary.txt"
        private const val ALTERNATIVE_DIC_VNI_LAST = "vni_dic.txt"
        private const val VNI_LAST_INPUT_METHOD = "vni_sign_in_last"

        private const val CREATE_TABLE_LAYOUT = """CREATE TABLE ${KeteContract.TABLE_LAYOUT}(
            ${KeteContract.Layout.COL_ID} TEXT PRIMARY KEY NOT NULL,
            ${KeteContract.Layout.COL_HASH} TEXT NOT NULL
        )"""

        private const val CREATE_TABLE_ORIGINAL_DICTIONARY = """CREATE TABLE ${KeteContract.TABLE_ORIGINAL_DICTIONARY}(
            ${KeteContract.OriginalDictionary.COL_WORD} TEXT PRIMARY KEY NOT NULL
        )"""

        private const val CREATE_TABLE_INPUT_METHOD = """CREATE TABLE ${KeteContract.TABLE_INPUT_METHOD}(
            ${KeteContract.InputMethod.COL_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${KeteContract.InputMethod.COL_TYPE} TEXT NOT NULL UNIQUE
        )"""

        private const val CREATE_TABLE_ALTERNATIVE_DICTIONARY = """CREATE TABLE ${KeteContract.TABLE_ALTERNATIVE_DICTIONARY}(
            ${KeteContract.AlternativeDictionary.COL_ALT_WORD} TEXT NOT NULL,
            ${KeteContract.AlternativeDictionary.COL_INPUT_METHOD} INTEGER,
            ${KeteContract.AlternativeDictionary.COL_ORG_WORD} TEXT,
            PRIMARY KEY (${KeteContract.AlternativeDictionary.COL_ALT_WORD}, ${KeteContract.AlternativeDictionary.COL_INPUT_METHOD}),
            FOREIGN KEY(${KeteContract.AlternativeDictionary.COL_INPUT_METHOD}) REFERENCES
                ${KeteContract.TABLE_INPUT_METHOD}(${KeteContract.InputMethod.COL_ID}),
            FOREIGN KEY(${KeteContract.AlternativeDictionary.COL_ORG_WORD}) REFERENCES
                ${KeteContract.TABLE_ORIGINAL_DICTIONARY}(${KeteContract.OriginalDictionary.COL_WORD})
        )"""

        private const val CREATE_TABLE_POINT_MODEL = """CREATE TABLE ${KeteContract.TABLE_POINT_MODEL}(
            ${KeteContract.PointModel.COL_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${KeteContract.PointModel.COL_MODEL} TEXT NOT NULL,
            ${KeteContract.PointModel.COL_N_POINTS} INTEGER,
            ${KeteContract.PointModel.COL_LAYOUT} TEXT,
            ${KeteContract.PointModel.COL_FIRSTX} REAL,
            ${KeteContract.PointModel.COL_FIRSTY} REAL,
            ${KeteContract.PointModel.COL_WORD} TEXT,
            ${KeteContract.PointModel.COL_INPUT_METHOD} INTEGER,
            FOREIGN KEY(${KeteContract.PointModel.COL_LAYOUT}) REFERENCES
                ${KeteContract.TABLE_LAYOUT}(${KeteContract.Layout.COL_ID}),
            FOREIGN KEY(${KeteContract.PointModel.COL_WORD}, ${KeteContract.PointModel.COL_INPUT_METHOD}) REFERENCES
                ${KeteContract.TABLE_ALTERNATIVE_DICTIONARY}(${KeteContract.AlternativeDictionary.COL_ALT_WORD},
                 ${KeteContract.AlternativeDictionary.COL_INPUT_METHOD})
        )"""

        private const val CREATE_INDEX_POINT_MODEL_FIRST_SEARCHING = """CREATE INDEX PointModel_firstX_firstY
            ON ${KeteContract.TABLE_POINT_MODEL}(${KeteContract.PointModel.COL_FIRSTX}, ${KeteContract.PointModel.COL_FIRSTY})
        """
    }

    private val mContext: Context = context

    override fun onCreate(db: SQLiteDatabase) {
        db.run {
            execSQL(CREATE_TABLE_LAYOUT)
            execSQL(CREATE_TABLE_INPUT_METHOD)
            execSQL(CREATE_TABLE_ORIGINAL_DICTIONARY)
            execSQL(CREATE_TABLE_ALTERNATIVE_DICTIONARY)
            execSQL(CREATE_TABLE_POINT_MODEL)
            execSQL(CREATE_INDEX_POINT_MODEL_FIRST_SEARCHING)
        }
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.run {
            execSQL("DROP TABLE IF EXISTS " + KeteContract.TABLE_LAYOUT)
            execSQL("DROP TABLE IF EXISTS " + KeteContract.TABLE_ORIGINAL_DICTIONARY)
            execSQL("DROP TABLE IF EXISTS " + KeteContract.TABLE_ALTERNATIVE_DICTIONARY)
            execSQL("DROP TABLE IF EXISTS " + KeteContract.TABLE_INPUT_METHOD)
            execSQL("DROP TABLE IF EXISTS " + KeteContract.TABLE_POINT_MODEL)
        }
    }

    fun getRowCount(tableName: String): Int {
        val db = readableDatabase
        var result = 0
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $tableName", null)
        if (cursor.count > 0) {
            cursor.moveToFirst()
            result = cursor.getInt(0)
        }
        cursor.close()
        return result
    }

    fun insertToModel(model: PolylineModel, nPoints: Int, layoutId: String, firstX: Float, firstY: Float,
                      altWord: String, inputMethod: Int) {
        val contentValues = ContentValues()

        contentValues.put(KeteContract.PointModel.COL_MODEL, model.toString())
        contentValues.put(KeteContract.PointModel.COL_N_POINTS, nPoints)
        contentValues.put(KeteContract.PointModel.COL_LAYOUT, layoutId)
        contentValues.put(KeteContract.PointModel.COL_FIRSTX, firstX)
        contentValues.put(KeteContract.PointModel.COL_FIRSTY, firstY)
        contentValues.put(KeteContract.PointModel.COL_WORD, altWord)
        contentValues.put(KeteContract.PointModel.COL_INPUT_METHOD, inputMethod)

        writableDatabase.insert(KeteContract.TABLE_POINT_MODEL, null, contentValues)
    }

    fun verify(keteConfig: KeteConfig, threadListener: OnWorkerThreadListener) {
        var count = 0

        // Step 1: checking dictionary
        count = getRowCount(KeteContract.TABLE_ORIGINAL_DICTIONARY)
        if (count == 0) {
            val infoText = "Rebuild database"
            // Add dictionary to database from asset
            val words = mContext.assets.open(DICTIONARY_PATH).bufferedReader().readLines()
            val iterator = words.iterator()
            var i = 0
            var wordLen = words.size
            // Begin transaction
            writableDatabase.beginTransaction()
            try {
                while (iterator.hasNext()) {
                    val word = iterator.next()
                    if (word.isEmpty())
                        continue
                    i++
                    addOriginalWord(word)
                    if (i % 100 == 0)
                        threadListener.onUpdate(i * 100 / wordLen, infoText)
                }
                writableDatabase.setTransactionSuccessful()
            } finally {
                writableDatabase.endTransaction()
            }
        }

        // Step 2: checking input_method
        count = getRowCount(KeteContract.TABLE_INPUT_METHOD)
        if (count == 0) {
            // Add default method
            addInputMethod(VNI_LAST_INPUT_METHOD)
        }
        // Step 3: checking alternative dictionary
        count = getRowCount(KeteContract.TABLE_ALTERNATIVE_DICTIONARY)
        if (count == 0) {
            val infoText = "Rebuild swipe database"
            // Add dictionary to database from asset
            val words = mContext.assets.open(ALTERNATIVE_DIC_VNI_LAST).bufferedReader().readLines()
            val inputMethod = getInputMethodId(VNI_LAST_INPUT_METHOD) ?: throw RuntimeException(
                    "input method $VNI_LAST_INPUT_METHOD not found in database.")
            val iterator = words.iterator()
            var i = 0
            val wordLen = words.size
            writableDatabase.beginTransaction()
            try {
                while (iterator.hasNext()) {
                    val word = iterator.next()
                    if (word.isEmpty())
                        continue
                    i++
                    val splitWord = word.split(" - ")
                    if (splitWord.size != 2)
                        continue
                    addAlternativeWord(splitWord[1], inputMethod, splitWord[0])
                    if (i % 100 == 0)
                        threadListener.onUpdate(i * 100 / wordLen, infoText)
                }
                writableDatabase.setTransactionSuccessful()
            } finally {
                writableDatabase.endTransaction()
            }
        }

        // Step 4: verify layout_hash
        if (keteConfig.id == null)
            throw RuntimeException("Layout ID must not be null!")
        if (Information.LAYOUT_HASH == null)
            throw RuntimeException("Layout Hash error!")
        val currentHash = getLayoutHash(keteConfig.id)
        if (currentHash == null || currentHash != Information.LAYOUT_HASH) {
            cleanLayout(keteConfig.id)
            addLayout(keteConfig.id, Information.LAYOUT_HASH!!)
        }
    }

    private fun addOriginalWord(word: String) {
        val contentValues = ContentValues()
        contentValues.put(KeteContract.OriginalDictionary.COL_WORD, word)
        writableDatabase.insert(KeteContract.TABLE_ORIGINAL_DICTIONARY, null, contentValues)
    }

    private fun addAlternativeWord(alternativeWord: String, inputMethod: Int, originalWord: String) {
        val contentValues = ContentValues()

        contentValues.put(KeteContract.AlternativeDictionary.COL_ALT_WORD, alternativeWord)
        contentValues.put(KeteContract.AlternativeDictionary.COL_INPUT_METHOD, inputMethod)
        contentValues.put(KeteContract.AlternativeDictionary.COL_ORG_WORD, originalWord)

        writableDatabase.insert(KeteContract.TABLE_ALTERNATIVE_DICTIONARY, null, contentValues)
    }

    private fun addInputMethod(inputMethod: String) {
        val contentValues = ContentValues()
        contentValues.put(KeteContract.InputMethod.COL_TYPE, inputMethod)
        writableDatabase.insert(KeteContract.TABLE_INPUT_METHOD, null, contentValues)
    }

    private fun addLayout(layoutId: String, layoutHash: String) {
        val contentValues = ContentValues()

        contentValues.put(KeteContract.Layout.COL_ID, layoutId)
        contentValues.put(KeteContract.Layout.COL_HASH, layoutHash)

        writableDatabase.insert(KeteContract.TABLE_LAYOUT, null, contentValues)
    }

    private fun getLayoutHash(layoutId: String): String? {
        var result: String? = null
        val cursor = readableDatabase.query(KeteContract.TABLE_LAYOUT, arrayOf(KeteContract.Layout.COL_HASH),
                                            "${KeteContract.Layout.COL_ID}=?", arrayOf(layoutId), null, null, null)
        if (cursor.count != 0) {
            cursor.moveToFirst()
            val colHashIndex = cursor.getColumnIndex(KeteContract.Layout.COL_HASH)
            result = cursor.getString(colHashIndex)
        }
        cursor.close()
        return result
    }

    private fun getInputMethodId(type: String): Int? {
        val cursor = readableDatabase.query(KeteContract.TABLE_INPUT_METHOD, arrayOf(KeteContract.InputMethod.COL_ID),
                                            "${KeteContract.InputMethod.COL_TYPE}=?", arrayOf(type), null, null, null)
        val result = if (cursor.count == 0) null else {
            cursor.moveToFirst()
            cursor.getInt(
                    cursor.getColumnIndex(KeteContract.InputMethod.COL_ID))
        }
        cursor.close()
        return result
    }

    private fun cleanLayout(layoutId: String) {
        // Delete PointModel
        writableDatabase
                .delete(KeteContract.TABLE_POINT_MODEL, "${KeteContract.PointModel.COL_LAYOUT}=?", arrayOf(layoutId))
        // Delete Layout
        writableDatabase.delete(KeteContract.TABLE_LAYOUT, "${KeteContract.Layout.COL_ID}=?", arrayOf(layoutId))
    }
}