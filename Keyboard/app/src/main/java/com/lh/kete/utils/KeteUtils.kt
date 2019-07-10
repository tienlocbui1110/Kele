package com.lh.kete.utils

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.security.NoSuchAlgorithmException


object KeteUtils {
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

    fun md5(s: String): String {
        val md5 = "MD5"
        try {
            // Create MD5 Hash
            val digest = java.security.MessageDigest
                    .getInstance(md5)
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()

            // Create Hex String
            val hexString = StringBuilder()
            for (aMessageDigest in messageDigest) {
                var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                while (h.length < 2)
                    h = "0$h"
                hexString.append(h)
            }
            return hexString.toString()

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return ""
    }
}