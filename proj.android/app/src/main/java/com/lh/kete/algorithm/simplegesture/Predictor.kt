package com.lh.kete.algorithm.simplegesture

import android.app.Activity
import com.lh.kete.algorithm.Algorithm
import com.lh.kete.algorithm.common.Path
import com.lh.kete.algorithm.common.Point
import com.lh.kete.algorithm.common.PolylineModel
import com.lh.kete.data.KeteConfig
import com.lh.kete.utils.KeteUtils
import java.util.*
import kotlin.collections.HashMap

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class Predictor : Algorithm<Path, PredictorResult> {
    private val buttonMapper = HashMap<String, Point>()
    private val baseModel: MutableList<Pair<PolylineModel, String>>

    constructor(activity: Activity, kete: KeteConfig) {
        //kete to point"
        for (i in 0 until kete.buttonConfig.size) {
            val button = kete.buttonConfig[i]
            if (button.char == null)
                continue
            val fX = button.x + button.width / 2
            val fY = button.y + button.height / 2
            buttonMapper[button.char] = Point(fX, fY)
        }

        // Simple mapping sample to model
        baseModel = LinkedList()
        buildBaseModel(activity, kete)
    }

    override fun doCalculate(path: Path, callback: Algorithm.Callback<PredictorResult>) {
        //TODO: Hiện tại set cứng thuộc tính x_range và y_range
        val X_RANGE = 12f
        val Y_RANGE = 30f
        // Do bruteForce first
        val userModel = path.toPolylineModel().getPointList()
        val result = PredictorResult()

        baseModel.forEach {
            var avgDistance = 0f
            val currentModel = it.first.getPointList()
            // Optimize
            // Check if first character in range
            if (Math.abs(currentModel[0].x - userModel[0].x) >= X_RANGE
                || Math.abs(currentModel[0].y - userModel[0].y) >= Y_RANGE
            ) {
                return@forEach
            }
            for (i in 0 until PolylineModel.N_POINTS) {
                avgDistance += KeteUtils.distance(
                    userModel[i].x,
                    userModel[i].y,
                    currentModel[i].x,
                    currentModel[i].y
                )
            }
            result.addResult(it.second, avgDistance / PolylineModel.N_POINTS)
        }
        callback.onDone(result)
    }

    private fun buildBaseModel(activity: Activity, kete: KeteConfig) {
        val inputStream = activity.assets.open("vni_dic.txt").bufferedReader()
        var line = inputStream.readLine()
        while (line != null) {
            if (line.isEmpty()) {
                line = inputStream.readLine()
                continue
            }
            val splitString = line.split(" - ")
            if (splitString.size == 2) {
                baseModel.add(Pair(buildPolylineModelFromString(splitString[1]), splitString[0]))
            }
            line = inputStream.readLine()
        }
        inputStream.close()
    }

    private fun buildPolylineModelFromString(charSeq: String): PolylineModel {
        val pathBuilder = Path.Builder()
        for (i in 0 until charSeq.length) {
            val character = charSeq[i].toString()
            buttonMapper[character]?.let {
                pathBuilder.appendPoint(it)
            }
        }
        return pathBuilder.build().toPolylineModel()
    }
}