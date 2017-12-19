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

package de.dreier.mytargets.views.selector

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import de.dreier.mytargets.R
import de.dreier.mytargets.databinding.SelectorItemImageDetailsBinding
import de.dreier.mytargets.shared.models.IDetailProvider
import de.dreier.mytargets.shared.models.IImageProvider

abstract class ImageSelectorBase<T> @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        val title: Int? = null
) : SelectorBase<T>(context, attrs, R.layout.selector_item_image_details) where T : IImageProvider, T : Parcelable {

    protected lateinit var binding: SelectorItemImageDetailsBinding

    override fun bindView(item: T) {
        binding = DataBindingUtil.bind(view)
        binding.name.text = item.name
        if (selectedItem is IDetailProvider) {
            binding.details.visibility = View.VISIBLE
            binding.details.text = (item as IDetailProvider).getDetails(context)
        }
        binding.image.setImageDrawable(item.getDrawable(context))
        title?.let {
            binding.title.visibility = View.VISIBLE
            binding.title.setText(it)
        }
    }
}
