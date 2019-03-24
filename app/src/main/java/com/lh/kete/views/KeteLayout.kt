package com.lh.kete.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lh.kete.data.KeteConfig
import com.lh.kete.utils.KeteUtils

/**
 * Created by Tien Loc Bui on 18/03/2019.
 */

class KeteLayout : FrameLayout, KeteV<KeteConfig?> {

    private var layoutData: KeteConfig? = null

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, keteConfig: KeteConfig) : this(context, null) {
        this.layoutData = keteConfig
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //TODO: Set LayoutParams if needed using layoutData
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var needCallSuper = true

        layoutData?.let {
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

    fun setLayoutData(config: KeteConfig) {
        this.layoutData = config
        requestLayout()
    }

    override fun getConfig(): KeteConfig? {
        return layoutData
    }

    private fun measureChild(child: KeteButton, width: Int, height: Int) {
        val childConfig = child.getConfig()
        val childWidth = KeteUtils.percentToPx(width, childConfig?.width)
        val childHeight = KeteUtils.percentToPx(height, childConfig?.height)
        val childWidthSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY)
        val childHeightSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY)
        child.measure(childWidthSpec, childHeightSpec)
    }
}
