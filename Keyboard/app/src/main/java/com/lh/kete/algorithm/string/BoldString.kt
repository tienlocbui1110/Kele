package com.lh.kete.algorithm.string

import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.StyleSpan


class BoldString : StringType {
    private val charSequence: CharSequence

    constructor(charSequence: CharSequence) {
        val boldOption = SpannableString(charSequence)
        boldOption.setSpan(StyleSpan(Typeface.BOLD), 0, charSequence.length, 0)
        this.charSequence = boldOption
    }


    override fun getString(): CharSequence {
        return charSequence
    }
}