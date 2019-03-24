package com.lh.kete.views

import android.content.Context
import android.widget.FrameLayout
import com.lh.kete.data.ButtonConfig

/**
 * Created by Tien Loc Bui on 18/03/2019.
 */

class KeteButton : FrameLayout, KeteV<ButtonConfig?> {

    private var dataConfig: ButtonConfig? = null

    constructor(context: Context) : super(context)

    override fun getConfig(): ButtonConfig? {
        return dataConfig
    }
}