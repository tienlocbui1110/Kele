package com.lh.kete.algorithm

interface Algorithm<Input, Output> {
    fun doCalculate(i: Input, callback: Callback<Output>)

    interface Callback<Output> {
        fun onDone(result: Output)
    }
}
