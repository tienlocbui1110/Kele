package com.lh.kete.network

import android.app.Application
import android.util.Log
import com.lh.kete.algorithm.common.Point
import com.lh.kete.config.Config
import com.lh.kete.utils.KeteUtils
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit


class UserTracking {

    class Builder {
        private val obj = JSONObject()
        private val JSON_TYPE = MediaType.parse("application/json; charset=utf-8")
        private val client: OkHttpClient = OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .build()
        private var isRequest = false

        fun addLayoutId(layoutId: String): Builder {
            obj.put("layoutId", layoutId)
            return this
        }

        fun addTime(userTime: Float): Builder {
            obj.put("time", userTime)
            return this
        }

        fun addChosen(chosenWord: String): Builder {
            obj.put("chosen", chosenWord)
            return this
        }

        fun addAvgDistance(distance: Float): Builder {
            obj.put("avg_distance", distance)
            return this
        }

        fun addInputMethod(inputMethod: String): Builder {
            obj.put("input_method", inputMethod)
            return this
        }

        fun addPoint(points: List<Point>): Builder {
            val pointsObj = JSONArray()
            points.forEach {
                val pointObj = JSONObject()
                pointObj.put("x", it.mX)
                pointObj.put("y", it.mY)
                pointsObj.put(pointObj)
            }
            obj.put("points", pointsObj)
            return this
        }

        fun request() {
            obj.put("input_type", 1)
            if (!isRequest) {
                isRequest = true
                val body = RequestBody.create(JSON_TYPE, obj.toString())
                val request = Request.Builder()
                        .url(Config.HOST + "/user")
                        .post(body)
                        .build()
                client.newCall(request).execute()
            }
        }
    }
}