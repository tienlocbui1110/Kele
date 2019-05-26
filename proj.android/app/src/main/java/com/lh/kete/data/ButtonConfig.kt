package com.lh.kete.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Tien Loc Bui on 18/03/2019.
 */

class ButtonConfig : AbstractConfig {

    // Character display on UI
    @SerializedName("char")
    @Expose
    val char: String? = null

    // Character will use at computing in dictionary
    @SerializedName("computing_char")
    @Expose
    val computingChar: String? = null

    @SerializedName("width")
    @Expose
    val width = 0f

    @SerializedName("height")
    @Expose
    val height = 0f

    @SerializedName("x")
    @Expose
    val x = 0f

    @SerializedName("y")
    @Expose
    val y = 0f

    @SerializedName("ui")
    @Expose
    val ui: UserInterfaceConfig? = null
}