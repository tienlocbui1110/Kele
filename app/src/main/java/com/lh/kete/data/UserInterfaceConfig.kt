package com.lh.kete.data

import com.google.gson.annotations.SerializedName

/**
 * Created by Tien Loc Bui on 24/03/2019.
 */

class UserInterfaceConfig : AbstractConfig {

    @SerializedName("backgroundColor")
    val backgroundColor: String = "#FFFFFF"

    @SerializedName("textColor")
    val textColor: String = "#000000"

    @SerializedName("fontSize")
    // text always use "sp" for density
    val fontSize: Int = 14
}
