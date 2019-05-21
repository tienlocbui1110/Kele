package com.lh.kete.activity.main

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.AnyThread
import android.support.annotation.IdRes
import android.support.annotation.MainThread
import android.support.annotation.WorkerThread
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import com.google.gson.GsonBuilder
import com.lh.kete.R
import com.lh.kete.algorithm.Algorithm
import com.lh.kete.algorithm.common.Path
import com.lh.kete.algorithm.common.Point
import com.lh.kete.algorithm.simplegesture.Predictor
import com.lh.kete.algorithm.simplegesture.PredictorResult
import com.lh.kete.data.KeteConfig
import com.lh.kete.listener.KeteGestureListener
import com.lh.kete.listener.KeteGestureListenerAdapter
import com.lh.kete.utils.KeteUtils
import com.lh.kete.views.KeteButton
import com.lh.kete.views.KeteLayout
import java.lang.StringBuilder

/**
 * Created by Tien Loc Bui on 18/03/2019.
 */

class MainActivity : AppCompatActivity() {
    private val MAX_TEXT_PREVIEW_CHAR = 500
    private val CHAR_REMOVE_NUMBER = 100

    private lateinit var keteLayout: KeteLayout
    private val DEFAULT_LAYOUT = "default_layout.json"
    private lateinit var textPreview: TextView
    private lateinit var mPresenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)
            keteLayout = bind(R.id.kete) as KeteLayout
            textPreview = bind(R.id.text_preview) as TextView
            val jsonIntent = intent.getStringExtra(KeteConfig.KETE_STRING_EXTRAS) ?: null
            init(jsonIntent)
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

    fun setTextPreview(charSeq: String) {
        textPreview.text = charSeq
    }

    private fun init(jsonIntent: String?) {
        val jsonLayout = jsonIntent ?: KeteUtils.readJsonConfigFromAssets(this, DEFAULT_LAYOUT)
        val keteConfig = GsonBuilder()
                .serializeNulls()
                .excludeFieldsWithoutExposeAnnotation()
                .create()
                .fromJson(jsonLayout, KeteConfig::class.java)
        mPresenter = Presenter(this, keteConfig)
        keteLayout.setLayoutData(keteConfig)
        keteLayout.setOnGestureListener(mPresenter.gestureListener)
    }

    private fun bind(@IdRes id: Int): View {
        return findViewById(id)
    }

    fun getKeteX(): Int {
        return keteLayout.x.toInt()
    }

    fun getKeteY(): Int {
        return keteLayout.y.toInt()
    }

    fun getKeteWidth(): Int {
        return keteLayout.width
    }

    fun getKeteHeight(): Int {
        return keteLayout.height
    }
}

private class Presenter(view: MainActivity, keteConfig: KeteConfig) : Algorithm.Callback<PredictorResult> {
    private val pathBuilder = Path.Builder()
    val gestureListener: KeteGestureListener
    val predictor: Predictor = Predictor(view, keteConfig)
    val mainView: MainActivity = view

    init {
        gestureListener = object : KeteGestureListenerAdapter() {
            override fun onSwipe(startPos: MotionEvent, endPos: MotionEvent,
                                 startButton: KeteButton?, endButton: KeteButton?) {
                super.onSwipe(startPos, endPos, startButton, endButton)
                pathBuilder.appendPoint(getPercentagePosition(endPos.x, endPos.y))
            }

            override fun onPressDown(event: MotionEvent, button: KeteButton?) {
                super.onPressDown(event, button)
                pathBuilder.appendPoint(getPercentagePosition(event.x, event.y))

            }

            override fun onKeyUp(event: MotionEvent) {
                super.onKeyUp(event)
                val path = pathBuilder.build()
                pathBuilder.reset()
                if (path.isValid()) {
                    Thread {
                        val begin = System.currentTimeMillis()
                        predictor.doCalculate(path, this@Presenter)
                        val end = System.currentTimeMillis() - begin
                        Log.d("Calculate time", end.toString())
                    }.start()
                }
            }
        }
    }

    private fun getPercentagePosition(x: Float, y: Float): Point {
        val fX = x / mainView.getKeteWidth()
        val fY = y / mainView.getKeteHeight()
        return Point(fX * 100, fY * 100)
    }

    @AnyThread
    override fun onDone(result: PredictorResult) {
        val stringList = result.getResult()
        val builder = StringBuilder()
        for (i in 0 until stringList.size) {
            builder.append(
                    String.format(
                            "Predict: %s  -- Average distance: %f", stringList[i].second,
                            stringList[i].first
                    )
            ).append("\n")
        }
        Handler(Looper.getMainLooper()).post {
            mainView.setTextPreview(builder.toString())
        }
    }
}