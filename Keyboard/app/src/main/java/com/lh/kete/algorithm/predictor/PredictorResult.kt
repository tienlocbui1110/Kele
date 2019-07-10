package com.lh.kete.algorithm.predictor

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
        if (result.size < MAX_STACK)
            result.add(Pair(avgDistance, predition))
        verify()
    }

    fun getResult(): List<Pair<Float, String>> {
        return result
    }

    fun remove(item: String) {
        var needToRemove: Pair<Float, String>? = null
        result.forEach {
            if (it.second == item) {
                needToRemove = it
            }
        }
        needToRemove?.let {
            result.remove(it)
        }
    }

    private fun verify() {
        while (result.size > MAX_STACK)
            result.removeAt(result.size - 1)
    }
}