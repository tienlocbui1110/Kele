package com.lh.kete.listener

import android.view.MotionEvent
import com.lh.kete.views.KeteButton

interface KeteGestureListener {
    fun onClick(event: MotionEvent?, button: KeteButton)

    fun onLongClick(event: MotionEvent?, button: KeteButton)

    fun onSwipe(
        startPos: MotionEvent?,
        endPos: MotionEvent?,
        startButton: KeteButton?,
        endButton: KeteButton?
    )

    fun onKeyUp(event: MotionEvent?)

    fun onPressDown(event: MotionEvent?, button: KeteButton)
}
