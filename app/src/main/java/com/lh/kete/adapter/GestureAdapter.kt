package com.lh.kete.adapter

import android.support.annotation.IntDef
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_UP
import com.lh.kete.listener.KeteGestureListener
import com.lh.kete.views.KeteButton

class GestureAdapter : GestureDetector.SimpleOnGestureListener {
    private val mListener: KeteGestureListener
    private val mFindView: FindView<KeteButton>
    private var mPressState = NONE

    // Swipe cache
    private var previousEvent: MotionEvent? = null
    private var previousButton: KeteButton? = null

    constructor(listener: KeteGestureListener, findView: FindView<KeteButton>) : super() {
        mListener = listener
        mFindView = findView
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        if (mPressState == LONG_PRESS)
            return false

        mPressState = SWIPE
        val currentButton = if (e2 != null) getButton(e2) else null
        mListener.onSwipe(previousEvent, e2, previousButton, currentButton)
        previousEvent = e2
        previousButton = currentButton
        return true
    }

    override fun onLongPress(e: MotionEvent?) {
        if (mPressState != PRESS)
            return
        mPressState = LONG_PRESS
        val button = if (e != null) getButton(e) else null
        button?.let {
            mListener.onLongClick(e, it)
        }
    }

    override fun onDown(e: MotionEvent?): Boolean {
        if (mPressState != PRESS) {
            mPressState = PRESS
            e?.let {
                getButton(e)?.let {
                    mListener.onPressDown(e, it)
                }
            }
        }
        return true
    }

    fun onHandleEvent(ev: MotionEvent?) {
        if (ev?.action == ACTION_UP && mPressState == PRESS) {
            val button = getButton(ev)
            button?.let {
                mListener.onClick(ev, it)
            }
            // Reset Adapter
            resetAdapter()
        }

        // Handle Up and Down
        if (ev?.action == ACTION_UP)
            mListener.onKeyUp(ev)
    }

    private fun getButton(ev: MotionEvent): KeteButton? {
        return mFindView.getViewFromPosition(ev.x, ev.y)
    }

    private fun resetAdapter() {
        mPressState = NONE
        previousButton = null
        previousEvent = null

    }

    companion object {
        const val NONE = 0
        const val PRESS = 1
        const val SWIPE = 2
        const val LONG_PRESS = 3

        @IntDef(NONE, PRESS, LONG_PRESS, SWIPE)
        @Retention(AnnotationRetention.SOURCE)
        annotation class State
    }
}