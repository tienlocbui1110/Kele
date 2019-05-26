package com.lh.kete.listener

import android.support.annotation.WorkerThread

interface OnWorkerThreadListener {
    @WorkerThread
    fun onUpdate(percent: Int, infoText: String)

    @WorkerThread
    fun onCompleted()
}