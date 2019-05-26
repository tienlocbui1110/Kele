package com.lh.kete.db

object KeteContract {
    const val TABLE_LAYOUT = "Layout"
    const val TABLE_ORIGINAL_DICTIONARY = "OriginalDictionary"
    const val TABLE_ALTERNATIVE_DICTIONARY = "AlternativeDictionary"
    const val TABLE_INPUT_METHOD = "InputMethod"
    const val TABLE_POINT_MODEL = "PointModel"

    object Layout {
        const val COL_ID = "id"
        const val COL_HASH = "hash"
    }

    object OriginalDictionary {
        const val COL_WORD = "word"
    }

    object AlternativeDictionary {
        const val COL_ALT_WORD = "alternative_word"
        const val COL_INPUT_METHOD = "input_method"
        const val COL_ORG_WORD = "original_word"
    }

    object InputMethod {
        const val COL_ID = "id"
        const val COL_TYPE = "type"
    }

    object PointModel {
        const val COL_ID = "id"
        const val COL_MODEL = "model"
        const val COL_N_POINTS = "n_points"
        const val COL_LAYOUT = "layout"
        const val COL_FIRSTX = "first_x"
        const val COL_FIRSTY = "first_y"
        const val COL_WORD = "word"
        const val COL_INPUT_METHOD = "input_method"
    }
}