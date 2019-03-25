package com.lh.kete.defs

import android.support.annotation.IntDef

import java.lang.annotation.RetentionPolicy

object KeteDefs {


    // Declare the @IntDef for these constants
    @IntDef(null_init, default_init)
    @Retention(AnnotationRetention.SOURCE)
    annotation class ConstructType

    // Constants
    const val null_init = 0
    const val default_init = 1
}
