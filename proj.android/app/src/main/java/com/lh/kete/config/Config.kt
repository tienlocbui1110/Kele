package com.lh.kete.config

import com.lh.kete.BuildConfig

class Config {
    //    private var layout
    class Layout {
        companion object {
            const val QWERTY = "default_layout.json"
            const val MODERN = "modern_layout.json"
        }
    }

    companion object {
        val HOST = if (BuildConfig.DEBUG) {
            "http://192.168.1.2:3000"
        } else {
            "http://35.240.187.44"
        }

        private var layout: String = Layout.QWERTY

        fun getLayoutAsset(): String {
            return layout
        }

        fun changeLayoutAsset(assetName: String) {
            if (assetName == Layout.QWERTY || assetName == Layout.MODERN)
                layout = assetName
            else
                layout = Layout.QWERTY
        }
    }
}