package com.lh.kete.activity.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.IdRes
import android.support.annotation.MainThread
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.lh.kete.R
import com.lh.kete.views.KeteLayout

/**
 * Created by Tien Loc Bui on 18/03/2019.
 */

class MainActivity : AppCompatActivity() {

    private lateinit var rootView: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            init()
        } catch (e: Exception) {
            showErrorLayout(e)
        }
    }

    private fun init() {
        setContentView(KeteLayout(this, null))
    }

    fun showErrorLayout(e : Exception) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            showErrorLayoutWithMessage(e.message)
        } else {
            Handler(Looper.getMainLooper()).post {
                showErrorLayoutWithMessage(e.message)
            }
        }
    }

    @MainThread
    fun showErrorLayoutWithMessage(message: String?) {
        setContentView(R.layout.error_layout)
        val textError: TextView = bind(R.id.txt_error) as TextView
        textError.text = message?.let { it }
    }

    private fun bind(@IdRes id: Int): View {
        return findViewById(id)
    }

}