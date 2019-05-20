package com.lh.kete.algorithm.simplegesture

import java.util.*

class PredictorResult {
    private val MAX_STACK = 5
    private val result = LinkedList<Pair<Float, String>>()
    fun addResult(predition: String, avgDistance: Float) {
        for (i in 0 until result.size) {
            if (avgDistance < result[i].first) {
                result.add(i, Pair(avgDistance, predition))
                break
            }
        }
        if (result.isEmpty())
            result.add(Pair(avgDistance, predition))
        verify()
    }

    fun getResult(): List<Pair<Float, String>> {
        return result
    }

    private fun verify() {
        while (result.size > MAX_STACK)
            result.removeAt(result.size - 1)
    }
}