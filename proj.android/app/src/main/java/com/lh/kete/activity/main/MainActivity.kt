package com.lh.kete.activity.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.*
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.gson.GsonBuilder
import com.lh.kete.MainApplication
import com.lh.kete.R
import com.lh.kete.activity.pref.SettingActivity
import com.lh.kete.algorithm.Algorithm
import com.lh.kete.algorithm.common.Path
import com.lh.kete.algorithm.common.Point
import com.lh.kete.algorithm.common.PolylineModel
import com.lh.kete.algorithm.predictor.*
import com.lh.kete.data.Information
import com.lh.kete.data.KeteConfig
import com.lh.kete.db.SQLiteHelper
import com.lh.kete.listener.KeteGestureListener
import com.lh.kete.listener.KeteGestureListenerAdapter
import com.lh.kete.listener.OnWorkerThreadListener
import com.lh.kete.network.UserTracking
import com.lh.kete.threadpool.KeteExec
import com.lh.kete.utils.KeteUtils
import com.lh.kete.views.KeteButton
import com.lh.kete.views.KeteLayout


/**
 * Created by Tien Loc Bui on 18/03/2019.
 */

@Suppress("LocalVariableName")
class MainActivity : AppCompatActivity(), OnWorkerThreadListener {
    private lateinit var keteLayout: KeteLayout
    private lateinit var textPreviewer: TextView
    private lateinit var loaderView: View
    private lateinit var progressText: TextView
    private lateinit var progressInfoText: TextView
    private lateinit var mPresenter: Presenter
    private lateinit var keteConfig: KeteConfig

    private lateinit var predictLayout: View
    private lateinit var predictText1: Button
    private lateinit var predictText2: Button
    private lateinit var predictText3: Button
    private lateinit var predictManual: Button

    private var textPreviewerBuilder = StringBuilder()

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
            predictLayout = bind(R.id.text_predicted)
            predictText1 = bind(R.id.predict1) as Button
            predictText2 = bind(R.id.predict2) as Button
            predictText3 = bind(R.id.predict3) as Button
            predictManual = bind(R.id.predict_manual) as Button
            predictManual.setOnClickListener {
                showManualPredict()
            }

            checkingPrefs()
            init()
        } catch (e: Exception) {
            showErrorLayout(e)
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkingPrefs()) {
            init()
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
            R.id.menu_setting -> {
                val settingIntent = Intent(this, SettingActivity::class.java)
                startActivity(settingIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkingPrefs(): Boolean {
        val DEFAULT_POINTS_N = 50
        // ------ Layout preferences --------------------------------------------------------- //
        var flag = false
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        var layout = prefs.getString(resources.getString(R.string.layout_prefs), null)
        if (layout == null) {
            prefs.edit().putString(resources.getString(R.string.layout_prefs),
                    resources.getString(R.string.layout_qwerty)).apply()
            layout = resources.getString(R.string.layout_qwerty)
        }

        if (layout != Information.LAYOUT_ASSET)
            flag = true
        Information.LAYOUT_ASSET = layout

        // ------ Point preferences --------------------------------------------------------- //
        var nPoints = prefs.getString(resources.getString(R.string.points_prefs), null)?.toInt()
        if (nPoints == null) {
            prefs.edit().putString(resources.getString(R.string.points_prefs), "50").apply()
            nPoints = DEFAULT_POINTS_N
        }

        if (nPoints != PolylineModel.N_POINTS)
            flag = true
        PolylineModel.N_POINTS = nPoints

        // ------ Method preferences --------------------------------------------------------- //

        var method = prefs.getString(resources.getString(R.string.method_prefs), null)
        if (method == null) {
            prefs.edit().putString(resources.getString(R.string.method_prefs),
                    resources.getString(R.string.euclid_method)).apply()
            method = resources.getString(R.string.euclid_method)
        }

        if (method != Information.METHOD)
            flag = true
        Information.METHOD = method
        return flag
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

    private fun addTextPreview(charSeq: String) {
        textPreviewerBuilder.append(" ").append(charSeq)
        textPreviewer.text = textPreviewerBuilder.toString()
    }

    fun removeLatestString() {
        if (textPreviewerBuilder.isNotEmpty()) {
            textPreviewerBuilder.replace(textPreviewerBuilder.lastIndexOf(' '), textPreviewerBuilder.length, "")
            textPreviewer.text = textPreviewerBuilder.toString()
        }
    }

    // Track when show predict
    private var userTrackingBuilder: Any? = null
    // Save avgDistance of first predict => Add to builder when clean predict
    private var firstAvgDistance: Float = 0f


    fun showManualPredict() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Từ dự đoán")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        dialogBuilder.setView(input)

        dialogBuilder.setPositiveButton("OK") { _, _ ->
            predictText1.setOnClickListener(null)
            predictText2.setOnClickListener(null)
            predictText3.setOnClickListener(null)
            predictLayout.visibility = View.INVISIBLE
            if (userTrackingBuilder != null && userTrackingBuilder is UserTracking.Builder) {
                val userTracking = userTrackingBuilder as UserTracking.Builder
                userTracking.addChosen(input.text.toString().toUpperCase())
                        .addAvgDistance(-1f)
                KeteExec.doBackground(Runnable {
                    userTracking.request()
                })
                // Add text
                addTextPreview(input.text.toString().toUpperCase())
            }
        }
        dialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        dialogBuilder.show()
    }

    fun showPredict(result: PredictorResult, obj: Any?) {
        userTrackingBuilder = obj
        val predictorOnClick = View.OnClickListener {
            val idx: Int
            when {
                it.id == R.id.predict1 -> {
                    idx = 0
                    addTextPreview(result.getResult()[0].second)
                }
                it.id == R.id.predict2 -> {
                    idx = 1
                    addTextPreview(result.getResult()[1].second)
                }
                else -> {
                    idx = 2
                    addTextPreview(result.getResult()[2].second)
                }
            }
            if (obj != null && obj is UserTracking.Builder) {
                KeteExec.doBackground(Runnable {
                    obj.addChosen(result.getResult()[idx].second)
                            .addAvgDistance(result.getResult()[idx].first)
                    obj.request()
                })
            }
            predictLayout.visibility = View.INVISIBLE
        }

        predictText1.visibility = View.GONE
        predictText2.visibility = View.GONE
        predictText3.visibility = View.GONE

        val len = result.getResult().size
        if (len > 0) {
            predictText1.text = result.getResult()[0].second
            predictText1.visibility = View.VISIBLE
            firstAvgDistance = result.getResult()[0].first
        }
        if (len > 1) {
            predictText2.text = result.getResult()[1].second
            predictText2.visibility = View.VISIBLE
        }
        if (len > 2) {
            predictText3.text = result.getResult()[2].second
            predictText3.visibility = View.VISIBLE
        }
        predictText1.setOnClickListener(predictorOnClick)
        predictText2.setOnClickListener(predictorOnClick)
        predictText3.setOnClickListener(predictorOnClick)
        predictLayout.visibility = View.VISIBLE
    }

    fun clearPredict() {
        predictText1.setOnClickListener(null)
        predictText2.setOnClickListener(null)
        predictText3.setOnClickListener(null)
        predictLayout.visibility = View.INVISIBLE
    }

    @UiThread
    private fun setProgressText(current: Int) {
        progressText.text = String.format("%d%%", current)
    }

    @UiThread
    private fun setProgressInfoText(text: CharSequence) {
        progressInfoText.text = text
    }

    private fun init(jsonString: String?) {
        predictLayout.visibility = View.INVISIBLE
        // Step 1: Show loading + set isLoaded = false
        loaderView.visibility = View.VISIBLE
        setProgressText(0)
        isLoaded = false
        // Step 2: Handle init in new thread
        KeteExec.doBackground(Runnable {
            try {
                // Step 2: read json from file or intent
                val jsonLayout = jsonString
                        ?: KeteUtils.readJsonConfigFromAssets(this, Information.LAYOUT_ASSET!!)
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

    private fun init() {
        init(null)
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

    private var firstButton: KeteButton? = null

    @WorkerThread
    constructor(view: MainActivity, keteConfig: KeteConfig, threadListener: OnWorkerThreadListener) {
        mainView = view

        // Checking and init database if needed
        initDatabase(threadListener)

        // Choose Predictor
        when (Information.METHOD) {
            mainView.resources.getString(R.string.cosine_method) -> predictor = CosineSimilarity(mainView, keteConfig, threadListener)
            mainView.resources.getString(R.string.mahalanobis_method) -> predictor = MahalanobisPredictor(mainView, keteConfig, threadListener)
            mainView.resources.getString(R.string.new_method) -> predictor = NewEuclid(mainView, keteConfig, threadListener)
            else -> predictor = EuclidPredictor(mainView, keteConfig, threadListener)
        }

        gestureListener = object : KeteGestureListenerAdapter() {
            private var startTime: Long = 0L
            override fun onSwipe(startPos: MotionEvent, endPos: MotionEvent,
                                 startButton: KeteButton?, endButton: KeteButton?) {
                super.onSwipe(startPos, endPos, startButton, endButton)
                pathBuilder.appendPoint(getPercentagePosition(endPos.x, endPos.y))
            }

            override fun onPressDown(event: MotionEvent, button: KeteButton?) {
                super.onPressDown(event, button)
                firstButton = button
                startTime = System.nanoTime()
                pathBuilder.appendPoint(getPercentagePosition(event.x, event.y))

            }

            override fun onKeyUp(event: MotionEvent) {
                super.onKeyUp(event)
                val endTime = System.nanoTime() - startTime
                val path = pathBuilder.build()
                pathBuilder.reset()
                mainView.clearPredict()
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
                            mainView.showErrorLayout(
                                    RuntimeException("Database error. Please delete data and try again."))
                        }
                    })
                } else {
                    firstButton?.let {
                        if ("DEL" == it.getConfig()?.char) {
                            mainView.removeLatestString()
                        }
                    }
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
        Handler(Looper.getMainLooper()).post {
            mainView.showPredict(result, obj)
        }
    }
}