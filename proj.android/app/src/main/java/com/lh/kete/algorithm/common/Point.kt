package com.lh.kete.algorithm.common

class Point{
    val x: Float
    val y: Float

    constructor(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    override fun toString(): String {
        return String.format("(%f,%f)", x, y)
    }
}