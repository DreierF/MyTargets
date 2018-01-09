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

package de.dreier.mytargets.utils

import android.databinding.BindingAdapter
import android.view.View
import android.widget.ImageView

object DataBindingAdapters {

    @JvmStatic
    @BindingAdapter("android:src")
    fun setImageResource(imageView: ImageView, resource: Int) {
        imageView.setImageResource(resource)
    }

    @JvmStatic
    @BindingAdapter("propertyShowAll", "propertyShouldShow", "propertyValue")
    fun setPropertyVisibility(view: View, showAll: Boolean, shouldShow: Boolean, value: String) {
        val visible = shouldShow && (showAll || !value.isEmpty())
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }
}
