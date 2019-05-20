package com.lh.kete.utils

import android.content.Context
import com.lh.kete.data.SpecialKeyList
import com.lh.kete.views.KeteButton
import java.io.BufferedReader
import java.io.InputStreamReader


class KeteUtils {
    companion object {
        fun percentToPx(base: Int?, percent: Float?): Int {
            if (base == null || percent == null)
                return 0
            return (base * percent / 100).toInt()
        }

        fun dpToPx(dp: Int, context: Context): Int {
            return (dp * context.resources.displayMetrics.density).toInt()
        }

        fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
            return Math.sqrt(((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)).toDouble()).toFloat()
        }

        fun readJsonConfigFromAssets(context: Context, assetName: String): String {
            val inputStream = context.assets.open(assetName)
            val stringReader = BufferedReader(InputStreamReader(inputStream))
            val result = StringBuilder()
            while (true) {
                val line = stringReader.readLine()
                if (line != null)
                    result.append(line).append("\n")
                else
                    break
            }
            if (!result.isEmpty()) {
                result.deleteCharAt(result.length - 1)
            }
            return result.toString()
        }

        fun getTextFromButton(keteButton: KeteButton): String {
            val config = keteButton.getConfig()
            config?.specialKey?.let {
                when (it) {
                    SpecialKeyList.ENTER -> return "\n"
                    SpecialKeyList.SPACE -> return " "
                    else -> {
                    }
                }
            }
            config?.char?.let { return it }
            return ""
        }
    }
}