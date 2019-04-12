package com.lh.kete.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Tien Loc Bui on 24/03/2019.
 */

class LayoutConfig : AbstractConfig {
    @SerializedName("minWidth")
    @Expose
    val minWidth: Int = -1

    @SerializedName("minHeight")
    @Expose
    val minHeight: Int = -1

    @SerializedName("maxWidth")
    @Expose
    val maxWidth: Int = -1

    @SerializedName("maxHeight")
    @Expose
    val maxHeight: Int = -1

    @SerializedName("backgroundColor")
    @Expose
    val backgroundColor: String = backgroundColorDefault()

    companion object {
        private const val BACKGROUND_COLOR_DEFAULT = "#FFFFFF"

        fun backgroundColorDefault(): String {
            return BACKGROUND_COLOR_DEFAULT
        }
    }
}