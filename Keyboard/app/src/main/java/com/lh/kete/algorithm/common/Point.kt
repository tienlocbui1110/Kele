package com.lh.kete.algorithm.common

import pl.luwi.series.reducer.Point

class Point : Point {
    val mX: Float
    val mY: Float

    override fun getX(): Double {
        return mX.toDouble()
    }

    override fun getY(): Double {
        return mY.toDouble()
    }

    constructor(x: Float, y: Float) {
        this.mX = x
        this.mY = y
    }

    override fun toString(): String {
        return String.format("(%f,%f)", mX, mY)
    }
}