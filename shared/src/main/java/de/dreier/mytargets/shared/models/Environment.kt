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
package de.dreier.mytargets.shared.models

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.text.TextUtils

import de.dreier.mytargets.shared.R
import de.dreier.mytargets.shared.SharedApplicationInstance
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class Environment(
        var indoor: Boolean = false,
        var weather: EWeather = EWeather.SUNNY,
        var windSpeed: Int = 0,
        var windDirection: Int = 0,
        var location: String = ""
) : Parcelable {

    val colorDrawable: Int
        @DrawableRes
        get() = if (indoor) {
            R.drawable.ic_house_24dp
        } else {
            weather.colorDrawable
        }

    fun getWindSpeed(context: Context): String {
        return windSpeed.toString() + " Bft " + WindDirection.getList(context)[windDirection].name
    }

    companion object {
        fun getDefault(indoor: Boolean): Environment {
            return Environment(indoor = indoor)
        }
    }
}
