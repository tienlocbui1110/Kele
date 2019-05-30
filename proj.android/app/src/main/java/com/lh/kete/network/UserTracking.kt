package com.lh.kete.network

import android.util.Log
import com.lh.kete.algorithm.common.Point
import com.lh.kete.config.Config
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception


class UserTracking {

    class Builder {
        private val obj = JSONObject()
        private val JSON_TYPE = MediaType.parse("application/json; charset=utf-8")
        private val client = OkHttpClient()

        fun addLayoutId(layoutId: String): Builder {
            obj.put("layoutId", layoutId)
            return this
        }

        fun addTime(userTime: Float): Builder {
            obj.put("time", userTime)
            return this
        }

        fun addPredicted(predictedWord: String) : Builder {
            obj.put("predicted", predictedWord)
            return this
        }

        fun addChosen(chosenWord: String): Builder {
            obj.put("chosen", chosenWord)
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
                pointObj.put("x", it.x)
                pointObj.put("y", it.y)
                pointsObj.put(pointObj)
            }
            obj.put("points", pointsObj)
            return this
        }

        fun request() {
            try {
                val body = obj.toString().toRequestBody(JSON_TYPE)
                val request = Request.Builder()
                        .url(Config.HOST + "/user")
                        .post(body)
                        .build()
                client.newCall(request).execute()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("UserTracking", e.message)
            }
        }
    }
}