package com.lh.kete.views

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.View
import java.util.*


class AnimationDrawable(view: View) {
    // Set cứng  các thuộc tính animation
    private val MAX_ANIMATION_TIME = 1000
    private val MILIS_PER_FRAME = 1000 / 60f
    private val NEON_COLOR = Color.parseColor("#39ff14")
    private val mPoints: LinkedList<Pair<Pair<Float, Float>, Long>> = LinkedList()
    private val viewContext: View = view

    private val TAG = AnimationDrawable::class.java.canonicalName

    fun addNextPoint(x: Float, y: Float) {
        addNextPoint(x, y, System.currentTimeMillis())
        invalidateSelf()
    }

    fun isEmpty(): Boolean {
        return mPoints.isEmpty()
    }

    fun draw(canvas: Canvas?) {
        if (canvas == null || hasDone())
            return

        val paint = Paint()
        val currentTime = System.currentTimeMillis()
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 15f
        paint.pathEffect = null
        paint.style = Paint.Style.STROKE

        for (i in 0 until mPoints.size - 1) {
            paint.color = getColorFromPoint(mPoints[i + 1], currentTime)
            canvas.drawLine(
                    mPoints[i].first.first, mPoints[i].first.second,
                    mPoints[i + 1].first.first, mPoints[i + 1].first.second, paint
            )
        }
        invalidateSelf()
    }

    fun hasDone(): Boolean {
        return mPoints.size < 2
    }

    fun reset() {
        mPoints.clear()
    }

    private fun invalidateSelf() {
        // Invalidate time
        val currentTime = System.currentTimeMillis()
        val it = mPoints.iterator()
        while (it.hasNext()) {
            val point = it.next()
            if (currentTime - point.second > MAX_ANIMATION_TIME)
                it.remove()
        }
        if (mPoints.size == 0) {
            return
        }

        // require view.invalidate
        viewContext.invalidate()
    }

    private fun getColorFromPoint(point: Pair<Pair<Float, Float>, Long>, currentTime: Long): Int {
        val timePassed = currentTime - point.second
        val alphaF = Math.min(timePassed / MAX_ANIMATION_TIME.toFloat(), 1f)
        val alpha = ((1 - alphaF) * 255).toInt()
        val r = Color.red(NEON_COLOR)
        val g = Color.green(NEON_COLOR)
        val b = Color.blue(NEON_COLOR)
        return Color.argb(alpha, r, g, b)
    }

    private fun addNextPoint(x: Float, y: Float, time: Long) {
        if (mPoints.size > 0 && time - mPoints.last.second < MILIS_PER_FRAME)
            return
        val positionPair = Pair(x, y)
        val point = Pair(positionPair, time)
        mPoints.add(point)
    }
}