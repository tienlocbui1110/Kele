package com.lh.kete.adapter

interface FindView<V> {
    fun getViewFromPosition(x: Float, y: Float): V?
}
