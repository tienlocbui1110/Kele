package com.lh.kete.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Tien Loc Bui on 18/03/2019.
 */

class ButtonConfig : AbstractConfig {

    @SerializedName("char")
    @Expose
    val char: String? = null

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


    @SerializedName("special_key")
    @Expose
    val specialKey: String? = null
}