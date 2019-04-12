package com.lh.kete.activity.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.IdRes
import android.support.annotation.MainThread
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import com.google.gson.GsonBuilder
import com.lh.kete.R
import com.lh.kete.data.KeteConfig
import com.lh.kete.listener.KeteGestureListenerAdapter
import com.lh.kete.utils.KeteUtils
import com.lh.kete.views.KeteButton
import com.lh.kete.views.KeteLayout

/**
 * Created by Tien Loc Bui on 18/03/2019.
 */

class MainActivity : AppCompatActivity() {
    private val MAX_TEXT_PREVIEW_CHAR = 500
    private val CHAR_REMOVE_NUMBER = 100

    private lateinit var keteLayout: KeteLayout
    private val SAMPLE_LAYOUT_JSON_ASSET = "sample_layout.json"
    private lateinit var textPreview: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)
            keteLayout = bind(R.id.kete) as KeteLayout
            textPreview = bind(R.id.text_preview) as TextView
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
        keteLayout.setLayoutData(keteConfig)
        keteLayout.setOnGestureListener(object : KeteGestureListenerAdapter() {
            override fun onClick(event: MotionEvent?, button: KeteButton) {
                super.onClick(event, button)
                updatePreviewTextFromButton(button)
            }

            override fun onSwipe(
                startPos: MotionEvent?,
                endPos: MotionEvent?,
                startButton: KeteButton?,
                endButton: KeteButton?
            ) {
                super.onSwipe(startPos, endPos, startButton, endButton)
                if (endButton != null && endButton != startButton) {
                    updatePreviewTextFromButton(endButton)
                }
            }
        })
    }

    private fun updatePreviewTextFromButton(button: KeteButton) {
        var currentText: String? = textPreview.text.toString()
        currentText = currentText ?: ""
        currentText += button.getConfig()?.char
        if (currentText.length > MAX_TEXT_PREVIEW_CHAR)
            currentText = currentText.substring(CHAR_REMOVE_NUMBER, currentText.length)
        textPreview.text = currentText
    }

    private fun bind(@IdRes id: Int): View {
        return findViewById(id)
    }
}