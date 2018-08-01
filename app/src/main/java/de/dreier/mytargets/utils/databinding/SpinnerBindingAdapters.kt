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

package de.dreier.mytargets.utils.databinding

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.appcompat.widget.AppCompatSpinner
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner

import de.dreier.mytargets.shared.models.Dimension

import de.dreier.mytargets.shared.models.Dimension.Unit.INCH
import de.dreier.mytargets.shared.models.Dimension.Unit.MILLIMETER

@Suppress("unused")
object SpinnerBindingAdapters {

    @JvmStatic
    @BindingAdapter(value = ["selectedUnit", "selectedValueAttrChanged"], requireAll = false)
    fun bindSpinnerData(
        pAppCompatSpinner: AppCompatSpinner,
        newSelectedValue: Dimension.Unit?,
        newTextAttrChanged: InverseBindingListener
    ) {
        pAppCompatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                newTextAttrChanged.onChange()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        if (newSelectedValue != null) {
            val pos = if (newSelectedValue == MILLIMETER) 0 else 1
            pAppCompatSpinner.setSelection(pos, false)
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["selectedUnit", "selectedValueAttrChanged"], requireAll = false)
    fun bindSpinnerData(
        pAppCompatSpinner: Spinner,
        newSelectedValue: Dimension.Unit?,
        newTextAttrChanged: InverseBindingListener
    ) {
        pAppCompatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                newTextAttrChanged.onChange()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        if (newSelectedValue != null) {
            val pos = if (newSelectedValue == MILLIMETER) 0 else 1
            pAppCompatSpinner.setSelection(pos, false)
        }
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "selectedUnit", event = "selectedValueAttrChanged")
    fun captureSelectedValue(pAppCompatSpinner: AppCompatSpinner): Dimension.Unit {
        return if (pAppCompatSpinner.selectedItemPosition == 0) MILLIMETER else INCH
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "selectedUnit", event = "selectedValueAttrChanged")
    fun captureSelectedValue(pAppCompatSpinner: Spinner): Dimension.Unit {
        return if (pAppCompatSpinner.selectedItemPosition == 0) MILLIMETER else INCH
    }
}
