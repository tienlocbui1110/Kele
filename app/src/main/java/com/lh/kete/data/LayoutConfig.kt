package com.lh.kete.data

import com.google.gson.annotations.SerializedName

/**
 * Created by Tien Loc Bui on 24/03/2019.
 */

class LayoutConfig : AbstractConfig {
    @SerializedName("minWidth")
    val minWidth: Int = 500

    @SerializedName("minHeight")
    val minHeight: Int = 500

    @SerializedName("maxWidth")
    val maxWidth: Int = 500

    @SerializedName("maxHeight")
    val maxHeight: Int = 500
}