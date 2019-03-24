package com.lh.kete.data

import com.google.gson.annotations.SerializedName

/**
 * Created by Tien Loc Bui on 18/03/2019.
 */

class ButtonConfig : AbstractConfig {

    @SerializedName("char")
    val char: Char = '0'

    @SerializedName("width")
    val width = 0f

    @SerializedName("height")
    val height = 0f

    @SerializedName("x")
    val x = 0f

    @SerializedName("y")
    val y = 0f

    @SerializedName("ui")
    val ui = UserInterfaceConfig()
}