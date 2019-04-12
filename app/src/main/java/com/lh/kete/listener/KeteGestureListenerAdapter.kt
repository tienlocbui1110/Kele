package com.lh.kete.listener

import android.view.MotionEvent
import com.lh.kete.views.KeteButton

open class KeteGestureListenerAdapter : KeteGestureListener {
    private var mExternal: KeteGestureListener? = null
    override fun onClick(event: MotionEvent?, button: KeteButton) {
        mExternal?.onClick(event, button)
    }

    override fun onLongClick(event: MotionEvent?, button: KeteButton) {
        mExternal?.onLongClick(event, button)
    }

    override fun onSwipe(
        startPos: MotionEvent?,
        endPos: MotionEvent?,
        startButton: KeteButton?,
        endButton: KeteButton?
    ) {
        mExternal?.onSwipe(startPos, endPos, startButton, endButton)
    }

    override fun onKeyUp(event: MotionEvent?) {
        mExternal?.onKeyUp(event)
    }

    override fun onPressDown(event: MotionEvent?, button: KeteButton) {
        mExternal?.onPressDown(event, button)
    }

    fun setExternal(external: KeteGestureListener) {
        this.mExternal = external
    }
}