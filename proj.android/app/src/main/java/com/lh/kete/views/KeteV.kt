package com.lh.kete.views

import com.lh.kete.data.AbstractConfig

interface KeteV<T : AbstractConfig?> {
    fun getConfig(): T
}