/*
 * Copyright (C) 2017 Florian Dreier
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

import android.support.annotation.StringRes
import android.text.TextUtils
import de.dreier.mytargets.shared.SharedApplicationInstance

class HtmlInfoBuilder {
    private val info = StringBuilder()

    fun addLine(key: Int, value: Any) {
        if (info.isNotEmpty()) {
            info.append("<br>")
        }
        info.append(getKeyValueLine(key, value))
    }

    fun addLine(key: String, value: Any) {
        if (info.isNotEmpty()) {
            info.append("<br>")
        }
        info.append(getKeyValueLine(key, value))
    }

    private fun getKeyValueLine(key: String, value: Any): String {
        return String.format("%s: <b>%s</b>", key, TextUtils.htmlEncode(value.toString()))
    }

    private fun getKeyValueLine(@StringRes key: Int, value: Any): String {
        return getKeyValueLine(SharedApplicationInstance.getStr(key), value)
    }

    override fun toString(): String {
        return info.toString()
    }
}
