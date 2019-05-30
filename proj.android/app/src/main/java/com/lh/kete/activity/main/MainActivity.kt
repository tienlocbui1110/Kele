package com.lh.kete.activity.main

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.AnyThread
import android.support.annotation.IdRes
import android.support.annotation.MainThread
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import com.google.gson.GsonBuilder
import com.lh.kete.MainApplication
import com.lh.kete.R
import com.lh.kete.algorithm.Algorithm
import com.lh.kete.algorithm.common.Path
import com.lh.kete.algorithm.common.Point
import com.lh.kete.algorithm.simplegesture.Predictor
import com.lh.kete.algorithm.simplegesture.PredictorResult
import com.lh.kete.algorithm.string.AdvancedStringBuilder
import com.lh.kete.algorithm.string.BoldString
import com.lh.kete.config.Config
import com.lh.kete.data.Information
import com.lh.kete.data.KeteConfig
import com.lh.kete.db.KeteContract
import com.lh.kete.db.SQLiteHelper
import com.lh.kete.listener.KeteGestureListener
import com.lh.kete.listener.KeteGestureListenerAdapter
import com.lh.kete.listener.OnWorkerThreadListener
import com.lh.kete.network.UserTracking
import com.lh.kete.threadpool.KeteExec
import com.lh.kete.utils.KeteUtils
import com.lh.kete.views.KeteButton
import com.lh.kete.views.KeteLayout
import java.lang.RuntimeException
import java.lang.StringBuilder

/**
 * Created by Tien Loc Bui on 18/03/2019.
 */

class MainActivity : AppCompatActivity(), OnWorkerThreadListener {
    private lateinit var keteLayout: KeteLayout
    private lateinit var textPreviewer: TextView
    private lateinit var loaderView: View
    private lateinit var progressText: TextView
    private lateinit var progressInfoText: TextView
    private lateinit var mPresenter: Presenter
    private lateinit var keteConfig: KeteConfig

    private var isLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_main)
            keteLayout = bind(R.id.kete) as KeteLayout
            textPreviewer = bind(R.id.text_preview) as TextView
            loaderView = bind(R.id.loader_view)
            progressText = bind(R.id.progress_text_percentage) as TextView
            progressInfoText = bind(R.id.progress_info_text) as TextView
            val jsonIntent = intent.getStringExtra(KeteConfig.KETE_STRING_EXTRAS) ?: null
            init(jsonIntent)
        } catch (e: Exception) {
            showErrorLayout(e)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_layout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (!isLoaded) return super.onOptionsItemSelected(item)

        return when (item.itemId) {
            R.id.menu_layout_qwerty -> {
                Config.changeLayoutAsset(Config.Layout.QWERTY)
                init(null)
                true
            }
            R.id.menu_layout_modern -> {
                Config.changeLayoutAsset(Config.Layout.MODERN)
                init(null)
                true
            }
            R.id.menu_layout_show_info -> {
                showLayoutInfo()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     *
     * @see OnWorkerThreadListener
     */
    @AnyThread
    override fun onUpdate(percent: Int, infoText: String) {
        runOnUiThread {
            setProgressText(percent)
            setProgressInfoText(infoText)
        }
    }

    /**
     *
     * @see OnWorkerThreadListener
     */
    @AnyThread
    override fun onCompleted() {
        runOnUiThread {
            loaderView.visibility = View.GONE
            progressText.text = ""
            progressInfoText.text = ""
            // IMPORTANT: Set layout_id to INFORMATION
            // Set data to Keyboard
            keteLayout.setLayoutData(keteConfig)
            keteLayout.setOnGestureListener(mPresenter.gestureListener)
            isLoaded = true
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
        textPreviewer.text = charSeq
    }

    @UiThread
    private fun setProgressText(current: Int) {
        progressText.text = String.format("%d%%", current)
    }

    @UiThread
    private fun setProgressInfoText(text: CharSequence) {
        progressInfoText.text = text
    }

    private fun showLayoutInfo() {
        val infoBuilder = AdvancedStringBuilder()
        infoBuilder.append("Layout id:              ")
                .append(BoldString(Information.LAYOUT_ID ?: "unknown"))
                .newLine()
                .append("Average distance:  ")
                .append(BoldString(Information.AVERAGE_DISTANCE?.toString()
                        ?: "invalid")).append("px")
                .newLine()
                .append("Conflict:               ")
                .append(BoldString(Information.CONFLICT_PERCENT?.toString()
                        ?: "invalid")).append("%")
                .newLine()

        AlertDialog.Builder(this).setMessage(infoBuilder.toCharSequence())
                .setNeutralButton("OK", null).create().show()
    }

    private fun init(jsonIntent: String?) {
        // Step 1: Show loading + set isLoaded = false
        loaderView.visibility = View.VISIBLE
        setProgressText(0)
        isLoaded = false
        // Step 2: Handle init in new thread
        KeteExec.doBackground(Runnable {
            try {
                // Step 2: read json from file or intent
                val jsonLayout = jsonIntent
                        ?: KeteUtils.readJsonConfigFromAssets(this, Config.getLayoutAsset())
                // Step 3: CalculateHash of layout
                Information.LAYOUT_HASH = KeteUtils.md5(jsonLayout)
                // Step 4: Convert json string to keyboard config.
                keteConfig = GsonBuilder()
                        .serializeNulls()
                        .excludeFieldsWithoutExposeAnnotation()
                        .create()
                        .fromJson(jsonLayout, KeteConfig::class.java)
                Information.LAYOUT_ID = keteConfig.id
                // Step 5: Setup Presenter & callback when init done.
                mPresenter = Presenter(this, keteConfig, this)
            } catch (e: Exception) {
                showErrorLayout(e)
            }
        })
    }

    private fun bind(@IdRes id: Int): View {
        return findViewById(id)
    }

    fun getKeteWidth(): Int {
        return keteLayout.width
    }

    fun getKeteHeight(): Int {
        return keteLayout.height
    }
}

@Suppress("ConvertSecondaryConstructorToPrimary")
private class Presenter : Algorithm.Callback<PredictorResult> {
    private val pathBuilder = Path.Builder()
    val gestureListener: KeteGestureListener
    val predictor: Predictor
    val mainView: MainActivity

    @WorkerThread
    constructor(view: MainActivity, keteConfig: KeteConfig, threadListener: OnWorkerThreadListener) {
        //Step 1: Init database
        initDatabase(threadListener)
        predictor = Predictor(view, keteConfig, threadListener)
        mainView = view

        gestureListener = object : KeteGestureListenerAdapter() {
            private var startTime: Long = 0L
            override fun onSwipe(startPos: MotionEvent, endPos: MotionEvent,
                                 startButton: KeteButton?, endButton: KeteButton?) {
                super.onSwipe(startPos, endPos, startButton, endButton)
                pathBuilder.appendPoint(getPercentagePosition(endPos.x, endPos.y))
            }

            override fun onPressDown(event: MotionEvent, button: KeteButton?) {
                super.onPressDown(event, button)
                startTime = System.nanoTime()
                pathBuilder.appendPoint(getPercentagePosition(event.x, event.y))

            }

            override fun onKeyUp(event: MotionEvent) {
                super.onKeyUp(event)
                val endTime = System.nanoTime() - startTime
                val path = pathBuilder.build()
                pathBuilder.reset()
                if (path.isValid()) {
                    KeteExec.doBackground(Runnable {
                        try {
                            val builder = UserTracking.Builder()
                                    .addLayoutId(Information.LAYOUT_ID!!)
                                    .addTime(endTime / 1000000f)
                                    .addInputMethod(SQLiteHelper.VNI_LAST_INPUT_METHOD)
                                    .addPoint(path.toPolylineModel().getPointList())
                            predictor.doCalculate(builder, path, this@Presenter)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            mainView.showErrorLayout(RuntimeException("Database error. Please delete data and try again."))
                        }
                    })
                }
            }
        }
    }

    @WorkerThread
    private fun initDatabase(threadListener: OnWorkerThreadListener) {
        val db = SQLiteHelper(MainApplication.getAppContext())
        // Step 1: verify database
        db.verify(threadListener)
        db.close()
    }


    private fun getPercentagePosition(x: Float, y: Float): Point {
        val fX = x / mainView.getKeteWidth()
        val fY = y / mainView.getKeteHeight()
        return Point(fX * 100, fY * 100)
    }

    @AnyThread
    override fun onDone(obj: Any?, result: PredictorResult) {
        if (obj != null && obj is UserTracking.Builder) {
           obj.addPredicted(result.getResult()[0].second)
            obj.addChosen(result.getResult()[0].second)
            obj.request()
        }
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