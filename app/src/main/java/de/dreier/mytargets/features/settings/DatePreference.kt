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

package de.dreier.mytargets.features.settings

import android.content.Context
import android.content.res.TypedArray
import androidx.preference.DialogPreference
import android.util.AttributeSet

import org.threeten.bp.LocalDate

class DatePreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs) {
    var date = LocalDate.now()

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any? {
        return a.getString(index)
    }

    override fun onSetInitialValue(restoreValue: Boolean, defaultValue: Any?) {
        val value: String = if (restoreValue) {
            if (defaultValue == null) {
                getPersistedString(LocalDate.now().toString())
            } else {
                getPersistedString(defaultValue.toString())
            }
        } else {
            defaultValue!!.toString()
        }

        date = LocalDate.parse(value)
    }

    fun persistDateValue(value: LocalDate) {
        persistString(value.toString())
    }
}
