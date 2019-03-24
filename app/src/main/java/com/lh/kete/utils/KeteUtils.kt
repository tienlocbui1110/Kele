package com.lh.kete.utils

class KeteUtils {
    companion object {
        fun percentToPx(base: Int?, percent: Float?): Int {
            if (base == null || percent == null)
                return 0
            return (base * percent / 100).toInt()
        }
    }
}