package com.lh.kete.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.text.Layout
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.lh.kete.data.ButtonConfig
import com.lh.kete.data.UserInterfaceConfig


/**
 * Created by Tien Loc Bui on 18/03/2019.
 */

class KeteButton : FrameLayout, KeteV<ButtonConfig?> {

    private var layoutConfig: ButtonConfig? = null
    private var defaultUIConfig: UserInterfaceConfig? = null

    constructor(context: Context) : super(context, null)

    constructor(
        context: Context,
        layoutConfig: ButtonConfig?,
        rootUIConfig: UserInterfaceConfig?
    ) : super(context, null) {
        this.layoutConfig = layoutConfig
        this.defaultUIConfig = rootUIConfig
        requestViewFromCurrentConfig()
    }

    override fun getConfig(): ButtonConfig? {
        return layoutConfig
    }

    internal fun onPressDown() {

    }
    internal fun onKeyUp() {

    }
    private fun requestViewFromCurrentConfig() {
        getConfig()?.let {
            val buttonUI = it.ui

            // --------------------Get button config -----------------------//
            // Get background color
            val backgroundColor: String = buttonUI?.backgroundColor
                ?: defaultUIConfig?.backgroundColor
                ?: UserInterfaceConfig.backgroundColorDefault()
            // Get text color
            val textColor = buttonUI?.textColor
                ?: defaultUIConfig?.textColor
                ?: UserInterfaceConfig.textColorDefault()
            // Get font size
            val fontSize = buttonUI?.fontSize
                ?: defaultUIConfig?.fontSize
                ?: UserInterfaceConfig.fontSizeDefault()
            // ------------------------- End --------------------------------//

            setBackgroundColor(Color.parseColor(backgroundColor))

            val textView = TextView(context)
            textView.text = it.char?: it.computingChar
            textView.setTextColor(Color.parseColor(textColor))
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize.toFloat())

            val lp = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            textView.gravity = Gravity.CENTER
            addView(textView, lp)
        }
    }
}