package com.lh.kete.views

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.View
import java.util.*


class AnimationDrawable(view: View) {
    // Set cứng  các thuộc tính animation
    private val MAX_ANIMATION_TIME = 1000
    private val NEON_COLOR = Color.parseColor("#39ff14")
    private var bitmapDrawable: BitmapDrawable? = null
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
//        Log.d(TAG, String.format("Draw Animation with %d points.", mPoints.size))
        if (canvas == null)
            return
        bitmapDrawable?.let {
            it.setBounds(0, 0, it.bitmap.width, it.bitmap.height)
            it.draw(canvas)
        }

        invalidateSelf()
    }

    fun hasDone(): Boolean {
        return mPoints.size < 2
    }

    fun reset() {
        if (bitmapDrawable?.bitmap?.isRecycled == false) {
            bitmapDrawable?.bitmap?.recycle()
        }
        bitmapDrawable = null
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
        if (mPoints.size < 2)
            return
        // Draw current points
        drawPaths(currentTime)

        // require view.invalidate
        viewContext.invalidate()
    }

    private fun drawPaths(currentTime: Long) {
        var bm = bitmapDrawable?.bitmap
        if (bm != null && !bm.isRecycled)
            bm.recycle()
        bm = createAlphaBmp()
        bitmapDrawable = BitmapDrawable(viewContext.resources, bm)
        val canvas = Canvas(bm)
        val paint = Paint()
        val path = Path()

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

    private fun createAlphaBmp(): Bitmap {
        return Bitmap.createBitmap(viewContext.width, viewContext.height, Bitmap.Config.ARGB_8888)
    }


    private fun addNextPoint(x: Float, y: Float, time: Long) {
        val positionPair = Pair(x, y)
        val point = Pair(positionPair, time)
        mPoints.add(point)
    }
}