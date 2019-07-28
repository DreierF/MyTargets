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
package de.dreier.mytargets.shared.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import de.dreier.mytargets.shared.R
import de.dreier.mytargets.shared.SharedApplicationInstance

enum class EWeather constructor(
        @StringRes private val nameRes: Int,
        @DrawableRes val colorDrawable: Int,
        @DrawableRes private val outlineDrawable: Int,
        @DrawableRes val drawable: Int
) {
    SUNNY(R.string.sunny, R.drawable.ic_sun_48dp, R.drawable.ic_sun_outline_48dp, R.drawable.ic_sun_outline_color_48dp),
    PARTLY_CLOUDY(R.string.partly_cloudy, R.drawable.ic_partly_cloudy_48dp, R.drawable.ic_partly_cloudy_outline_48dp, R.drawable.ic_partly_cloudy_outline_color_48dp),
    CLOUDY(R.string.cloudy, R.drawable.ic_cloudy_48dp, R.drawable.ic_cloudy_outline_48dp, R.drawable.ic_cloudy_outline_color_48dp),
    LIGHT_RAIN(R.string.light_rain, R.drawable.ic_light_rain_48dp, R.drawable.ic_light_rain_outline_48dp, R.drawable.ic_light_rain_outline_color_48dp),
    RAIN(R.string.rain, R.drawable.ic_rain_48dp, R.drawable.ic_rain_outline_48dp, R.drawable.ic_rain_outline_color_48dp);

    fun getName(): String {
        return SharedApplicationInstance.getStr(nameRes)
    }

    fun getDrawable(selected: EWeather): Int {
        return if (selected == this) drawable else outlineDrawable
    }

    companion object {
        fun getOfValue(value: Int) = EWeather.values()[value]
    }
}
