package com.lh.kete.algorithm.string

import android.text.SpannableStringBuilder

class AdvancedStringBuilder {
    private val builder = SpannableStringBuilder()

    fun append(charSequence: CharSequence?): AdvancedStringBuilder {
        builder.append(charSequence)
        return this
    }

    fun append(custom: StringType): AdvancedStringBuilder {
        builder.append(custom.getString())
        return this
    }

    fun newLine(): AdvancedStringBuilder {
        builder.appendln()
        return this
    }

    fun toCharSequence(): CharSequence {
        return builder
    }
}