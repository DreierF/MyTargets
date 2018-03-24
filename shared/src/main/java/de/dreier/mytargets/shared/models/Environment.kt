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

import android.content.Context
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Environment(
    var indoor: Boolean = false,
    var weather: EWeather = EWeather.SUNNY,
    var windSpeed: Int = 0,
    var windDirection: Int = 0,
    var location: String = ""
) : Parcelable {

    fun getWindSpeed(context: Context): String {
        return windSpeed.toString() + " Bft " + WindDirection.getList(context)[windDirection].name
    }

    companion object {
        fun getDefault(indoor: Boolean): Environment {
            return Environment(indoor = indoor)
        }
    }
}
