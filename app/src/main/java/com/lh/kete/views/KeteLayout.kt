package com.lh.kete.views

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.support.annotation.IntDef
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import com.lh.kete.R
import com.lh.kete.data.ButtonConfig
import com.lh.kete.data.KeteConfig
import com.lh.kete.data.LayoutConfig
import com.lh.kete.data.UserInterfaceConfig
import com.lh.kete.utils.KeteUtils


/**
 * Created by Tien Loc Bui on 18/03/2019.
 */

class KeteLayout : FrameLayout, KeteV<KeteConfig?> {

    private var _layoutData: KeteConfig? = null
    @State
    private var state: Int = INVALID

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, keteConfig: KeteConfig?) : this(context, null as AttributeSet?) {
        this._layoutData = keteConfig
        changeState(VALID)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        getConfig()?.let {
            val widthMode = MeasureSpec.getMode(widthMeasureSpec)
            val widthSize = MeasureSpec.getSize(widthMeasureSpec)
            val heightMode = MeasureSpec.getMode(heightMeasureSpec)
            val heightSize = MeasureSpec.getSize(heightMeasureSpec)
            var width = 0
            var height = 0

            if (widthSize < it.otherConfig.minWidth || heightSize < it.otherConfig.minHeight) {
                changeState(INVALID)
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
                return
            }
            changeState(VALID)

            val maxWidth =
                if (it.otherConfig.maxWidth >= 0) KeteUtils.dpToPx(it.otherConfig.maxWidth, context)
                else widthSize
            when (widthMode) {
                MeasureSpec.EXACTLY -> width = widthSize
                MeasureSpec.AT_MOST -> width = Math.min(widthSize, maxWidth)
                MeasureSpec.UNSPECIFIED -> width = maxWidth
            }

            val maxHeight =
                if (it.otherConfig.maxHeight >= 0) KeteUtils.dpToPx(
                    it.otherConfig.maxHeight,
                    context
                )
                else heightSize
            when (heightMode) {
                MeasureSpec.EXACTLY -> height = heightSize
                MeasureSpec.AT_MOST -> height = Math.min(heightSize, maxHeight)
                MeasureSpec.UNSPECIFIED -> height = maxHeight
            }

            // Send calculate to child
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child is KeteButton)
                    measureChild(child, width, height)
            }
            setMeasuredDimension(width, height)
            return
        }

        changeState(NO_CONFIG)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (checkState(VALID)) {
            for (i in 0 until childCount) {
                val child = getChildAt(i) as? KeteButton ?: continue
                layoutChild(child, left, top, right, bottom)
            }
        } else {
            super.onLayout(changed, left, top, right, bottom)
        }
    }

    override fun getConfig(): KeteConfig? {
        return _layoutData
    }

    fun setLayoutData(config: KeteConfig?) {
        this._layoutData = config
        resetLayout()
        changeState(VALID)
    }

    fun resetLayout() {
        removeAllViews()
    }

    private fun requestViewFromCurrentConfig() {
        getConfig()?.let {
            setStyleFromCurrentConfig()
            val buttonCount = it.buttonConfig.size
            for (i in 0 until buttonCount) {
                addKeteButton(it.buttonConfig[i], it.ui)
            }
        }
    }

    private fun addKeteButton(buttonConfig: ButtonConfig, rootUI: UserInterfaceConfig?) {
        val child = KeteButton(context, buttonConfig, rootUI)
        addView(child)
    }

    private fun changeState(@State state: Int) {
        if (checkState(state))
            return
        Handler(Looper.getMainLooper()).post {
            removeAllViews()
            setState(state)
            when (state) {
                VALID -> requestViewFromCurrentConfig()
                INVALID -> showErrorLayout()
                NO_CONFIG -> showNoConfig()
            }
        }

    }

    private fun showErrorLayout() {
        setDefaultStyle()
        val tv = getDefaultTextView()
        tv.text = resources.getString(R.string.kete_error)
        addView(tv, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    private fun showNoConfig() {
        setDefaultStyle()
        val tv = getDefaultTextView()
        tv.text = resources.getString(R.string.kete_no_config)
        addView(tv, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    private fun getDefaultTextView(): TextView {
        val tv = TextView(context)
        tv.gravity = Gravity.CENTER
        return tv
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
        val childLeft = KeteUtils.percentToPx(mWidth, child.getConfig()?.x)
        val childTop = KeteUtils.percentToPx(mHeight, child.getConfig()?.y)
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

    private fun setDefaultStyle() {
        setBackgroundColor(Color.parseColor(LayoutConfig.backgroundColorDefault()))
    }

    private fun checkState(@State state: Int): Boolean {
        return this.state == state
    }

    private fun setState(@State state: Int) {
        this.state = state
    }

    companion object {
        const val INVALID = 0
        const val VALID = 1
        const val NO_CONFIG = 2

        @IntDef(INVALID, VALID, NO_CONFIG)
        @Retention(AnnotationRetention.SOURCE)
        annotation class State
    }

}
