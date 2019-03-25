package com.lh.kete.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.lh.kete.defs.KeteDefs

/**
 * Created by Tien Loc Bui on 24/03/2019.
 */

class UserInterfaceConfig : AbstractConfig {

    @SerializedName("backgroundColor")
    @Expose
    val backgroundColor: String? = null

    @SerializedName("textColor")
    @Expose
    val textColor: String? = null

    @SerializedName("fontSize")
    @Expose
    // text always use "sp" for density
    val fontSize: Int? = null

    companion object {
        private const val BACKGROUND_COLOR_DEFAULT = "#FFFFFF"
        private const val TEXT_COLOR_DEFAULT = "#000000"
        private const val FONT_SIZE_DEFAULT = 30

        fun backgroundColorDefault(): String {
            return BACKGROUND_COLOR_DEFAULT
        }

        fun textColorDefault(): String {
            return TEXT_COLOR_DEFAULT
        }

        fun fontSizeDefault(): Int {
            return FONT_SIZE_DEFAULT
        }
    }
}
