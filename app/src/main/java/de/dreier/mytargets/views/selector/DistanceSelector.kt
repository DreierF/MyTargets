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

package de.dreier.mytargets.views.selector

import android.content.Context
import android.util.AttributeSet
import de.dreier.mytargets.R
import de.dreier.mytargets.databinding.SelectorItemDistanceBinding
import de.dreier.mytargets.shared.models.Dimension

class DistanceSelector @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    SelectorBase<Dimension, SelectorItemDistanceBinding>(
        context,
        attrs,
        R.layout.selector_item_distance,
        DISTANCE_REQUEST_CODE
    ) {

    override fun bindView(item: Dimension) {
        view.distanceValue.text = item.toString()
    }

    companion object {
        const val DISTANCE_REQUEST_CODE = 1
    }
}
