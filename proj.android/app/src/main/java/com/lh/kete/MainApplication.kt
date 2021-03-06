package com.lh.kete

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class MainApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context

        fun getAppContext(): Context {
            return context
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}