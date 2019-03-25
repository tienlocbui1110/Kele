package com.lh.kete.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lh.kete.data.ButtonConfig
import com.lh.kete.data.KeteConfig
import com.lh.kete.data.UserInterfaceConfig
import com.lh.kete.utils.KeteUtils

/**
 * Created by Tien Loc Bui on 18/03/2019.
 */

class KeteLayout : FrameLayout, KeteV<KeteConfig?> {

    private var _layoutData: KeteConfig? = null

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, keteConfig: KeteConfig?) : this(context, null as AttributeSet?) {
        this._layoutData = keteConfig
        requestViewFromCurrentConfig()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var needCallSuper = true

        getConfig()?.let {
            val widthMode = MeasureSpec.getMode(widthMeasureSpec)
            val widthSize = MeasureSpec.getSize(widthMeasureSpec)
            val heightMode = MeasureSpec.getMode(heightMeasureSpec)
            val heightSize = MeasureSpec.getSize(heightMeasureSpec)
            var width = 0
            var height = 0

            if (widthSize < it.otherConfig.minWidth || heightSize < it.otherConfig.minHeight)
                return

            when (widthMode) {
                MeasureSpec.EXACTLY -> width = widthSize
                MeasureSpec.AT_MOST -> width = Math.min(widthSize, it.otherConfig.maxWidth)
                MeasureSpec.UNSPECIFIED -> width = it.otherConfig.maxWidth
            }

            when (heightMode) {
                MeasureSpec.EXACTLY -> height = heightSize
                MeasureSpec.AT_MOST -> height = Math.min(heightSize, it.otherConfig.maxHeight)
                MeasureSpec.UNSPECIFIED -> height = it.otherConfig.maxWidth
            }

            // Send calculate to child
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child is KeteButton)
                    measureChild(child, width, height)
            }
            setMeasuredDimension(width, height)
            needCallSuper = false
        }

        // If we cant measure child exactly, so layout is invalid, call parent instead.
        if (needCallSuper)
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i) as? KeteButton ?: continue
            layoutChild(child, left, top, right, bottom)
        }
    }

    override fun getConfig(): KeteConfig? {
        return _layoutData
    }

    fun setLayoutData(config: KeteConfig) {
        this._layoutData = config
        resetLayout()
        requestViewFromCurrentConfig()
        requestLayout()
    }

    fun resetLayout() {
        removeAllViews()
    }

    private fun requestViewFromCurrentConfig() {
        getConfig()?.let {
            // Setup root view
            setStyleFromCurrentConfig()

            val buttonCount = it.buttonConfig.size
            for (i in 0 until buttonCount) {
                addKeteButton(it.buttonConfig[i], it.ui)
            }
        }
    }

    private fun addKeteButton(buttonConfig: ButtonConfig, rootUI: UserInterfaceConfig?) {
        addView(KeteButton(context, buttonConfig, rootUI))
    }

    private fun measureChild(child: KeteButton, width: Int, height: Int) {
        val childConfig = child.getConfig()
        val childWidth = KeteUtils.percentToPx(width, childConfig?.width)
        val childHeight = KeteUtils.percentToPx(height, childConfig?.height)
        val childWidthSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY)
        val childHeightSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY)
        child.measure(childWidthSpec, childHeightSpec)
    }

    private fun layoutChild(child: KeteButton, left: Int, top: Int, right: Int, bottom: Int) {
        val mWidth = right - left
        val mHeight = bottom - top
        val childLeft = left + KeteUtils.percentToPx(mWidth, child.getConfig()?.x)
        val childTop = top + KeteUtils.percentToPx(mHeight, child.getConfig()?.y)
        val childRight = childLeft + child.measuredWidth
        val childBottom = childTop + child.measuredHeight

        child.layout(childLeft, childTop, childRight, childBottom)
    }

    private fun setStyleFromCurrentConfig() {
        val config = getConfig()
        config?.let {
            setBackgroundColor(Color.parseColor(it.otherConfig.backgroundColor))
        }
    }
}
