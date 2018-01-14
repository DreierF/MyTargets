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

package de.dreier.mytargets.features.statistics

import android.annotation.SuppressLint
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import de.dreier.mytargets.R
import de.dreier.mytargets.databinding.ViewChipsBinding
import de.dreier.mytargets.shared.models.Thumbnail
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
class Tag @JvmOverloads constructor(
        var id: Long?,
        var text: String,
        var image: Thumbnail? = null,
        var isChecked: Boolean = true
) : Parcelable {

    @IgnoredOnParcel
    val drawable: Drawable? by lazy { image?.roundDrawable }

    fun getView(context: Context, parent: ViewGroup): ViewChipsBinding {
        val binding = DataBindingUtil
                .inflate<ViewChipsBinding>(LayoutInflater.from(context), R.layout.view_chips, parent, false)
        binding.tag = this
        binding.root.isActivated = !isChecked
        val mDensity = context.resources.displayMetrics.density
        binding.root.layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                (CHIP_HEIGHT * mDensity).toInt())
        return binding
    }

    companion object {
        private const val CHIP_HEIGHT = 32 // dp
    }
}
