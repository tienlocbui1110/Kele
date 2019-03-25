package com.lh.kete.utils

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader


class KeteUtils {
    companion object {
        fun percentToPx(base: Int?, percent: Float?): Int {
            if (base == null || percent == null)
                return 0
            return (base * percent / 100).toInt()
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
    }
}