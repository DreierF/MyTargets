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

package de.dreier.mytargets.features.training.environment

import com.google.gson.annotations.SerializedName
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.shared.models.EWeather
import de.dreier.mytargets.shared.models.Environment
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt

class CurrentWeather {

    @SerializedName("cod")
    var httpCode: Int? = null

    @SerializedName("name")
    var cityName: String = ""

    @SerializedName("weather")
    var weather: List<Weather> = ArrayList()

    @SerializedName("wind")
    var wind: Wind? = null

    private fun mpsToKmh(mps: Double): Double {
        return mps / 0.277777778
    }

    private fun kmhToBeaufort(kmh: Double): Int {
        return (kmh / 3.01).pow(0.666666666).roundToInt()
    }

    fun toEnvironment(): Environment {
        val code = Integer.parseInt(weather[0].icon?.substring(0, 2) ?: "1")
        val e = Environment()
        e.indoor = SettingsManager.indoor
        e.weather = imageCodeToWeather(code)
        e.windDirection = 0
        e.location = cityName
        e.windDirection = 0
        e.windSpeed = kmhToBeaufort(mpsToKmh(wind?.speed ?: 0.0))
        return e
    }

    private fun imageCodeToWeather(code: Int): EWeather {
        return when (code) {
            1 -> EWeather.SUNNY
            2 -> EWeather.PARTLY_CLOUDY
            3, 4 -> EWeather.CLOUDY
            9 -> EWeather.RAIN
            10 -> EWeather.LIGHT_RAIN
            else -> EWeather.CLOUDY
        }
    }

    inner class Wind {
        @SerializedName("speed")
        var speed: Double? = null
    }

    inner class Weather {
        @SerializedName("id")
        var id: Int? = null
        @SerializedName("main")
        var main: String? = null
        @SerializedName("description")
        var description: String? = null
        @SerializedName("icon")
        var icon: String? = null
    }
}


