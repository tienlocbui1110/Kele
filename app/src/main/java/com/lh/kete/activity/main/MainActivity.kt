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
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lh.kete.R
import com.lh.kete.data.KeteConfig
import com.lh.kete.utils.KeteUtils
import com.lh.kete.views.KeteLayout

/**
 * Created by Tien Loc Bui on 18/03/2019.
 */

class MainActivity : AppCompatActivity() {

    private lateinit var rootView: FrameLayout
    private val SAMPLE_LAYOUT_JSON_ASSET = "sample_layout.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            init()
        } catch (e: Exception) {
            showErrorLayout(e)
        }
    }

    fun showErrorLayout(e: Exception) {
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

    private fun init() {
        val jsonLayout = KeteUtils.readJsonConfigFromAssets(this, SAMPLE_LAYOUT_JSON_ASSET)
        val keteConfig =
            GsonBuilder()
                .serializeNulls()
                .excludeFieldsWithoutExposeAnnotation()
                .create()
                .fromJson(jsonLayout, KeteConfig::class.java)
        setContentView(KeteLayout(this, keteConfig))
    }

    private fun bind(@IdRes id: Int): View {
        return findViewById(id)
    }
}