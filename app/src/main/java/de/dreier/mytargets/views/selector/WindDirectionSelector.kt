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
import android.util.AttributeSet

import de.dreier.mytargets.R
import de.dreier.mytargets.features.training.environment.WindDirectionActivity
import de.dreier.mytargets.shared.models.WindDirection

class WindDirectionSelector @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ImageSelectorBase<WindDirection>(context, attrs) {

    init {
        defaultActivity = WindDirectionActivity::class.java
        requestCode = WIND_DIRECTION_REQUEST_CODE
    }

    fun setItemId(direction: Long) {
        setItem(WindDirection.getList(context)[direction.toInt()])
    }

    override fun bindView(item: WindDirection) {
        super.bindView(item)
        setTitle(R.string.wind_direction)
    }

    companion object {
        private val WIND_DIRECTION_REQUEST_CODE = 3
    }
}
