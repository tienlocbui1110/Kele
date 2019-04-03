package com.lh.kete.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Tien Loc Bui on 18/03/2019.
 */

class KeteConfig : AbstractConfig {
    @SerializedName("version")
    @Expose
    val version: Int? = null

    @SerializedName("id")
    @Expose
    val id: String? = null

    @SerializedName("button")
    @Expose
    val buttonConfig: Array<ButtonConfig> = arrayOf()

    @SerializedName("commonButtonUI")
    @Expose
    val ui: UserInterfaceConfig? = null

    @SerializedName("otherConfig")
    @Expose
    val otherConfig: LayoutConfig = LayoutConfig()
}