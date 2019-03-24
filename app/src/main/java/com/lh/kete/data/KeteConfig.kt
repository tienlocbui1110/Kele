package com.lh.kete.data

import com.google.gson.annotations.SerializedName

/**
 * Created by Tien Loc Bui on 18/03/2019.
 */

class KeteConfig : AbstractConfig {
    @SerializedName("version")
    val version: Int = 0

    @SerializedName("id")
    val id: String = ""

    @SerializedName("button")
    val buttonConfig: Array<ButtonConfig> = arrayOf()

    @SerializedName("commonButtonUI")
    val ui = UserInterfaceConfig()

    @SerializedName("otherConfig")
    val otherConfig = LayoutConfig()
}