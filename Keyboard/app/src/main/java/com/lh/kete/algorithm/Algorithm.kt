package com.lh.kete.algorithm

interface Algorithm<Input, Output> {
    fun doCalculate(obj: Any?, i: Input, callback: Callback<Output>)

    interface Callback<Output> {
        fun onDone(obj: Any?, result: Output)
    }
}
