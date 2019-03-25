package com.lh.kete.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Tien Loc Bui on 24/03/2019.
 */

class LayoutConfig : AbstractConfig {
    @SerializedName("minWidth")
    @Expose
    val minWidth: Int = 500

    @SerializedName("minHeight")
    @Expose
    val minHeight: Int = 500

    @SerializedName("maxWidth")
    @Expose
    val maxWidth: Int = 500

    @SerializedName("maxHeight")
    @Expose
    val maxHeight: Int = 500

    @SerializedName("backgroundColor")
    @Expose
    val backgroundColor: String = "#FFFFFF"
}