package com.lh.kete.config

class Config {
    //    private var layout
    class Layout {
        companion object {
            const val QWERTY = "default_layout.json"
            const val MODERN = "modern_layout.json"
        }
    }

    companion object {
        const val HOST = "http://192.168.1.2:3000"

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