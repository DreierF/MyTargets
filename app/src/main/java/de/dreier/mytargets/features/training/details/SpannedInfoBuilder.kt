/*
 * Copyright (C) 2018 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.features.training.details

import android.graphics.Typeface.BOLD
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.style.StyleSpan
import de.dreier.mytargets.shared.SharedApplicationInstance

class SpannedInfoBuilder {
    private val info = SpannableStringBuilder()

    fun addLine(key: Int, value: Any) {
        addLine(SharedApplicationInstance.getStr(key), value)
    }

    fun addLine(key: String, value: Any) {
        if (info.isNotEmpty()) {
            info.appendln()
        }
        info.append("$key: ")
        val start = info.length
        info.append(value.toString())
        info.setSpan(StyleSpan(BOLD), start, info.length, SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    fun toSpanned(): Spanned {
        return info
    }
}
